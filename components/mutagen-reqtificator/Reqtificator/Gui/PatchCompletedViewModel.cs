using System;
using System.Windows.Input;

namespace Reqtificator.Gui
{
    internal class PatchCompletedViewModel
    {
        public ICommand CloseCommand => new DelegateCommand(RequestClose);
        public ICommand ReturnCommand => new DelegateCommand(RequestReturn);

        public EventHandler<bool> CloseRequested = delegate { };

        private void RequestClose(object? sender) { Close(sender, true); }
        private void RequestReturn(object? sender) { Close(sender, false); }

        private void Close(object? _, bool shouldCloseAll)
        {
            CloseRequested.Invoke(this, shouldCloseAll);
        }
    }
}