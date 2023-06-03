using System;
using System.Collections.Generic;
using System.Linq;
using Reqtificator.Events.Outcomes;
using Reqtificator.Utils;

namespace Reqtificator.Events
{
    internal class MissingScrambledBugsPatch : ReqtificatorOutcome
    {
        private readonly string nl = Environment.NewLine;

        public MissingScrambledBugsPatch(List<ScrambledBugsPatch> dependencies)
        {
            Status = PatchStatus.WARNING;
            Title = "Disabled Scrambled Bugs Patches!";


            string messageString = string.Join(nl + nl,
                dependencies.Select(d =>
                $" - **{d.Name}**"
            ));

            Message = Formatter.FormatMultiline(@"
                Recommended patches from Scrambled Bugs are disabled!
                
                These are important patches for Skyrim bugs that significantly
                affect Requiem gameplay. You can still choose to run the 
                Reqtificator if you want.

                Disabled patches are:

                " + messageString);
        }

        public override PatchStatus Status { get; }
        public override string Title { get; }
        public override string Message { get; }
    }
    public record ScrambledBugsPatch(string Name, string Key);
}
