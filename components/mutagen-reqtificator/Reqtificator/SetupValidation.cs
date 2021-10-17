using System;
using Reqtificator.Events;

namespace Reqtificator
{
    internal static class SetupValidation
    {
        private static class Uris
        {
            public static readonly Uri EngineFixes = new("https://www.nexusmods.com/skyrimspecialedition/mods/17230");
            public static readonly Uri ScrambledBugs = new("https://www.nexusmods.com/skyrimspecialedition/mods/43532");
        }

        public static ReqtificatorFailure? VerifySetup()
        {
            if (!System.IO.File.Exists("SKSE/Plugins/EngineFixes.dll"))
            {
                return new MissingBugfixDependency("SSE Engine Fixes", "NexusMods", Uris.EngineFixes);
            }
            if (!System.IO.File.Exists("DLLPlugins/ScrambledBugs.dll"))
            {
                return new MissingBugfixDependency("Scrambled Bugs", "NexusMods", Uris.ScrambledBugs);
            }

            return null;
        }
    }
}