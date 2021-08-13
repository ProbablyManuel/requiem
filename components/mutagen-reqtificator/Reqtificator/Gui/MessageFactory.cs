using System;
using System.Diagnostics;
using System.Windows.Documents;
using System.Windows.Media;
using MdXaml;

namespace Reqtificator.Gui
{
    internal static class MessageFactory
    {
        private static readonly Action<object?> openHyperlinkExternally = (ea) =>
        {
            var link = (ea as string) ?? throw new ArgumentNullException("Null hyperlink");
            _ = Process.Start(new ProcessStartInfo()
            {
                FileName = new Uri(link).AbsoluteUri,
                UseShellExecute = true
            });
        };

        private static readonly Markdown mdFdConverter = new()
        {
            HyperlinkCommand = new DelegateCommand(openHyperlinkExternally)
        };
        public static FlowDocument BuildMessage(string message)
        {
            var document = mdFdConverter.Transform(message);
            document.IsEnabled = true;
            document.FontSize = 14;
            document.FontFamily = new FontFamily("Arial");
            return document;
        }
    }
}