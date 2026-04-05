using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Serilog;

namespace Reqtificator.Transformers
{
    internal class ForwardDataFromTemplate<T, TI, TGetter> : Transformer<T, TI, TGetter>
        where T : MajorRecord, TI where TI : class, IMajorRecord, TGetter where TGetter : class, IMajorRecordGetter
    {
        private readonly ILinkCache<ISkyrimMod, ISkyrimModGetter> _linkCache;
        private readonly ImmutableDictionary<ModKey, ImmutableList<ModKey>> _masters;
        private readonly IDataForwardingLogic<T, TGetter> _logic;
        private readonly bool _featureActive;

        public ForwardDataFromTemplate(ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache,
            ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder,
            IDataForwardingLogic<T, TGetter> processingLogic, bool featureActive)
        {
            _linkCache = linkCache;
            _logic = processingLogic;
            _featureActive = featureActive;
            _masters = loadOrder.PriorityOrder
                .Select(x =>
                    KeyValuePair.Create(x.ModKey, x.Mod!.MasterReferences.Select(y => y.Master).ToImmutableList()))
                .ToImmutableDictionary();
        }

        public override TransformationResult<T, TGetter> Process(TransformationResult<T, TGetter> input)
        {
            if (!_featureActive)
            {
                return input;
            }

            var lastOverride = _linkCache.ResolveContext<T, TGetter>(input.Record().FormKey);
            var otherVersions = _linkCache.ResolveAllContexts<T, TGetter>(input.Record().FormKey).Skip(1)
                .ToImmutableList();

            if (IsTemplate(lastOverride, otherVersions))
            {
                Log.Debug($"last override \"{lastOverride.ModKey}\" is a visual template");
                return input;
            }

            var template = otherVersions.FirstOrDefault(x => IsTemplate(x, otherVersions));
            if (template != null && !_logic.CheckRecordEquality(lastOverride.Record, template.Record))
            {
                //TODO: refine this log message if the code is used for other features in the future 
                Log.Information($"applying visual auto-merge: \"{template.ModKey}\" (visual) & \"{lastOverride.ModKey}\" (data)");
                return input.Modify(record => _logic.ForwardDataFromTemplate(record, template.Record));
            }

            return input;
        }

        private bool IsTemplate(IModContext<ISkyrimMod, ISkyrimModGetter, T, TGetter> thisVersion,
            ImmutableList<IModContext<ISkyrimMod, ISkyrimModGetter, T, TGetter>> otherVersions)
        {
            var maybePreviousVersion =
                otherVersions.FirstOrDefault(x => _masters[thisVersion.ModKey].Contains(x.ModKey));

            if (maybePreviousVersion == null)
            {
                return false;
            }

            return !_logic.CheckRecordEquality(thisVersion.Record, maybePreviousVersion.Record);
        }
    }

    internal interface IDataForwardingLogic<in T1, in T1Getter>
    {
        public bool CheckRecordEquality(T1Getter reference, T1Getter other);

        public void ForwardDataFromTemplate(T1 record, T1Getter templateRecord);
    }
}