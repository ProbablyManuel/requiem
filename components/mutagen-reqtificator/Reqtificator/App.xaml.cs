using System;
using System.Windows;
using System.Windows.Threading;
using Reqtificator.Events;
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

                eventQueue.PatchingFinished += (_, patchStatus) => { OnUiThread(dispatcher, () => HandlePatchingFinished(window, patchStatus)); };

                window.Show();

                var backend = new Backend(eventQueue, logContext);
                Log.Debug("Gui Started");
            }
            catch (Exception ex)
            {
                HandlePatchingFinished(null, ReqtificatorFailure.CausedBy(ex));
                throw;
            }
        }

        private static void OnUiThread(Dispatcher d, Action a)
        {
            d.Invoke(a);
        }

        private static void HandlePatchingFinished(MainWindow? window, ReqtificatorOutcome outcome)
        {
            PatchingFinishedViewModel patchingFinishedViewModel = new(outcome);
            PatchingFinishedWindow pfWindow = new() { DataContext = patchingFinishedViewModel };
            patchingFinishedViewModel.CloseRequested += () =>
            {

                pfWindow.Close();
                window?.Close();
            };

            if (outcome is ReqtificatorFailure exOutcome)
            {
                Log.Error(exOutcome.Exception.Message);
                Log.Error(exOutcome.Exception.StackTrace);
            }
            _ = pfWindow.ShowDialog();
        }

        private void App_SessionEnding(object _, SessionEndingCancelEventArgs ea)
        {
            Log.CloseAndFlush();
        }
    }
}
