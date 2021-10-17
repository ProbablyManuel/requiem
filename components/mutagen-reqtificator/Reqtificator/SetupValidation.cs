using System;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Events;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator
{
    internal static class SetupValidation
    {
        private static class Uris
        {
            public static readonly Uri EngineFixes = new("https://www.nexusmods.com/skyrimspecialedition/mods/17230");
            public static readonly Uri ScrambledBugs = new("https://www.nexusmods.com/skyrimspecialedition/mods/43532");
        }

        public static ReqtificatorFailure? VerifySetup(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder, RequiemVersion patcherVersion)
        {
            if (!System.IO.File.Exists("SKSE/Plugins/EngineFixes.dll"))
            {
                return new MissingBugfixDependency("SSE Engine Fixes", "NexusMods", Uris.EngineFixes);
            }
            if (!System.IO.File.Exists("DLLPlugins/ScrambledBugs.dll"))
            {
                return new MissingBugfixDependency("Scrambled Bugs", "NexusMods", Uris.ScrambledBugs);
            }

            int pluginVersion = (int)((IGlobalIntGetter)loadOrder.ToImmutableLinkCache()
                .Resolve<IGlobalGetter>(GlobalVariables.VersionStampPlugin.FormKey)).Data!;
            if (patcherVersion.AsNumeric() != pluginVersion)
            {
                return new VersionMismatch(new RequiemVersion(pluginVersion), patcherVersion);
            }

            return null;
        }
    }
}