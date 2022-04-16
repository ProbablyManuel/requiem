using System;
using Reqtificator.Events.Outcomes;
using Reqtificator.Exceptions;
using Reqtificator.Utils;

namespace Reqtificator.Events
{
    internal class VersionMismatch : ReqtificatorFailure
    {
        public VersionMismatch(RequiemVersion pluginVersion, RequiemVersion patcherVersion)
        {
            Status = PatchStatus.ERROR;
            Title = "Version Inconsistency Detected!";
            Message = Formatter.FormatMultiline(@"
                There's a version mismatch between your Reqtificator and your Requiem.esp!

                **Reqtificator Version:** {0}

                **Requiem.esp Version:** {1}

                This version inconsistency indicates a major issue in your installation.
                Please reinstall Requiem from scratch to ensure that all files originate
                from the same release.", patcherVersion.ShortVersion(), pluginVersion.ShortVersion());

            Exception = new VersionMismatchException(pluginVersion, patcherVersion);
        }

        public override PatchStatus Status { get; }
        public override string Title { get; }
        public override string Message { get; }
        public override Exception Exception { get; }
    }
}