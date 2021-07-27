using System;
using System.Diagnostics;
using System.Windows;
using System.Windows.Documents;
using System.Windows.Navigation;

namespace Reqtificator
{
    public static class FlowDocumentExtensions
    {
        public static Paragraph P(this FlowDocument fd, string content)
        {
            var p = new Paragraph(new Run(content));
            fd?.Blocks.Add(p);
            return p;
        }

        public static Paragraph Norm(this Paragraph p, string content)
        {
            var p2 = p ?? new Paragraph();
            p2.Inlines.Add(new Run(content));
            return p2;
        }

        public static Paragraph Bold(this FlowDocument fd, string content)
        {
            var fd2 = fd ?? new FlowDocument();
            var p = new Paragraph();
            fd2.Blocks.Add(p);
            return p.Bold(content);
        }

        public static Paragraph Bold(this Paragraph p, string content)
        {
            var p2 = p ?? new Paragraph();
            p2.Inlines.Add(new Run(content) { FontWeight = FontWeights.Bold });
            return p2;
        }
        public static Paragraph Link(this Paragraph p, string content, Uri uri)
        {
            var p2 = p ?? new Paragraph();
            var h = new Hyperlink(new Run(content)) { NavigateUri = uri, IsEnabled = true };
            h.RequestNavigate += (_, e) => { OpenHyperlinkExternally(e); };
            p2.Inlines.Add(h);
            return p2;
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
            var fd2 = fd ?? new FlowDocument();
            var list = new List();
            fd2.Blocks.Add(list);
            return list;
        }

        public static List Li(this List list, string content)
        {
            var list2 = list ?? new List();
            var li = new ListItem(new Paragraph(new Run(content)));
            list2.ListItems.Add(li);
            return list2;
        }
    }
}