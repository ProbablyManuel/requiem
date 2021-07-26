using System;
using System.Windows.Input;

namespace Reqtificator.Gui
{
    internal class PatchCompletedViewModel
    {
        public ICommand CloseCommand => new DelegateCommand(RequestClose);

        public Action CloseRequested = delegate { };

        private void RequestClose(object? sender) { CloseRequested.Invoke(); }
    }
}