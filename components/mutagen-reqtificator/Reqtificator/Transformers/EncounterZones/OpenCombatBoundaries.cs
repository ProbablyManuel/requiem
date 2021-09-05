using System.Collections.Generic;
using System.Collections.Immutable;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator.Transformers.EncounterZones
{
    internal class OpenCombatBoundaries : TransformerV2<EncounterZone, IEncounterZoneGetter>
    {
        private readonly IReadOnlySet<IFormLink<IEncounterZoneGetter>> _alwaysClosedZones;
        private readonly bool _openCombatZones;

        internal OpenCombatBoundaries(IReadOnlySet<IFormLink<IEncounterZoneGetter>> alwaysClosedZones,
            bool openCombatZones)
        {
            _alwaysClosedZones = alwaysClosedZones;
            _openCombatZones = openCombatZones;
        }

        public OpenCombatBoundaries(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder, UserSettings userConfig)
        {
            var linkCache = loadOrder.ToImmutableLinkCache();
            var exclusions = ImmutableHashSet<IFormLink<IEncounterZoneGetter>>.Empty;
            foreach (var recordVersion in FormLists.ClosedEncounterZones.ResolveAll(linkCache))
            {
                foreach (var entry in recordVersion.Items)
                {
                    if (entry.TryResolve<IEncounterZoneGetter>(linkCache, out var zone))
                    {
                        exclusions = exclusions.Add(zone.AsLink());
                    }
                    else
                    {
                        //TODO: error handling strategy: throw or log? fail fast or collect and aggregate as events?
                    }
                }
            }

            _alwaysClosedZones = exclusions;
            _openCombatZones = userConfig.OpenEncounterZones;
        }

        public override TransformationResult<EncounterZone, IEncounterZoneGetter> Process(TransformationResult<EncounterZone, IEncounterZoneGetter> input)
        {
            if (!_openCombatZones || _alwaysClosedZones.Contains(new FormLink<IEncounterZoneGetter>(input.Record()))) { return input; }

            var result = input.Modify(record => record.Flags |= EncounterZone.Flag.DisableCombatBoundary);
            Log.Debug("opened combat boundaries of encounter zone");
            return result;
        }
    }
}