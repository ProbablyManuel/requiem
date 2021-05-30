using System.Collections.Generic;
using System.Collections.Immutable;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator.Transformers.EncounterZones
{
    internal record OpenCombatBoundaries
        (IReadOnlySet<IFormLink<EncounterZone>> AlwaysClosedZones, bool OpenCombatZones) : Transformer<EncounterZone,
            IEncounterZone, IEncounterZoneGetter>
    {
        public override bool ShouldProcess(IEncounterZoneGetter record)
        {
            return OpenCombatZones && !AlwaysClosedZones.Contains(record.AsLink<EncounterZone>());
        }

        public override void Process(IEncounterZone record)
        {
            record.Flags |= EncounterZone.Flag.DisableCombatBoundary;
            Log.Debug("opened combat boundaries of encounter zone");
        }


        public static OpenCombatBoundaries Create(LoadOrder<IModListing<ISkyrimModGetter>> loadOrder,
            UserSettings userConfig)
        {
            var linkCache = loadOrder.ToImmutableLinkCache();
            var exclusions = ImmutableHashSet<IFormLink<EncounterZone>>.Empty;
            foreach (var recordVersion in FormLists.ClosedEncounterZones.ResolveAll(linkCache))
            {
                foreach (var entry in recordVersion.Items)
                {
                    if (entry.TryResolve<EncounterZone>(linkCache, out var foo))
                    {
                        exclusions = exclusions.Add(foo.AsLink());
                    }
                    else
                    {
                        //TODO: error handling strategy: throw or log? fail fast or collect and aggregate as events?
                    }
                }
            }

            return new OpenCombatBoundaries(exclusions, userConfig.OpenEncounterZones);
        }
    }
}