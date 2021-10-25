using System;
using System.IO;
using System.Linq;
using System.Resources;
using System.Runtime.CompilerServices;
using Hocon;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Reqtificator.Events;
using Serilog;
using Serilog.Events;

[assembly: CLSCompliant(false)]
[assembly: InternalsVisibleTo("ReqtificatorTest")]
[assembly: InternalsVisibleTo("DynamicProxyGenAssembly2")] //for mocking internal entities
[assembly: NeutralResourcesLanguage("en-US")]


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
        private static readonly ModKey PatchModKey = ModKey.FromNameAndExtension("Requiem for the Indifferent.esp");
        private static readonly ModKey RequiemModKey = new ModKey("Requiem", ModType.Plugin);

        private readonly InternalEvents _events;
        private readonly MainLogicExecutor _executor;
        private readonly ReqtificatorLogContext _logs;
        private readonly GameContext _context;
        private readonly RequiemVersion _version;

        public Backend(InternalEvents eventsQueue, ReqtificatorLogContext logContext)
        {
            _events = eventsQueue;
            _logs = logContext;
            WarmupSkyrim.Init();

            _context = GameContext.GetRequiemContext(Release, PatchModKey);
            //TODO: update base folder for configurations if needed
            var configFolder = Path.Combine(_context.DataFolder, "Reqtificator", "Config");
            var reqtificatorConfig = ReqtificatorConfig.LoadFromConfigs(configFolder, _context.ActiveMods);

            var buildInfo = HoconConfigurationFactory.FromResource<Backend>("VersionInfo");
            _version = new RequiemVersion(buildInfo.GetInt("versionNumber"), buildInfo.GetString("versionName"));

            Log.Information($"working directory: {System.IO.Directory.GetCurrentDirectory()}");
            Log.Information($"patcher version: {_version}");
            Log.Information($"build git branch: {buildInfo.GetString("gitBranch")}");
            Log.Information($"build git revision: {buildInfo.GetString("gitRevision")}");

            _executor = new MainLogicExecutor(_events, _context, reqtificatorConfig, _version);

            var userConfig = LoadAndVerifyUserSettings(_context);

            _events.PublishReadyToPatch(userConfig, _context.ActiveMods.Select(x => x.ModKey));
            Log.Information("Ready to patch: userConfig detected\r\n{userConfig}", userConfig);

            _events.PatchRequested += (_, updatedSettings) =>
            {
                var logLevel = updatedSettings.VerboseLogging ? LogEventLevel.Debug : LogEventLevel.Information;
                _logs.LogLevel.MinimumLevel = logLevel;
                updatedSettings.WriteToFile(Path.Combine(_context.DataFolder, "Reqtificator", "UserSettings.json"));
                var generatedPatch = GeneratePatch(updatedSettings, PatchModKey);
                Log.Information("done patching, now exporting to disk");
                WritePatchToDisk(generatedPatch, _context.DataFolder);
                Log.Information("done exporting");
                _events.PublishFinished(ReqtificatorOutcome.Success);
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
                _events.PublishFinished(ReqtificatorOutcome.MissingRequiem);
            }

            return userConfig;
        }


        public SkyrimMod GeneratePatch(UserSettings userConfig, ModKey patchModKey)
        {
            try
            {
                var loadOrder = LoadOrder.Import<ISkyrimModGetter>(_context.DataFolder, _context.ActiveMods, _context.Release);

                var checkResult = SetupValidation.VerifySetup(loadOrder, _version);
                if (checkResult is not null)
                {
                    _events.PublishFinished(checkResult);
                    throw checkResult.Exception;
                }

                Log.Information("start patching");

                return _executor.GeneratePatch(loadOrder, userConfig, patchModKey) switch
                {
                    Success<SkyrimMod> s => s.Value,
                    Failed<SkyrimMod> f => throw f.Error,
                    _ => throw new NotImplementedException()
                };
            }
            catch (Exception ex)
            {
                Log.Error(ex, "things did not go according to plan");
                _events.PublishFinished(ReqtificatorFailure.CausedBy(ex));
                throw;
            }
        }

        public static void WritePatchToDisk(SkyrimMod generatedPatch, string outputDirectory)
        {
            generatedPatch.WriteToBinaryParallel(Path.Combine(outputDirectory, generatedPatch.ModKey.FileName));
        }
    }
}