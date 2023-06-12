using Reqtificator.Events.Outcomes;
using Reqtificator.Utils;

namespace Reqtificator.Events
{
    internal class InvalidScrambledBugsSettings : ReqtificatorOutcome
    {
        public InvalidScrambledBugsSettings()
        {
            Status = PatchStatus.WARNING;
            Title = "Invalid Scrambled Bugs Settings!";
            Message = Formatter.FormatMultiline(@"
                The recommended settings for Scrambled Bugs could not be verified!
                
                This likely means you are using different version of Scrambled
                Bugs than expected. You can still choose to run the  Reqtificator
                if you want.");
        }

        public override PatchStatus Status { get; }
        public override string Title { get; }
        public override string Message { get; }
    }
}
