using Reqtificator.Utils;

namespace Reqtificator.Events.Outcomes
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
        internal static class Uris
        {
            public const string ServiceCentral = "https://www.nexusmods.com/skyrimspecialedition/mods/60888?tab=bugs";
        }
        public abstract PatchStatus Status { get; }
        public abstract string Title { get; }
        public abstract string Message { get; }

        private class ReqtificatorOutcomeBase : ReqtificatorOutcome
        {
            internal ReqtificatorOutcomeBase(PatchStatus status, string title, string message, params string[] args)
            {
                Status = status;
                Title = title;
                Message = Formatter.FormatMultiline(message, args);
            }
            public override PatchStatus Status { get; }
            public override string Title { get; }
            public override string Message { get; }
        }

        internal static readonly ReqtificatorOutcome Success = new ReqtificatorOutcomeBase(
            PatchStatus.SUCCESS, "Patch Success!", @"
            Your **Requiem for the Indifferent.esp** has been created successfully.
            This patch depends heavily on the content of your load order. Please rebuild the patch when you:
            * install a new mod
            * remove a mod
            * update a mod to a newer version
            * change the order of mods
            
            Enjoy your Requiem!");

        internal static readonly ReqtificatorOutcome MissingSkyrim = new ReqtificatorOutcomeBase(
            PatchStatus.ERROR, "Could not find Skyrim.esm", @"
            We couldn't find Skyrim.esm.

            Please check that you correctly configured the Reqtificator in your mod manager. If you
            use Vortex, you must select the **Reqtificator.bat** in Skyrim's data folder and not
            in the folder where Vortex unpacked Requiem.");

        internal static readonly ReqtificatorOutcome MissingRequiem = new ReqtificatorOutcomeBase(
            PatchStatus.ERROR, "Could not find Requiem.esp", @"
            We couldn't find Requiem.esp.

            Please check that **Requiem.esp** is enabled in your load order and is loaded before
            **Requiem for the Indifferent.esp**.");

        internal static readonly ReqtificatorOutcome MissingRequiemConfig = new ReqtificatorOutcomeBase(
            PatchStatus.ERROR, "Could not find Requiem's configuration", @"
            We couldn't find any configuration for Requiem. The file we are looking for 
            should be in your **Skyrim/Data** 
            folder, where Mod Organizer or Vortex unpacked Requiem, at: 
            
            **Data/Reqtificator/Config/Requiem/Reqtificator.conf**
            
            Please reinstall the Requiem download if required.
            ");
    }
}
