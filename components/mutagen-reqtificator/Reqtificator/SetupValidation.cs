using System;
using System.Collections.Generic;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Events;
using Reqtificator.StaticReferences;

namespace Reqtificator
{
    internal static class SetupValidation
    {
        private static class Uris
        {
            public static readonly Uri EngineFixes = new("https://www.nexusmods.com/skyrimspecialedition/mods/17230");
            public static readonly Uri ScrambledBugs = new("https://www.nexusmods.com/skyrimspecialedition/mods/43532");
        }

        public static ReqtificatorOutcome? VerifySetup(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder, RequiemVersion patcherVersion)
        {
            var missingDependencies = new List<BugfixDependency>()
            {
                new BugfixDependency("SSE Engine Fixes", "SKSE/Plugins/EngineFixes.dll", "NexusMods", Uris.EngineFixes),
                new BugfixDependency("Scrambled Bugs", "DLLPlugins/ScrambledBugs.dll", "NexusMods", Uris.ScrambledBugs)
            }.FindAll(d => !System.IO.File.Exists(d.ExpectedLocation));

            if (missingDependencies.Count > 0) { return new MissingBugfixDependency(missingDependencies); }

            int pluginVersion = (int)((IGlobalIntGetter)loadOrder.ToImmutableLinkCache()
                .Resolve<IGlobalGetter>(GlobalVariables.VersionStampPlugin.FormKey)).Data!;

            return patcherVersion.AsNumeric() != pluginVersion ? new VersionMismatch(new RequiemVersion(pluginVersion), patcherVersion) : null;
        }
    }
}