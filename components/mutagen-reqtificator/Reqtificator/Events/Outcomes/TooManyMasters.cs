using Reqtificator.Utils;

namespace Reqtificator.Events.Outcomes
{
    internal class TooManyMasters : ReqtificatorOutcome
    {
        public override PatchStatus Status => PatchStatus.ERROR;
        public override string Title => "Patch export failed!";

        public override string Message => Formatter.FormatMultiline(@"
            The generated patch could not be exported because it references too many masters.
            Although Skyrim Special Edition can load more than 256 mods at runtime, the internal size
            limitation on the masterlist of a plugin still exists. A mod can contain references
            to at most 255 other mods.
            You will need to either remove some mods from your load order or merge plugins.

            Note that the generated patch will only reference those mods from your load order that need
            to be referenced. The masterlist size limit is therefore not equivalent to a hard limit on
            the number of plugins you can use.
            ");
    }
}