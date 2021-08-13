using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;

namespace Reqtificator.Gui
{
    public class DynamicRichTextBox : RichTextBox
    {
        public static readonly DependencyProperty MessageProperty =
            DependencyProperty.Register("Message", typeof(FlowDocument), typeof(DynamicRichTextBox),
                new PropertyMetadata(new PropertyChangedCallback(OnMessageChanged)));


        public DynamicRichTextBox()
        {

        }

        public FlowDocument Message
        {
            get => (FlowDocument)GetValue(MessageProperty);
            set => SetValue(MessageProperty, value);
        }

        private static void OnMessageChanged(DependencyObject d,
           DependencyPropertyChangedEventArgs e)
        {
            if (d is RichTextBox messageHolder)
            {
                messageHolder.Document = e.NewValue as FlowDocument;
            }
        }
    }
}
