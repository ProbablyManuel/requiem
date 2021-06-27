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

        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);

            LogUtils.SetUpLogging();
            Log.Information("starting the Reqtificator");

            var eventQueue = new InternalEvents();

            Log.Debug("Starting Gui");
            MainWindowViewModel mainWindowViewModel = new(eventQueue);
            MainWindow window = new() { DataContext = mainWindowViewModel };

            var backend = new Backend(eventQueue);

            eventQueue.ExceptionOccured += (_, ex) => { HandleError(ex, window); };

            window.Show();
            Log.Debug("Gui Started");
        }

        private static void HandleError(System.Exception ex, MainWindow window)
        {
            var messageText = "Unfortunately an error occurred:\r\n" + ex.Message;
            MessageBox.Show(messageText, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            Log.Error(ex.ToString());
            window.Close();
        }

        private void App_SessionEnding(object _, SessionEndingCancelEventArgs ea)
        {
            Log.CloseAndFlush();
        }
    }
}
