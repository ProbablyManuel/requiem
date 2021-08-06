using System;
using System.Globalization;
using System.Windows.Documents;
using System.Windows.Input;

namespace Reqtificator.Gui
{
    internal class PatchingFinishedViewModel
    {
        public ICommand CloseCommand => new DelegateCommand(RequestClose);

        public Action CloseRequested = delegate { };

        public PatchingFinishedViewModel(ReqtificatorOutcome outcome)
        {
            Title = outcome.Title;
            Message = MessageFactory.BuildMessage(outcome.Message);
            var imageName = outcome.Status.ToString().ToLower(CultureInfo.CurrentCulture);
            Image = $"../Resources/Images/{imageName}.png";
        }

        private void RequestClose(object? sender) { CloseRequested.Invoke(); }

        public FlowDocument Message { get; private set; }

        public string Title { get; private set; }

        public string Image { get; private set; }
    }
}