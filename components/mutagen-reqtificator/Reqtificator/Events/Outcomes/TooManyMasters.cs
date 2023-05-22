using Reqtificator.Utils;

namespace Reqtificator.Events.Outcomes
{
    internal class TooManyMasters : ReqtificatorOutcome
    {
        public override PatchStatus Status => PatchStatus.ERROR;
        public override string Title => "Patch export failed!";

        public override string Message => Formatter.FormatMultiline(@"
            The generated patch could not be exported because it references more than 254 masters.
            You will need to either remove some mods from your load order or merge plugins.

            Note that the masterlist size limit is unrelated to how many plugins Skyrim Special
            Edition can load at runtime and does not differentiate between normal and light plugins.

            The generated patch will only reference those plugins from your load order that need to
            be referenced. Therefore, the number of plugins that can be installed before exceeding
            the masterlist size limit is different for each load order.
            ");
    }
}