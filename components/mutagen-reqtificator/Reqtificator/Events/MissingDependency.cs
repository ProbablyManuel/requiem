using System;
using Reqtificator.Exceptions;
using Reqtificator.Utils;

namespace Reqtificator.Events
{
    internal class MissingBugfixDependency : ReqtificatorFailure
    {
        public MissingBugfixDependency(string name, string downloadLocation, Uri downloadUrl)
        {
            Status = PatchStatus.ERROR;
            Title = "Missing Requirements!";
            Message = Formatter.FormatMultiline(@"
                You are missing one of the required dependencies for Requiem!

                Requiem could not find '{0}' in your setup.
                These bugfixes are important for Requiem to work correctly.
                Please install them before you continue your Requiem installation.

                You can download '{0}' at [{1}]({2})", name, downloadLocation, downloadUrl.ToString());
            Exception = new MissingDependencyException(name);
        }

        public override PatchStatus Status { get; }
        public override string Title { get; }
        public override string Message { get; }
        public override Exception Exception { get; }
    }
}