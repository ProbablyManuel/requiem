using System;
using Reqtificator.Configuration;

namespace Reqtificator
{
    internal class InternalEvents
    {
        public event EventHandler<UserSettings> PatchRequested = delegate { };
        public event EventHandler<PatchCompleted> PatchCompleted = delegate { };
        public event EventHandler<Exception> ExceptionOccured = delegate { };

        public void RequestPatch(UserSettings userSettings)
        {
            PatchRequested.Invoke(this, userSettings);
        }

        public void PublishPatchCompleted()
        {
            PatchCompleted.Invoke(this, new PatchCompleted());
        }

        public void ReportException(Exception ex)
        {
            ExceptionOccured.Invoke(this, ex);
        }
    }

    public record PatchCompleted;
}