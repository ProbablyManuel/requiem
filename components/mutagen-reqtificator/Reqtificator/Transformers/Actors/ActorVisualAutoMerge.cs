using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Serilog;

namespace Reqtificator.Transformers.Actors
{
    internal class ActorVisualAutoMerge : TransformerV2<Npc, INpcGetter>
    {
        private readonly bool _featureActive;
        private readonly ILinkCache<ISkyrimMod, ISkyrimModGetter> _linkCache;
        private readonly ImmutableDictionary<ModKey, ImmutableList<ModKey>> _masters;
        private readonly Npc.TranslationMask _compareTraitsMask;

        public ActorVisualAutoMerge(ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache,
            ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder, bool featureActive)
        {
            _linkCache = linkCache;
            _featureActive = featureActive;
            _masters = loadOrder.PriorityOrder
                .Select(x =>
                    KeyValuePair.Create(x.ModKey, x.Mod!.MasterReferences.Select(y => y.Master).ToImmutableList()))
                .ToImmutableDictionary();
            _compareTraitsMask = ActorCopyTools.InheritTraitsMask();
            _compareTraitsMask.TintLayers = false; // changes here are usually Creation Kit artifacts
            _compareTraitsMask.TextureLighting = false; // changes here are usually Creation Kit artifacts
            _compareTraitsMask.HeadParts = false; // custom comparison needed, as order of elements is irrelevant
            _compareTraitsMask.FaceMorph = false; // can contain small numeric differences that are irrelevant
        }

        private bool IsVisualTemplate(IModContext<ISkyrimMod, ISkyrimModGetter, Npc, INpcGetter> thisVersion,
            ImmutableList<IModContext<ISkyrimMod, ISkyrimModGetter, Npc, INpcGetter>> otherVersions)
        {
            if ((thisVersion.Record.Configuration.TemplateFlags & NpcConfiguration.TemplateFlag.Traits) != 0)
                return false;
            var maybePreviousVersion =
                otherVersions.FirstOrDefault(x => _masters[thisVersion.ModKey].Contains(x.ModKey));

            if (maybePreviousVersion == null) return false;

            return !EqualsVisualData(thisVersion.Record, maybePreviousVersion.Record);
        }

        private bool EqualsVisualData(INpcGetter reference, INpcGetter other)
        {
            return reference.Equals(other, _compareTraitsMask) &&
                   CompareHeadParts(other, reference) &&
                   CompareFaceMorphs(other, reference);
        }

        private static bool CompareHeadParts(INpcGetter reference, INpcGetter other)
        {
            var thisParts = reference.HeadParts.Select(x => x.FormKey).ToImmutableHashSet();
            var otherParts = other.HeadParts.Select(x => x.FormKey).ToImmutableHashSet();
            return thisParts.SetEquals(otherParts);
        }

        private static bool CompareFaceMorphs(INpcGetter reference, INpcGetter other)
        {
            var refMorph = reference.FaceMorph;
            var otherMorph = other.FaceMorph;
            var tolerance = 0.0001f;

            if (refMorph == null && otherMorph == null) return true;
            if (refMorph == null || otherMorph == null) return false;

            return Math.Abs(refMorph.BrowsForwardVsBack - otherMorph.BrowsForwardVsBack) < tolerance &&
                   Math.Abs(refMorph.BrowsInVsOut - otherMorph.BrowsInVsOut) < tolerance &&
                   Math.Abs(refMorph.BrowsUpVsDown - otherMorph.BrowsUpVsDown) < tolerance &&
                   Math.Abs(refMorph.CheeksForwardVsBack - otherMorph.CheeksForwardVsBack) < tolerance &&
                   Math.Abs(refMorph.CheeksUpVsDown - otherMorph.CheeksUpVsDown) < tolerance &&
                   Math.Abs(refMorph.ChinNarrowVsWide - otherMorph.ChinNarrowVsWide) < tolerance &&
                   Math.Abs(refMorph.ChinUnderbiteVsOverbite - otherMorph.ChinUnderbiteVsOverbite) < tolerance &&
                   Math.Abs(refMorph.ChinUpVsDown - otherMorph.ChinUpVsDown) < tolerance &&
                   Math.Abs(refMorph.EyesForwardVsBack - otherMorph.EyesForwardVsBack) < tolerance &&
                   Math.Abs(refMorph.EyesInVsOut - otherMorph.EyesInVsOut) < tolerance &&
                   Math.Abs(refMorph.EyesUpVsDown - otherMorph.EyesUpVsDown) < tolerance &&
                   Math.Abs(refMorph.JawForwardVsBack - otherMorph.JawForwardVsBack) < tolerance &&
                   Math.Abs(refMorph.JawNarrowVsWide - otherMorph.JawNarrowVsWide) < tolerance &&
                   Math.Abs(refMorph.JawUpVsDown - otherMorph.JawUpVsDown) < tolerance &&
                   Math.Abs(refMorph.LipsInVsOut - otherMorph.LipsInVsOut) < tolerance &&
                   Math.Abs(refMorph.LipsUpVsDown - otherMorph.LipsUpVsDown) < tolerance &&
                   Math.Abs(refMorph.NoseLongVsShort - otherMorph.NoseLongVsShort) < tolerance &&
                   Math.Abs(refMorph.NoseUpVsDown - otherMorph.NoseUpVsDown) < tolerance;
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

            if (IsVisualTemplate(lastOverride, otherVersions))
            {
                if (lastOverride.ModKey == new ModKey("Requiem", ModType.Plugin))
                    Log.Warning("found visual template in Requiem");
                return input;
            }

            var visualTemplate = otherVersions.FirstOrDefault(x => IsVisualTemplate(x, otherVersions));
            if (visualTemplate != null && !EqualsVisualData(lastOverride.Record, visualTemplate.Record))
            {
                Log.Information(
                    $"applying visual automerge: {visualTemplate.ModKey} (visual) & {lastOverride.ModKey} (data)");
                return MergeTemplates(lastOverride.Record, visualTemplate.Record);
            }

            return input;
        }
    }
}