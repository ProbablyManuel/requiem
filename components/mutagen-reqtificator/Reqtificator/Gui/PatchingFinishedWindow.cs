using System;
using System.Runtime.InteropServices;
using System.Windows;
using System.Windows.Interop;

namespace Reqtificator.Gui
{
    /// <summary>
    /// Interaction logic for PatchingFinishedWindow.xaml
    /// </summary>
    public partial class PatchingFinishedWindow : Window
    {
        // https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-setwindowlongptra

        private const int GWL_STYLE = -16; // Sets new window style
        private const int WS_SYSMENU = 0x80000; // Has a window menu

        [DefaultDllImportSearchPaths(DllImportSearchPath.System32)]
        [DllImport("user32.dll")]
        private static extern int GetWindowLongPtr(IntPtr hWnd, int nIndex);

        [DefaultDllImportSearchPaths(DllImportSearchPath.System32)]
        [DllImport("user32.dll")]
        private static extern int SetWindowLongPtr(IntPtr hWnd, int nIndex, int dwNewLong);

        public PatchingFinishedWindow()
        {
            InitializeComponent();
            SourceInitialized += HideWindowMenu;
        }

        private void HideWindowMenu(object? _, EventArgs e)
        {
            var hwnd = new WindowInteropHelper(this).Handle;
            int styleWithoutWindowMenu = GetWindowLongPtr(hwnd, GWL_STYLE) & ~WS_SYSMENU;
            _ = SetWindowLongPtr(hwnd, GWL_STYLE, styleWithoutWindowMenu);
        }
    }
}
