using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.IO;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Events;
using Reqtificator.Events.Outcomes;
using Reqtificator.Exceptions;
using Reqtificator.StaticReferences;

namespace Reqtificator
{
    internal static class SetupValidation
    {
        private static class Uris
        {
            public static readonly Uri BugFixes = new("https://www.nexusmods.com/skyrimspecialedition/mods/33261");
            public static readonly Uri EngineFixes = new("https://www.nexusmods.com/skyrimspecialedition/mods/17230");
            public static readonly Uri ScrambledBugs = new("https://www.nexusmods.com/skyrimspecialedition/mods/43532");
        }

        public static ReqtificatorOutcome? VerifySetup(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder,
            RequiemVersion patcherVersion)
        {
            var missingDependencies = new List<BugfixDependency>()
            {
                new BugfixDependency("Bug Fixes", "SKSE/Plugins/BugFixesSSE.dll", "NexusMods", Uris.BugFixes),
                new BugfixDependency("Engine Fixes", "SKSE/Plugins/EngineFixes.dll", "NexusMods", Uris.EngineFixes),
                new BugfixDependency("Scrambled Bugs", "SKSE/Plugins/ScrambledBugs.dll", "NexusMods", Uris.ScrambledBugs)
            }.FindAll(d => !File.Exists(d.ExpectedLocation));

            if (missingDependencies.Count > 0)
            {
                return new MissingBugfixDependency(missingDependencies);
            }

            int pluginVersion = (int)((IGlobalIntGetter)loadOrder.ToImmutableLinkCache()
                .Resolve<IGlobalGetter>(GlobalVariables.VersionStampPlugin.FormKey)).Data!;

            if (patcherVersion.AsNumeric() != pluginVersion)
            {
                return new VersionMismatch(new RequiemVersion(pluginVersion), patcherVersion);
            }

            return ValidateLoadOrder(loadOrder);
        }

        private static ReqtificatorOutcome? ValidateLoadOrder(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder)
        {
            var modList = loadOrder.Select(e => e.Key).ToImmutableList();
            return loadOrder
                .SelectMany<IKeyValue<ModKey, IModListing<ISkyrimModGetter>>, ReqtificatorOutcome>((elem, index) =>
                    {
                        if (elem.Value.Mod is null)
                        {
                            return ReqtificatorFailure.CausedBy(new ModFromLoadOrderNotFoundException(elem.Key))
                                .AsEnumerable();
                        }

                        var missingMasters = elem.Value.Mod!.MasterReferences
                            .Where(e => modList.Take(index).ContainsNot(e.Master))
                            .Select(e => e.Master)
                            .ToImmutableList();
                        if (missingMasters.IsEmpty)
                        {
                            return ImmutableList.Create<InvalidLoadOrder>();
                        }

                        var missingMods = missingMasters.Where(e => !File.Exists(e.FileName)).ToImmutableHashSet();
                        var outOfOrder = missingMasters.Where(e => modList.Contains(e)).ToImmutableHashSet();
                        var disabledMods = missingMasters.Where(e => missingMods.ContainsNot(e) && outOfOrder.ContainsNot(e)).ToImmutableHashSet();

                        return new InvalidLoadOrder(elem.Key, outOfOrder, missingMods, disabledMods).AsEnumerable();
                    }
                )
                .FirstOrDefault();
        }
    }
}