using Mutagen.Bethesda;
using Reqtificator.Configuration;
using System;
using System.Collections.Generic;


namespace Reqtificator
{
    public class InternalEvents
    {

        public event EventHandler<LoadOrderSettingsEventArgs> LoadOrderSettingsChanged = delegate { };
        public event EventHandler<OptionsEventArgs> UserOptionsChanged = delegate { };
        public event EventHandler<UserSettings> PatchRequested = delegate { };
        public event EventHandler<PatchCompleted> PatchCompleted = delegate { };

        public void NotifyLoadOrderSettingsChanged(LoadOrderSettingsEventArgs eventArgs)
        {
            LoadOrderSettingsChanged.Invoke(this, eventArgs);
        }

        public void NotifyUserOptionsChanged(OptionsEventArgs eventArgs)
        {
            UserOptionsChanged.Invoke(this, eventArgs);
        }

        public void RequestPatch(UserSettings userSettings)
        {
            PatchRequested.Invoke(this, userSettings);
        }

        public void PublishPatchCompleted()
        {
            PatchCompleted.Invoke(this, new PatchCompleted());
        }
    }

    public class LoadOrderSettingsEventArgs : EventArgs
    {
        public LoadOrderSettingsEventArgs(IList<ModKey> loadOrder, IList<ModKey> npcVisualTemplateMods,
            IList<ModKey> raceVisualTemplateMods)
        {
            LoadOrder = loadOrder;
            NpcVisualTemplateMods = npcVisualTemplateMods;
            RaceVisualTemplateMods = raceVisualTemplateMods;
        }

        public IList<ModKey> LoadOrder { get; }
        public IList<ModKey> NpcVisualTemplateMods { get; }
        public IList<ModKey> RaceVisualTemplateMods { get; }
    }

    public class OptionsEventArgs : EventArgs
    {
        public OptionsEventArgs(bool verboseLogging, bool mergeLeveledCharacters, bool mergeLeveledLists,
            bool openEncounterZones)
        {
            VerboseLogging = verboseLogging;
            MergeLeveledCharacters = mergeLeveledCharacters;
            MergeLeveledLists = mergeLeveledLists;
            OpenEncounterZones = openEncounterZones;
        }

        public bool VerboseLogging { get; }
        public bool MergeLeveledCharacters { get; }
        public bool MergeLeveledLists { get; }
        public bool OpenEncounterZones { get; }
    }

    public record PatchCompleted;
}