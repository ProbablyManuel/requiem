using System;
using System.Diagnostics;
using System.Windows;
using System.Windows.Documents;
using System.Windows.Navigation;

namespace Reqtificator
{
    internal static class FlowDocumentExtensions
    {
        public static Paragraph P(this FlowDocument fd, string content)
        {
            var p = new Paragraph(new Run(content));
            fd.Blocks.Add(p);
            return p;
        }

        public static Paragraph Norm(this Paragraph p, string content)
        {
            p.Inlines.Add(new Run(content));
            return p;
        }

        public static Paragraph Bold(this FlowDocument fd, string content)
        {
            var p = new Paragraph();
            fd.Blocks.Add(p);
            return p.Bold(content);
        }

        public static Paragraph Bold(this Paragraph p, string content)
        {
            p.Inlines.Add(new Run(content) { FontWeight = FontWeights.Bold });
            return p;
        }
        public static Paragraph Link(this Paragraph p, string content, Uri uri)
        {
            var h = new Hyperlink(new Run(content)) { NavigateUri = uri, IsEnabled = true };
            h.RequestNavigate += (_, e) => { OpenHyperlinkExternally(e); };
            p.Inlines.Add(h);
            return p;
        }

        private static void OpenHyperlinkExternally(RequestNavigateEventArgs e)
        {
            _ = Process.Start(new ProcessStartInfo()
            {
                FileName = e.Uri.AbsoluteUri,
                UseShellExecute = true
            });
            e.Handled = true;
        }

        public static List Ul(this FlowDocument fd)
        {
            var list = new List();
            fd.Blocks.Add(list);
            return list;
        }

        public static List Li(this List list, string content)
        {
            var li = new ListItem(new Paragraph(new Run(content)));
            list.ListItems.Add(li);
            return list;
        }
    }
}