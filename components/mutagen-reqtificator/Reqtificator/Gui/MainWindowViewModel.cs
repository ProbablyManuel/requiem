using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;
using Reqtificator.Configuration;
using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Threading;
using System.Windows.Input;

namespace Reqtificator.Gui
{
    internal class MainWindowViewModel : INotifyPropertyChanged, IDisposable
    {
        private readonly InternalEvents _events;
        private readonly SynchronizationContext? _syncContext;
        private int recordsProcessed;
        private BackgroundWorker worker = new();

        private UserSettings updatedUserSettings = new UserSettings(false, false, false, false, new List<ModKey>().ToImmutableList(), new List<ModKey>().ToImmutableList());

        public bool IsPatching { get; private set; }

        private int maxRecords;

        public event PropertyChangedEventHandler? PropertyChanged;

        public ObservableCollection<ModViewModel> Mods { get; }

        public bool VerboseLogging { get; set; }
        public bool MergeLeveledLists { get; set; }
        public bool MergeLeveledCharacters { get; set; }
        public bool OpenEncounterZones { get; set; }
        public ICommand PatchCommand => new DelegateCommand(RequestPatch);

        public string ProgramStatus { get; private set; }
        public double Progress => maxRecords == 0 ? 0 : recordsProcessed * 100 / maxRecords;

        public MainWindowViewModel(InternalEvents eventsQueue)
        {
            Mods = new ObservableCollection<ModViewModel>();
            ProgramStatus = "";

            _syncContext = SynchronizationContext.Current;
            _events = eventsQueue;
            _events.ReadyToPatch += (_, patchContext) => { _syncContext?.Post(_ => HandlePatchReady(patchContext), null); };
            _events.PatchStarted += (_, patchStarted) => { _syncContext?.Post(_ => HandlePatchStarted(patchStarted), null); };
            _events.PatchCompleted += (_, _1) => { _syncContext?.Post(_ => HandlePatchCompleted(), null); };
            _events.RecordProcessed += (_, result) => { _syncContext?.Post(_ => HandlePatchProgress(result), null); };
        }
        private void RequestPatch()
        {
            ProgramStatus = "patching";
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(ProgramStatus)));
            updatedUserSettings =
                new(VerboseLogging, MergeLeveledLists, MergeLeveledCharacters, OpenEncounterZones,
                    Mods.Where(m => m.NpcVisuals).Select(m => m.ModKey).ToList().ToImmutableList(), Mods
                        .Where(m => m.RaceVisuals).Select(m => m.ModKey).ToImmutableList());

            // NB: These have to be instance-level so that they don't get garbage-disposed while they're running.
            worker = new BackgroundWorker();
            worker.DoWork += (_, _1) => { _events.RequestPatch(updatedUserSettings); };
            worker.RunWorkerAsync();
        }

        private void HandlePatchProgress(RecordProcessedResult<IMajorRecordGetter> result)
        {
            ProgramStatus = "Processed " + result.Record.GetType() + " " + result.Record.FormKey.ToString();
            recordsProcessed++;
            NotifyChanged(nameof(ProgramStatus), nameof(Progress));
        }

        private void HandlePatchCompleted()
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

            ProgramStatus = "Ready to patch...";
            maxRecords = 0;
            recordsProcessed = 0;
            IsPatching = false;
            NotifyChanged(nameof(ProgramStatus), nameof(Progress), nameof(IsPatching));
        }

        private void NotifyChanged(params string[] propertyNames)
        {
            foreach (string propertyName in propertyNames)
            {
                PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
            }
        }

        private class DelegateCommand : ICommand
        {
            private readonly Action action;
            public event EventHandler? CanExecuteChanged = delegate { };

            public DelegateCommand(Action a)
            {
                action = a;
            }

            void ICommand.Execute(object? parameter)
            {
                action();
            }

            bool ICommand.CanExecute(object? parameter)
            {
                return true;
            }
        }

        public void Dispose()
        {
            worker.Dispose();
        }
    }
}