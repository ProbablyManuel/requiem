using System;
using System.Collections.Immutable;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Threading;
using System.Windows.Input;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;
using Reqtificator.Configuration;

namespace Reqtificator.Gui
{
    internal partial class MainWindowViewModel : INotifyPropertyChanged, IDisposable
    {
        private readonly InternalEvents _events;
        private readonly BackgroundWorker _patchRequestThread;
        private int recordsProcessed;

        public bool IsPatching { get; private set; }

        private int maxRecords;

        public event PropertyChangedEventHandler? PropertyChanged;

        public ObservableCollection<ModViewModel> Mods { get; }

        public bool VerboseLogging { get; set; }
        public bool MergeLeveledLists { get; set; }
        public bool MergeLeveledCharacters { get; set; }
        public bool OpenEncounterZones { get; set; }
        public string ProgramStatus { get; private set; }
        public double Progress => maxRecords == 0 ? 0 : recordsProcessed * 100 / maxRecords;
        public ICommand PatchCommand { get; set; }

        public MainWindowViewModel(InternalEvents eventsQueue)
        {
            Mods = new ObservableCollection<ModViewModel>();
            ProgramStatus = "";

            var mainThreadContext = SynchronizationContext.Current;
            _events = eventsQueue;

            _events.ReadyToPatch += (_, patchContext) => { mainThreadContext?.Post(_ => HandlePatchReady(patchContext), null); };
            _events.PatchStarted += (_, patchStarted) => { mainThreadContext?.Post(_ => HandlePatchStarted(patchStarted), null); };
            _events.PatchingFinished += (_, _1) => { mainThreadContext?.Post(_ => HandlePatchingFinished(), null); };
            _events.RecordProcessed += (_, result) => { mainThreadContext?.Post(_ => HandlePatchProgress(result), null); };

            _patchRequestThread = new BackgroundWorker();
            _patchRequestThread.DoWork += (_, _1) => { _events.RequestPatch(GetUpdatedUserSettings()); };

            PatchCommand = new DelegateCommand(RequestPatch);
        }

        private UserSettings GetUpdatedUserSettings()
        {
            return new UserSettings(VerboseLogging, MergeLeveledLists, MergeLeveledCharacters, OpenEncounterZones,
                                Mods.Where(m => m.NpcVisuals).Select(m => m.ModKey).ToList().ToImmutableList(), Mods
                                    .Where(m => m.RaceVisuals).Select(m => m.ModKey).ToImmutableList());
        }

        private void RequestPatch(object? _)
        {
            ProgramStatus = "Patching";
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(ProgramStatus)));

            _patchRequestThread.RunWorkerAsync();
        }

        private void HandlePatchProgress(RecordProcessedResult<IMajorRecordGetter> result)
        {
            ProgramStatus = "Processed " + result.Record.GetType() + " " + result.Record.FormKey.ToString();
            recordsProcessed++;
            NotifyChanged(nameof(ProgramStatus), nameof(Progress));
        }

        private void HandlePatchingFinished()
        {
            ProgramStatus = "Done!";
            recordsProcessed++;
            IsPatching = false;
            NotifyChanged(nameof(ProgramStatus), nameof(Progress), nameof(IsPatching));
        }

        private void HandlePatchStarted(PatchStarted patchStarted)
        {
            maxRecords = patchStarted.NumberOfRecords;
            recordsProcessed = 0;
            IsPatching = true;
            NotifyChanged(nameof(ProgramStatus), nameof(Progress), nameof(IsPatching));
        }

        private void HandlePatchReady(PatchContext patchContext)
        {
            DisplayUserSettings(patchContext);
            ResetPatchingStatus();
        }

        private void DisplayUserSettings(PatchContext patchContext)
        {
            var loadedUserConfig = patchContext.UserSettings;
            VerboseLogging = loadedUserConfig.VerboseLogging;
            MergeLeveledLists = loadedUserConfig.MergeLeveledLists;
            MergeLeveledCharacters = loadedUserConfig.MergeLeveledCharacters;
            OpenEncounterZones = loadedUserConfig.OpenEncounterZones;

            Mods.Clear();
            foreach (ModKey mod in patchContext.ActiveMods)
            {
                var npcVisuals = loadedUserConfig.NpcVisualTemplateMods.Contains(mod);
                var raceVisuals = loadedUserConfig.RaceVisualTemplateMods.Contains(mod);
                Mods.Add(new ModViewModel(mod, npcVisuals, raceVisuals));
            }

            NotifyChanged(
                nameof(VerboseLogging),
                nameof(MergeLeveledLists),
                nameof(MergeLeveledCharacters),
                nameof(OpenEncounterZones));
        }

        private void ResetPatchingStatus()
        {
            ProgramStatus = "Ready to patch...";
            maxRecords = 0;
            recordsProcessed = 0;
            IsPatching = false;
            NotifyChanged(
                nameof(ProgramStatus),
                nameof(Progress),
                nameof(IsPatching));
        }

        private void NotifyChanged(params string[] propertyNames)
        {
            foreach (string propertyName in propertyNames)
            {
                PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
            }
        }

        public void Dispose()
        {
            _patchRequestThread.Dispose();
        }
    }
}