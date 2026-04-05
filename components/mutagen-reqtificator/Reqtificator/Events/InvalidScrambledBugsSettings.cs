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
                The settings for Scrambled Bugs are invalid!
                
                This likely means you made a typo while editing the settings file.
                If you are unable to find the error in the file, please reinstall
                Scrambled Bugs and apply the recommended settings again.");
        }

        public override PatchStatus Status { get; }
        public override string Title { get; }
        public override string Message { get; }
    }
}
