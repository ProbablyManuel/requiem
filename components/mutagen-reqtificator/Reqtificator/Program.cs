using System;
using System.Collections.Immutable;
using System.IO;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Mutagen.Bethesda.Synthesis.Internal;
using Noggog;
using Serilog;
using Serilog.Core;
using Serilog.Events;

//TODO: figure out why I need to do this when adding Mutagen as a dependency
namespace System.Runtime.CompilerServices
{
    internal record IsExternalInit;
}

namespace Reqtificator
{
    public class Program
    {
        private const string LogFileName = "Reqtificator.log";
        private const GameRelease Release = GameRelease.SkyrimSE;
        private static readonly ModKey PatchModKey = ModKey.FromNameAndExtension("Requiem for the Mutilated.esp");
        private static readonly ModKey RequiemModKey = new ModKey("Requiem", ModType.Plugin);

        public static int Main(string[] args)
        {
            File.Delete(LogFileName);
            var logSwitch = new LoggingLevelSwitch(LogEventLevel.Debug);
            Log.Logger = new LoggerConfiguration()
                .MinimumLevel.Debug()
                .WriteTo.File(LogFileName, levelSwitch: logSwitch)
                .CreateLogger();

            Console.WriteLine("starting the Reqtificator");
            Log.Information("starting the Reqtificator");
            WarmupSkyrim.Init();

            // var config = HoconConfigurationFactory.FromFile("components/mutagen-reqtificator/Reqtificator.conf");

            if (!GameLocations.TryGetGameFolder(Release, out var gameFolder))
            {
                throw new DirectoryNotFoundException("Could not locate game folder automatically.");
            }

            var dataFolder = Path.Combine(gameFolder, "Data");
            if (!PluginListings.TryGetListingsFile(Release, out var path))
            {
                throw new FileNotFoundException("Could not locate load order automatically.");
            }

            var loadOrderEntries = Utility.GetLoadOrder(Release, path.Path, dataFolder);
            var activeMods = loadOrderEntries.OnlyEnabled().TakeWhile(it => it != PatchModKey).ToImmutableList();
            Log.Information("Active Load Order:");
            foreach (var (index, mod) in activeMods.WithIndex())
            {
                Log.Information($"  {index:D3} - {mod.ModKey.FileName}");
            }

            var loadOrder = LoadOrder.Import<ISkyrimModGetter>(dataFolder, activeMods, Release);

            //TODO: refactor this into a nice verification function
            if (loadOrder.PriorityOrder.All(x => x.ModKey != RequiemModKey))
            {
                Console.WriteLine("oops, where's Requiem.esp? -- hit enter to abort the patcher");
                Console.ReadLine();
                return 1;
            }

            Log.Information("start patching");
            var generatedPatch = MainLogic.GeneratePatch(loadOrder, PatchModKey);
            Log.Information("done patching, now exporting to disk");
            MainLogic.WritePatchToDisk(generatedPatch, dataFolder);
            Log.Information("done exporting");

            Log.CloseAndFlush();
            Console.WriteLine("Done! Press enter to finish your self-compiled Mutagen patcher.");
            Console.ReadLine();
            return 0;
        }
    }
}