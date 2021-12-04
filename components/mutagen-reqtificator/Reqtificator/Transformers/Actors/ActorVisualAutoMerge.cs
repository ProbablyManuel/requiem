using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Serilog;

namespace Reqtificator.Transformers.Actors
{
    internal class ActorVisualAutoMerge : TransformerV2<Npc, INpcGetter>
    {
        private readonly bool _featureActive;
        private readonly ILinkCache<ISkyrimMod, ISkyrimModGetter> _linkCache;
        private readonly ILoadOrder<IModListing<ISkyrimModGetter>> _loadOrder;
        private readonly ImmutableDictionary<ModKey, ImmutableList<ModKey>> _masters;

        public ActorVisualAutoMerge(ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache,
            ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder, bool featureActive)
        {
            _linkCache = linkCache;
            _loadOrder = loadOrder;
            _featureActive = featureActive;
            _masters = loadOrder.PriorityOrder
                .Select(x =>
                    KeyValuePair.Create(x.ModKey, x.Mod!.MasterReferences.Select(y => y.Master).ToImmutableList()))
                .ToImmutableDictionary();
        }

        private bool IsVisualTemplate(IModContext<ISkyrimMod, ISkyrimModGetter, Npc, INpcGetter> thisVersion,
            ImmutableList<IModContext<ISkyrimMod, ISkyrimModGetter, Npc, INpcGetter>> otherVersions)
        {
            var maybePreviousVersion =
                otherVersions.FirstOrDefault(x => _masters[thisVersion.ModKey].Contains(x.ModKey));

            if (maybePreviousVersion == null) return false;

            return !(maybePreviousVersion.Record.Equals(thisVersion.Record, ActorCopyTools.InheritTraitsMask()) &&
                     maybePreviousVersion.Record.Equals(thisVersion.Record, ActorCopyTools.InheritAttackDataMask()));
        }

        private static TransformationResult<Npc, INpcGetter> MergeTemplates(INpcGetter dataTemplate,
            INpcGetter visualTemplate)
        {
            var mutable = dataTemplate.DeepCopy();
            ActorCopyTools.CopyDataForTemplateFlag(mutable, visualTemplate, NpcConfiguration.TemplateFlag.Traits);
            ActorCopyTools.CopyDataForTemplateFlag(mutable, visualTemplate, NpcConfiguration.TemplateFlag.AttackData);
            return new Modified<Npc, INpcGetter>(mutable);
        }

        public override TransformationResult<Npc, INpcGetter> Process(TransformationResult<Npc, INpcGetter> input)
        {
            if (input is not UnChanged<Npc, INpcGetter>)
                throw new ArgumentException("input should not be transformed yet");
            if (!_featureActive) return input;

            var lastOverride = _linkCache.ResolveContext<Npc, INpcGetter>(input.Record().FormKey);
            var otherVersions = _linkCache.ResolveAllContexts<Npc, INpcGetter>(input.Record().FormKey).Skip(1)
                .ToImmutableList();

            var visualTemplate = otherVersions.FirstOrDefault(x => IsVisualTemplate(x, otherVersions));
            if (visualTemplate != null)
            {
                Log.Information(
                    $"applying visual automerge: {visualTemplate.ModKey} (visual) & {lastOverride.ModKey} (data)");
                return MergeTemplates(lastOverride.Record, visualTemplate.Record);
            }

            return input;
        }
    }
}