using System;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Hocon;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Mutagen.Bethesda.Synthesis;
using Reqtificator.Export;
using Reqtificator.Transformers;
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
        private static readonly string _logFileName = "Reqtificator.log";

        public static async Task<int> Main(string[] args)
        {
            File.Delete(_logFileName);

            var logSwitch = new LoggingLevelSwitch();

            Log.Logger = new LoggerConfiguration()
                .MinimumLevel.Debug()
                .WriteTo.File(_logFileName, levelSwitch: logSwitch)
                .CreateLogger();

            Log.Information("hello Requiem on info level debugging!");
            Log.Debug("You should never have seen this line! :o");

            logSwitch.MinimumLevel = LogEventLevel.Debug;

            Log.Debug("now let's get serious and switch to debug mode at runtime!");

            var config = HoconConfigurationFactory.FromFile("components/mutagen-reqtificator/Reqtificator.conf");
            Log.Debug($"maxlevel: {config.GetInt("maxLevel")}");
            Log.Debug($"subNode.omg: {config.GetString("subNode.omg")}");

            return await SynthesisPipeline.Instance
                .AddPatch<ISkyrimMod, ISkyrimModGetter>(RunPatch)
                .SetTypicalOpen(GameRelease.SkyrimSE, "Requiem for the Mutated.esp")
                .Run(args);
        }

        public static void RunPatch(IPatcherState<ISkyrimMod, ISkyrimModGetter> state)
        {
            //TODO: refactor this into a nice verification function
            var requiemModKey = new ModKey("Requiem", ModType.Plugin);

            if (state.LoadOrder.PriorityOrder.All(x => x.ModKey != requiemModKey))
            {
                Console.WriteLine("oops, where's Requiem.esp? -- hit enter to abort the patcher");
                Console.ReadLine();
                return;
            }

            var ammoRecords = state.LoadOrder.PriorityOrder.Ammunition().WinningOverrides();
            var ammoPatched = new AmmunitionTransformer().ProcessCollection(ammoRecords);

            foreach (var patched in ammoPatched)
            {
                state.PatchMod.Ammunitions.Add(patched);
            }

            var requiem = state.LoadOrder.PriorityOrder.First(x => x.ModKey == requiemModKey);

            var version = new RequiemVersion(5, 0, 0, "a Phoenix perhaps?");
            var processor = new PatchData();
            processor.SetPatchHeadersAndVersion(requiem.Mod!, state.PatchMod, version);

            Log.CloseAndFlush();
            Console.WriteLine("Done! Press enter to finish your self-compiled Mutagen patcher.");
            Console.ReadLine();
        }
    }
}