using System;
using Reqtificator.Utils;

namespace Reqtificator.Events
{
    internal class MissingBugfixDependency : ReqtificatorOutcome
    {
        public MissingBugfixDependency(string name, string downloadLocation, Uri downloadUrl)
        {
            Status = PatchStatus.WARNING;
            Title = "Missing Requirements!";
            Message = Formatter.FormatMultiline(@"
                You are missing one of the required dependencies for Requiem!

                Requiem could not find '{0}' in your setup.
                This is highly recommended for Requiem to work correctly. However,
                you can still choose to run the Reqtificator if you want.

                You can download '{0}' at [{1}]({2})", name, downloadLocation, downloadUrl.ToString());
        }

        public override PatchStatus Status { get; }
        public override string Title { get; }
        public override string Message { get; }
    }
}