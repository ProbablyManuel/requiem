using System;
using System.IO;
using System.Linq;
using System.Runtime.CompilerServices;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Serilog;

[assembly: CLSCompliant(false)]
[assembly: InternalsVisibleTo("ReqtificatorTest")]

//TODO: figure out why I need to do this when adding Mutagen as a dependency
namespace System.Runtime.CompilerServices
{
    public record IsExternalInit;
}

namespace Reqtificator
{
    internal static class Program
    {
        private const GameRelease Release = GameRelease.SkyrimSE;
        private static readonly ModKey PatchModKey = ModKey.FromNameAndExtension("Requiem for the Mutated.esp");
        private static readonly ModKey RequiemModKey = new ModKey("Requiem", ModType.Plugin);

        public static int Main()
        {
            LogUtils.SetUpLogging();

            Console.WriteLine("starting the Reqtificator with console logging enabled");
            Log.Information("starting the Reqtificator");
            WarmupSkyrim.Init();

            var context = GameContext.GetRequiemContext(Release, PatchModKey);
            var userConfigFile = Path.Combine(context.DataFolder, "Reqtificator", "UserSettings.json");
            var userConfig = UserSettings.LoadUserSettings(userConfigFile);

            //TODO: refactor this into a nice verification function
            if (context.ActiveMods.All(x => x.ModKey != RequiemModKey))
            {
                Console.WriteLine($"oops, where's {RequiemModKey.FileName}? -- hit enter to abort the patcher");
                Console.ReadLine();
                return 1;
            }

            // @Ludo hook your logic here, you have the active mods in the context record but no data loaded yet

            // TODO: this is just demonstration, the changes should be done in the UI and persisted into the same file
            Log.Information("loaded user configuration {@config}", userConfig);
            var updatedUserConfig = userConfig with
            {
                VerboseLogging = !userConfig.VerboseLogging,
                NpcVisualTemplateMods = userConfig.RaceVisualTemplateMods,
                RaceVisualTemplateMods = userConfig.NpcVisualTemplateMods
            };
            updatedUserConfig.WriteToFile(Path.Combine(context.DataFolder, "Reqtificator", "UserSettingsUpdated.json"));

            var loadOrder = LoadOrder.Import<ISkyrimModGetter>(context.DataFolder, context.ActiveMods, Release);

            Log.Information("start patching");
            var generatedPatch = MainLogic.GeneratePatch(loadOrder, PatchModKey);
            Log.Information("done patching, now exporting to disk");
            MainLogic.WritePatchToDisk(generatedPatch, context.DataFolder);
            Log.Information("done exporting");

            Log.CloseAndFlush();
            Console.WriteLine("Done! Press enter to finish your self-compiled Mutagen patcher.");
            Console.ReadLine();
            return 0;
        }
    }
}