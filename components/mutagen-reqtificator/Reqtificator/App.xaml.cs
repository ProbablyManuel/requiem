using System;
using System.Windows;
using System.Windows.Threading;
using Reqtificator.Gui;
using Serilog;

namespace Reqtificator
{
    public partial class App : Application
    {

        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);

            var logContext = new ReqtificatorLogContext(LogUtils.DefaultLogFileName);
            Log.Information("starting the Reqtificator");

            var eventQueue = new InternalEvents();
            var dispatcher = Dispatcher.CurrentDispatcher;

            Log.Debug("Starting Gui");
            MainWindowViewModel mainWindowViewModel = new(eventQueue);
            MainWindow window = new() { DataContext = mainWindowViewModel };

            eventQueue.ExceptionOccured += (_, ex) => { OnUiThread(dispatcher, () => HandleError(ex, window)); };
            eventQueue.PatchCompleted += (_, _1) => { OnUiThread(dispatcher, () => HandlePatchCompleted(window)); };

            window.Show();

            var backend = new Backend(eventQueue, logContext);
            Log.Debug("Gui Started");
        }

        private static void OnUiThread(Dispatcher d, Action a)
        {
            d.Invoke(a);
        }

        private static void HandlePatchCompleted(MainWindow window)
        {
            PatchCompletedViewModel patchCompletedViewModel = new();
            PatchCompletedWindow pcWindow = new() { DataContext = patchCompletedViewModel };
            patchCompletedViewModel.CloseRequested += () =>
            {
                pcWindow.Close();
                window.Close();
            };
            _ = pcWindow.ShowDialog();
        }

        private static void HandleError(Exception ex, MainWindow window)
        {
            var messageText = "Unfortunately an error occurred:\r\n" + ex.Message;
            _ = MessageBox.Show(messageText, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            Log.Error(ex.ToString());
            window.Close();
        }

        private void App_SessionEnding(object _, SessionEndingCancelEventArgs ea)
        {
            Log.CloseAndFlush();
        }
    }
}
