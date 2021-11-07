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

                eventQueue.PatchingResult += (_, patchStatus) => { OnUiThread(dispatcher, () => HandlePatchingResult(window, patchStatus)); };

                window.Show();

                var backend = new Backend(eventQueue, logContext);
                Log.Debug("Gui Started");
            }
            catch (Exception ex)
            {
                HandlePatchingResult(null, ReqtificatorFailure.CausedBy(ex));
                throw;
            }
        }

        private static void OnUiThread(Dispatcher d, Action a)
        {
            d.Invoke(a);
        }

        private static void HandlePatchingResult(MainWindow? window, ReqtificatorOutcome outcome)
        {
            Log.Information("Patching Result: " + outcome.Status.ToString());
            try
            {
                PatchStatusViewModel patchingFinishedViewModel = new(outcome);
                PatchStatusWindow pfWindow = new() { DataContext = patchingFinishedViewModel };
                patchingFinishedViewModel.CloseRequested += () =>
                {

                    pfWindow.Close();
                    window?.Close();
                };
                patchingFinishedViewModel.ReturnRequested += () =>
                {

                    pfWindow.Close();
                };

                if (outcome is ReqtificatorFailure exOutcome)
                {
                    throw exOutcome.Exception;
                }
                _ = pfWindow.ShowDialog();
            }
            catch (Exception e)
            {
                Log.Error(e.Message);
                Log.Error(e.StackTrace);
                throw;
            }
        }

        private void App_SessionEnding(object _, SessionEndingCancelEventArgs ea)
        {
            Log.CloseAndFlush();
        }
    }
}
