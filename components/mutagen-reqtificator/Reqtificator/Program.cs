using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Serilog;
using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.IO;
using System.Linq;
using System.Runtime.CompilerServices;

[assembly: CLSCompliant(false)]
[assembly: InternalsVisibleTo("ReqtificatorTest")]
[assembly: InternalsVisibleTo("DynamicProxyGenAssembly2")] //for mocking internal entities


//TODO: figure out why I need to do this when adding Mutagen as a dependency
namespace System.Runtime.CompilerServices
{
    public record IsExternalInit;
}

namespace Reqtificator
{
    internal class Program
    {
        private const GameRelease Release = GameRelease.SkyrimSE;
        private static readonly ModKey PatchModKey = ModKey.FromNameAndExtension("Requiem for the Mutated.esp");
        private static readonly ModKey RequiemModKey = new ModKey("Requiem", ModType.Plugin);

        private readonly InternalEvents _events;

        public Program(InternalEvents eventsQueue)
        {
            _events = eventsQueue;
        }

        public int SetUpPatcher()
        {

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

            _events.PatchRequested += (s, usea) => { Patch(context, usea); };
            _events.NotifyUserOptionsChanged(new OptionsEventArgs(userConfig.VerboseLogging, userConfig.MergeLeveledCharacters, userConfig.MergeLeveledLists, userConfig.OpenEncounterZones));
            _events.NotifyLoadOrderSettingsChanged(new LoadOrderSettingsEventArgs(ToModKeys(context.ActiveMods), userConfig.NpcVisualTemplateMods.ToList(), userConfig.RaceVisualTemplateMods.ToList()));
            return 0;
        }

        private static List<ModKey> ToModKeys(ImmutableList<LoadOrderListing> mods)
        {
            return mods.Select(m => m.ModKey).ToList();
        }

        private void Patch(GameContext context, UserSettings userConfig)
        {

            userConfig.WriteToFile(Path.Combine(context.DataFolder, "Reqtificator", "UserSettings.json"));

            var loadOrder = LoadOrder.Import<ISkyrimModGetter>(context.DataFolder, context.ActiveMods, Release);

            Log.Information("start patching");
            var generatedPatch = MainLogic.GeneratePatch(loadOrder, userConfig, PatchModKey);
            Log.Information("done patching, now exporting to disk");
            MainLogic.WritePatchToDisk(generatedPatch, context.DataFolder);
            Log.Information("done exporting");

            _events.PublishPatchCompleted();
            Log.CloseAndFlush();
            Console.WriteLine("Done! Press enter to finish your self-compiled Mutagen patcher.");
            Console.ReadLine();
        }
    }
}