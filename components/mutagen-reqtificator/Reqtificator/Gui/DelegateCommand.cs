using System;
using System.Windows.Input;

namespace Reqtificator.Gui
{
    public class DelegateCommand : ICommand
    {
        private readonly Action<object?> action;
        public event EventHandler? CanExecuteChanged = delegate { };

        public DelegateCommand(Action<object?> a)
        {
            action = a;
        }

        public void Execute(object? parameter)
        {
            action(parameter);
        }

        public bool CanExecute(object? parameter)
        {
            return true;
        }
    }
}