using Reqtificator.Gui;
using Serilog;
using System.Windows;

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

            Log.Information("Starting Gui");

            var eventQueue = new InternalEvents();
            var backend = new Program(eventQueue);

            MainWindowViewModel mainWindowViewModel = new(eventQueue);
            MainWindow window = new() { DataContext = mainWindowViewModel };
            window.Show();

            Log.Information("Gui Started");
            backend.SetUpPatcher();
        }
    }
}
