using System;
using System.Runtime.InteropServices;
using System.Windows;
using System.Windows.Interop;

namespace Reqtificator.Gui
{
    /// <summary>
    /// Interaction logic for PatchCompletedWindow.xaml
    /// </summary>
    public partial class PatchCompletedWindow : Window
    {
        private const int GWL_STYLE = -16;
        private const int WS_SYSMENU = 0x80000;

        [DefaultDllImportSearchPaths(DllImportSearchPath.System32)]
        [DllImport("user32.dll", SetLastError = true)]
        private static extern int GetWindowLong(IntPtr hWnd, int nIndex);

        [DefaultDllImportSearchPaths(DllImportSearchPath.System32)]
        [DllImport("user32.dll")]
        private static extern int SetWindowLong(IntPtr hWnd, int nIndex, int dwNewLong);

        public PatchCompletedWindow()
        {
            InitializeComponent();
            SourceInitialized += Window_SourceInitialized;
        }

        private void Window_SourceInitialized(object? _, EventArgs e)
        {
            var hwnd = new WindowInteropHelper(this).Handle;
            _ = SetWindowLong(hwnd, GWL_STYLE, GetWindowLong(hwnd, GWL_STYLE) & ~WS_SYSMENU);
        }
    }
}
