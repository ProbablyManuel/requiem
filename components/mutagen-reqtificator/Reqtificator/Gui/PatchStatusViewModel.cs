using System;
using System.Globalization;
using System.Windows.Documents;
using System.Windows.Input;
using Reqtificator.Events.Outcomes;

namespace Reqtificator.Gui
{
    internal class PatchStatusViewModel
    {
        public ICommand CloseCommand => new DelegateCommand(RequestClose);
        public ICommand ReturnCommand => new DelegateCommand(RequestReturn);

        public Action CloseRequested = delegate { };

        public Action ReturnRequested = delegate { };

        public PatchStatusViewModel(ReqtificatorOutcome outcome)
        {
            Title = outcome.Title;
            Message = MessageFactory.BuildMessage(outcome.Message);
            string imageName = outcome.Status.ToString().ToLower(CultureInfo.CurrentCulture);
            Image = $"../Resources/Images/{imageName}.png";
            IsWarning = outcome.Status == PatchStatus.WARNING;
        }

        private void RequestClose(object? sender) { CloseRequested.Invoke(); }
        private void RequestReturn(object? sender) { ReturnRequested.Invoke(); }

        public FlowDocument Message { get; private set; }

        public string Title { get; private set; }

        public string Image { get; private set; }

        public bool IsWarning { get; private set; }
    }
}