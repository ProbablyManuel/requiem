﻿using System;
using System.Collections.Generic;
using System.Linq;
using Reqtificator.Events.Outcomes;
using Reqtificator.Utils;

namespace Reqtificator.Events
{
    internal class MissingBugfixDependency : ReqtificatorOutcome
    {
        private readonly string nl = Environment.NewLine;

        public MissingBugfixDependency(List<BugfixDependency> dependencies)
        {
            Status = PatchStatus.WARNING;
            Title = "Missing SKSE Plugins!";


            string messageString = string.Join(nl + nl,
                dependencies.Select(d =>
                $" - **{d.Name}** (download at [{d.DownloadLocation}]({d.DownloadUrl}))"
            ));

            Message = Formatter.FormatMultiline(@"
                Recommended SKSE plugins are missing!
                
                These are important fixes for Skyrim bugs that significantly
                affect Requiem gameplay. You can still choose to run the 
                Reqtificator if you want.

                Missing dependencies are:

                " + messageString);
        }

        public override PatchStatus Status { get; }
        public override string Title { get; }
        public override string Message { get; }
    }
    internal record BugfixDependency(string Name, string ExpectedLocation, string DownloadLocation, Uri DownloadUrl);
}