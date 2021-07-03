using System;
using System.IO;
using System.Linq;
using System.Runtime.CompilerServices;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Serilog;

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
        private static readonly ModKey RequiemModKey = new ModKey("Requiem", ModType.Plugin);

        private readonly InternalEvents _events;

        public Backend(InternalEvents eventsQueue)
        {
            _events = eventsQueue;
            WarmupSkyrim.Init();
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
            try
            {
                var loadOrder = LoadOrder.Import<ISkyrimModGetter>(context.DataFolder, context.ActiveMods, release);
                //TODO: update base folder for configurations if needed
                var configFolder = Path.Combine(context.DataFolder, "SkyProc Patchers", "Requiem", "Config");
                var reqtificatorConfig = ReqtificatorConfig.LoadFromConfigs(configFolder, loadOrder);

                Log.Information("start patching");

                return MainLogic.GeneratePatch(loadOrder, userConfig, reqtificatorConfig, patchModKey);
            }
            catch (Exception ex)
            {
                Log.Error(ex, "things did not go according to plan");
                _events.ReportException(ex);
                //TODO: replace this throw with proper GUI-sided error handling
                throw;
            }
        }
    }
}