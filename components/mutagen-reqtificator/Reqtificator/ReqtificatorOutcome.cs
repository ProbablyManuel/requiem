using System;
using Reqtificator.Utils;

namespace Reqtificator
{
    public enum PatchStatus
    {
        SUCCESS,
        WARNING,
        ERROR,
        MESSAGE
    }

    public abstract class ReqtificatorOutcome
    {
        private static class Uris
        {
            public const string ServiceCentral = "https://requiem.atlassian.net/servicedesk/customer/portal/1";
        }

        public abstract PatchStatus Status { get; }
        public abstract string Title { get; }
        public abstract string Message { get; }
        public abstract Exception? Cause { get; }
        public abstract bool CausedByException();

        private class ReqtificatorOutcomeBase : ReqtificatorOutcome
        {
            internal ReqtificatorOutcomeBase(PatchStatus status, string title, string message, params string[] args) : this(status, null, title, message, args) { }
            internal ReqtificatorOutcomeBase(PatchStatus status, Exception? cause, string title, string message, params string[] args)
            {
                Status = status;
                Title = title;
                Message = Formatter.FormatMultiline(message, args);
                Cause = cause;
            }
            public override PatchStatus Status { get; }
            public override string Title { get; }
            public override string Message { get; }
            public override Exception? Cause { get; }
            public override bool CausedByException()
            {
                return Cause != null;
            }
        }

        internal static readonly ReqtificatorOutcome Success = new ReqtificatorOutcomeBase(
            PatchStatus.SUCCESS, "Patch Success!", @"
            Your **Requiem for the Mutated.esp** has been created successfully.
            This patch depends heavily on the content of your load order. Please rebuild the patch when you:
            - install a new mod
            - remove a mod
            - update a mod to a newer version
            - change the order of mods
            
            Enjoy your Requiem!");

        internal static ReqtificatorOutcome GeneralError(Exception e)
        {
            return new ReqtificatorOutcomeBase(
                PatchStatus.ERROR, e, "Patch Failed", @"
                There was an exception thrown during program execution!

                Check the **Reqtificator.log** in your **data**
                folder. If you cannot make any sense out of it, you can upload it to
                [Requiem Service Central]({0}) and ask for help there.

                Exception message was: {1}", Uris.ServiceCentral, e.Message);
        }

        internal static readonly ReqtificatorOutcome MissingRequiem = new ReqtificatorOutcomeBase(
            PatchStatus.ERROR, "Could not find Requiem.esp", @"
            We couldn't find Requiem.esp. Please check that you're running the Reqtificator in the 
            **Skyrim/Data** folder where Mod Organizer or Vortex unpacked Requiem.");
    }
}
