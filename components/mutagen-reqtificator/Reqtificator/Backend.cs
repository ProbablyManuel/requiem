using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Serilog;
using System;
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
    internal class Backend
    {
        private const GameRelease Release = GameRelease.SkyrimSE;
        private static readonly ModKey PatchModKey = ModKey.FromNameAndExtension("Requiem for the Mutated.esp");
        private static readonly ModKey RequiemModKey = new ModKey("Requiem", ModType.Plugin);

        private readonly InternalEvents _events;

        public Backend(InternalEvents eventsQueue)
        {
            _events = eventsQueue;
            WarmupSkyrim.Init();

            var context = GameContext.GetRequiemContext(Release, PatchModKey);
            var userConfig = LoadAndVerifyUserSettings(context);

            _events.PublishReadyToPatch(userConfig, context.ActiveMods.Select(x => x.ModKey));

            _events.PatchRequested += (_, updatedSettings) =>
            {
                updatedSettings.WriteToFile(Path.Combine(context.DataFolder, "Reqtificator", "UserSettings.json"));
                var generatedPatch = GeneratePatch(context, updatedSettings, Release, PatchModKey);
                Log.Information("done patching, now exporting to disk");
                MainLogic.WritePatchToDisk(generatedPatch, context.DataFolder);
                Log.Information("done exporting");
                _events.PublishPatchCompleted();
            };
        }

        public UserSettings LoadAndVerifyUserSettings(GameContext context)
        {
            var userConfigFile = Path.Combine(context.DataFolder, "Reqtificator", "UserSettings.json");
            var userConfig = UserSettings.LoadUserSettings(userConfigFile);

            //TODO: refactor this into a nice verification function & better error handling
            if (context.ActiveMods.All(x => x.ModKey != RequiemModKey))
            {
                Log.Error("oops, where's your Requiem.esp?");
                var ex = new NotImplementedException("no nice error handling for missing Requiem.esp :)");
                _events.ReportException(ex);
                throw ex;
            }

            return userConfig;
        }


        public SkyrimMod GeneratePatch(GameContext context, UserSettings userConfig, GameRelease release,
            ModKey patchModKey)
        {
            var loadOrder = LoadOrder.Import<ISkyrimModGetter>(context.DataFolder, context.ActiveMods, release);

            Log.Information("start patching");
            try
            {
                return MainLogic.GeneratePatch(loadOrder, userConfig, _events, patchModKey);
            }
            catch (Exception ex)
            {
                _events.ReportException(ex);
                throw;
            }
        }
    }
}