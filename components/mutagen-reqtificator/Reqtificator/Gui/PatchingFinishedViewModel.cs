using System;
using System.Windows.Documents;
using System.Windows.Input;

namespace Reqtificator.Gui
{
    internal class PatchingFinishedViewModel
    {
        public ICommand CloseCommand => new DelegateCommand(RequestClose);

        public Action CloseRequested = delegate { };

        public PatchingFinishedViewModel(Reqtificator.PatchingFinished patchStatus)
        {
            Message = MessageFactory.BuildMessage(patchStatus);
        }

        private void RequestClose(object? sender) { CloseRequested.Invoke(); }

        public FlowDocument Message { get; private set; }
    }
}