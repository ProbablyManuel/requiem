using System;
using Reqtificator.Configuration;
using Reqtificator.Exceptions;

namespace Reqtificator.Events
{
    internal interface IInternalEvents
    {
        public void PublishState(ReqtificatorState state);
        public void RequestPatch(UserSettings userSettings);
        public void ReportDeprecationWarning(IDeprecationWarning warning);
    }

    internal class InternalEvents : IInternalEvents
    {
        public event EventHandler<ReqtificatorState> StateChanged = delegate { };
        public event EventHandler<UserSettings> PatchRequested = delegate { };

        public event EventHandler<IDeprecationWarning> DeprecationWarningOccured = delegate { };

        public void PublishState(ReqtificatorState state)
        {
            StateChanged.Invoke(this, state);
        }

        public void RequestPatch(UserSettings userSettings)
        {
            PatchRequested.Invoke(this, userSettings);
        }

        public void ReportDeprecationWarning(IDeprecationWarning warning)
        {
            DeprecationWarningOccured.Invoke(this, warning);
        }
    }
}