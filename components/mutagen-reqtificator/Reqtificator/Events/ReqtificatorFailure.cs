using System;
using Reqtificator.Utils;

namespace Reqtificator.Events
{
    public abstract class ReqtificatorFailure : ReqtificatorOutcome
    {
        public abstract Exception Exception { get; }

        private class ReqtificatorFailureOutcomeBase : ReqtificatorFailure
        {
            internal ReqtificatorFailureOutcomeBase(Exception e)
            {
                Status = PatchStatus.ERROR;
                Title = "Patch Failed";
                Message = Formatter.FormatMultiline(@"
                There was an exception thrown during program execution!

                Check the **Reqtificator.log** in your **data**
                folder. If you cannot make any sense out of it, you can upload it to
                [Requiem Service Central]({0}) and ask for help there.

                Exception message was: {1}", Uris.ServiceCentral, e.Message);
                Exception = e;
            }
            public override PatchStatus Status { get; }
            public override string Title { get; }
            public override string Message { get; }
            public override Exception Exception { get; }
        }

        internal static ReqtificatorFailure CausedBy(Exception ex)
        {
            return new ReqtificatorFailureOutcomeBase(ex);
        }
    }
}
