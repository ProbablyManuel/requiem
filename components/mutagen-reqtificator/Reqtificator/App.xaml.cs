using System.IO;
using Reqtificator.Gui;
using Serilog;
using System.Windows;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;

namespace Reqtificator
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        private const GameRelease Release = GameRelease.SkyrimSE;
        private static readonly ModKey PatchModKey = ModKey.FromNameAndExtension("Requiem for the Mutated.esp");

        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);

            LogUtils.SetUpLogging();
            Log.Information("starting the Reqtificator");

            var eventQueue = new InternalEvents();
            var backend = new Backend(eventQueue);

            var context = GameContext.GetRequiemContext(Release, PatchModKey);
            var userConfig = backend.LoadAndVerifyUserSettings(context);

            Log.Debug("Starting Gui");
            MainWindowViewModel mainWindowViewModel = new(eventQueue, userConfig, context.ActiveMods);
            MainWindow window = new() { DataContext = mainWindowViewModel };
            window.Show();

            eventQueue.PatchRequested += (_, updatedSettings) =>
            {
                updatedSettings.WriteToFile(Path.Combine(context.DataFolder, "Reqtificator", "UserSettings.json"));
                var generatedPatch = backend.GeneratePatch(context, updatedSettings, Release, PatchModKey);
                Log.Information("done patching, now exporting to disk");
                MainLogic.WritePatchToDisk(generatedPatch, context.DataFolder);
                Log.Information("done exporting");
                eventQueue.PublishPatchCompleted();
                Log.CloseAndFlush();
            };

            Log.Debug("Gui Started");
        }
    }
}
