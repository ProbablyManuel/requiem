using System;
using System.IO;
using System.Linq;
using System.Resources;
using System.Runtime.CompilerServices;
using Hocon;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Exceptions;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Configuration;
using Reqtificator.Events;
using Reqtificator.Events.Outcomes;
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
        private static readonly ModKey RequiemModKey = new("Requiem", ModType.Plugin);

        private readonly InternalEvents _events;
        private readonly MainLogicExecutor _executor;
        private readonly ReqtificatorLogContext _logs;
        private readonly GameContext _context;
        private readonly RequiemVersion _version;

        public Backend(InternalEvents eventsQueue, ReqtificatorLogContext logContext)
        {
            _events = eventsQueue;
            _logs = logContext;

            var buildInfo = HoconConfigurationFactory.FromResource<Backend>("VersionInfo");
            _version = new RequiemVersion(buildInfo.GetInt("versionNumber"), buildInfo.GetString("versionName"));
            Log.Information($"working directory: {Directory.GetCurrentDirectory()}");
            Log.Information($"patcher version: {_version}");
            Log.Information($"build git branch: {buildInfo.GetString("gitBranch")}");
            Log.Information($"build git revision: {buildInfo.GetString("gitRevision")}");

            _context = GameContext.GetRequiemContext(Release, PatchModKey);

            Log.Information("load order:");
            _context.ActiveMods.WithIndex().ForEach(m => Log.Information($"  {m.Index} - {m.Item.ModKey}"));

            ValidateRequiemFound();

            //TODO: update base folder for configurations if needed
            string configFolder = Path.Combine(_context.DataFolder, "Reqtificator", "Config");
            Log.Information($"configuration folder: {configFolder}");

            var reqtificatorConfig = ReqtificatorConfig.LoadFromConfigs(configFolder, _context.ActiveMods, _events);


            _executor = new MainLogicExecutor(_events, _context, reqtificatorConfig, _version);

            var userConfig = LoadAndVerifyUserSettings(_context);

            var readyToPatchState = ReqtificatorState.ReadyToPatch(userConfig, _context.ActiveMods.Select(x => x.ModKey));
            _events.PublishState(readyToPatchState);
            Log.Information("Ready to patch: userConfig detected\r\n{userConfig}", userConfig);

            var loadOrder = LoadAndVerifyLoadOrder(readyToPatchState);

            _events.PatchRequested += (_, updatedSettings) => { HandlePatchRequest(updatedSettings, loadOrder); };
        }

        private void ValidateRequiemFound()
        {
            if (_context.ActiveMods.All(x => x.ModKey != RequiemModKey))
            {
                Log.Error("Could not find Requiem.esp in active mods");
                _events.PublishState(ReqtificatorState.Stopped(ReqtificatorOutcome.MissingRequiem));
            }
        }

        private void HandlePatchRequest(UserSettings updatedSettings, ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder)
        {
            try
            {
                var logLevel = updatedSettings.VerboseLogging ? LogEventLevel.Debug : LogEventLevel.Information;
                _logs.LogLevel.MinimumLevel = logLevel;
                updatedSettings.WriteToFile(Path.Combine(_context.DataFolder, "Reqtificator", "UserSettings.json"));
                var generatedPatch = GeneratePatch(loadOrder, updatedSettings, PatchModKey);
                Log.Information("done patching, now exporting to disk");

                _events.PublishState(ReqtificatorState.Patching(90, "Saving Patch"));
                var outcome = WritePatchToDisk(generatedPatch, _context.DataFolder);
                if (outcome is null)
                {
                    Log.Information("done exporting");
                    _events.PublishState(ReqtificatorState.Stopped(ReqtificatorOutcome.Success));
                }
                else
                {
                    Log.Information("exporting failed");
                    _events.PublishState(ReqtificatorState.Stopped(outcome));
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Patching process failed!");
                _events.PublishState(ReqtificatorState.Stopped(ReqtificatorFailure.CausedBy(ex)));
                throw;
            }
        }

        private ILoadOrder<IModListing<ISkyrimModGetter>> LoadAndVerifyLoadOrder(ReqtificatorState stateToReturnTo)
        {
            var loadOrder = LoadOrder.Import<ISkyrimModGetter>(_context.DataFolder, _context.ActiveMods, _context.Release);
            var checkResult = SetupValidation.VerifySetup(loadOrder, _version);
            if (checkResult is not null)
            {
                _events.PublishState(ReqtificatorState.Stopped(checkResult));
                if (checkResult.Status != PatchStatus.WARNING && checkResult is ReqtificatorFailure failure) { throw failure.Exception; }
                _events.PublishState(stateToReturnTo);
            }
            return loadOrder;
        }

        public static UserSettings LoadAndVerifyUserSettings(GameContext context)
        {
            string userConfigFile = Path.Combine(context.DataFolder, "Reqtificator", "UserSettings.json");
            var userConfig = UserSettings.LoadUserSettings(userConfigFile);

            return userConfig;
        }


        public SkyrimMod GeneratePatch(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder, UserSettings userConfig, ModKey patchModKey)
        {
            try
            {

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
                _events.PublishState(ReqtificatorState.Stopped(ReqtificatorFailure.CausedBy(ex)));
                throw;
            }
        }

        public static ReqtificatorOutcome? WritePatchToDisk(SkyrimMod generatedPatch, string outputDirectory)
        {
            try
            {
                generatedPatch.WriteToBinaryParallel(Path.Combine(outputDirectory, generatedPatch.ModKey.FileName));
                return null;
            }
            catch (TooManyMastersException)
            {
                return new TooManyMasters();
            }
        }
    }
}