using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Reqtificator.Utils;

namespace Reqtificator.Events.Outcomes
{
    internal class InvalidLoadOrder : ReqtificatorOutcome
    {
        public ModKey Mod { get; }
        public IImmutableSet<ModKey> OutOfOrderMasters { get; }
        public IImmutableSet<ModKey> MissingMasters { get; }
        public IImmutableSet<ModKey> DeactivatedMasters { get; }
        public override PatchStatus Status => PatchStatus.ERROR;
        public override string Title => "Load order validation failed!";

        public override string Message
        {
            get
            {
                static string FormatModList(string title, IImmutableSet<ModKey> mods)
                {
                    if (mods.Count == 0)
                    {
                        return "";
                    }

                    {
                        string text = $@"|
                        |
                        |{title}
                        |
                        |
                        " + mods
                            .OrderBy(e => e.FileName.String)
                            .Select(e => $"|* {e.FileName}\r\n").Aggregate((a, b) => a + b);
                        return Formatter.FormatMultiline(text, '|');
                    }
                }

                return $"Your load order could not be verified. The mod '{Mod.FileName}' has missing masters:\r\n"
                    + FormatModList("**Missing Mods** (no file found)", MissingMasters)
                    + FormatModList("**Disabled Mods** (file found, but not activated)", DeactivatedMasters)
                    + FormatModList("**Unsorted Masters** (plugin loaded after dependent mod)", OutOfOrderMasters);
            }
        }

        public InvalidLoadOrder(ModKey mod, IImmutableSet<ModKey> outOfOrderMasters,
            IImmutableSet<ModKey> missingMasters, IImmutableSet<ModKey> deactivatedMasters)
        {
            Mod = mod;
            OutOfOrderMasters = outOfOrderMasters;
            MissingMasters = missingMasters;
            DeactivatedMasters = deactivatedMasters;
        }
    }
}