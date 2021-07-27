using System;
using System.Collections.Generic;
using System.Collections.Immutable;
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
            try
            {
                var logContext = new ReqtificatorLogContext(LogUtils.DefaultLogFileName);
                Log.Information("starting the Reqtificator");

                var eventQueue = new InternalEvents();
                var dispatcher = Dispatcher.CurrentDispatcher;

                Log.Debug("Starting Gui");
                MainWindowViewModel mainWindowViewModel = new(eventQueue);
                MainWindow window = new() { DataContext = mainWindowViewModel };

                eventQueue.ExceptionOccured += (_, ex) => { OnUiThread(dispatcher, () => HandleError(ex, window)); };
                eventQueue.PatchCompleted += (_, patchStatus) => { OnUiThread(dispatcher, () => HandlePatchCompleted(window, patchStatus)); };

                window.Show();

                var backend = new Backend(eventQueue, logContext);
                Log.Debug("Gui Started");
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message);
                Log.Error(ex.StackTrace);
                HandlePatchCompleted(null, new PatchFinished(PatchStatus.GeneralError, new List<string>() { ex.Message }.ToImmutableList()));
                throw;
            }
        }

        private static void OnUiThread(Dispatcher d, Action a)
        {
            d.Invoke(a);
        }

        private static void HandlePatchCompleted(MainWindow? window, PatchFinished patchStatus)
        {

            PatchCompletedViewModel patchCompletedViewModel = new(patchStatus);
            PatchCompletedWindow pcWindow = new() { DataContext = patchCompletedViewModel };
            patchCompletedViewModel.CloseRequested += () =>
            {
                pcWindow.Close();
                window?.Close();
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
