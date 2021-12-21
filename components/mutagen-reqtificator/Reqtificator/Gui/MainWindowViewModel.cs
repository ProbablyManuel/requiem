using System;
using System.ComponentModel;
using System.Threading;
using System.Windows.Input;
using Reqtificator.Configuration;
using Reqtificator.Events;

namespace Reqtificator.Gui
{
    internal partial class MainWindowViewModel : INotifyPropertyChanged, IDisposable
    {
        private readonly InternalEvents _events;
        private readonly BackgroundWorker _patchRequestThread;
        public event PropertyChangedEventHandler? PropertyChanged;

        public bool VerboseLogging { get; set; }
        public bool MergeLeveledLists { get; set; }
        public bool MergeLeveledCharacters { get; set; }
        public bool OpenEncounterZones { get; set; }
        public bool RaceVisualAutoMerge { get; set; }
        public bool ActorVisualAutoMerge { get; set; }
        public string ProgramStatus { get; private set; }
        public double Progress { get; private set; }
        public bool IsPatching { get; private set; }
        public ICommand PatchCommand { get; private set; }

        public MainWindowViewModel(InternalEvents eventsQueue)
        {
            ProgramStatus = "";

            var mainThreadContext = SynchronizationContext.Current;
            _events = eventsQueue;

            _events.StateChanged += (_, state) => { mainThreadContext?.Post(_ => HandleStateChanged(state), null); };

            _patchRequestThread = new BackgroundWorker();
            _patchRequestThread.DoWork += (_, _1) => { _events.RequestPatch(GetUpdatedUserSettings()); };

            PatchCommand = new DelegateCommand(RequestPatch);
        }

        private void HandleStateChanged(ReqtificatorState state)
        {
            ProgramStatus = state.Readable;
            IsPatching = state.IsPatching;
            Progress = state.PercentageProgress;

            if (state is ReadyToPatchState readyToPatchState) { DisplayUserSettings(readyToPatchState); }

            NotifyChanged(nameof(ProgramStatus), nameof(Progress), nameof(IsPatching));
        }

        private UserSettings GetUpdatedUserSettings()
        {
            return new UserSettings(VerboseLogging, MergeLeveledLists, MergeLeveledCharacters, OpenEncounterZones, RaceVisualAutoMerge, ActorVisualAutoMerge);
        }

        private void RequestPatch(object? _)
        {
            _patchRequestThread.RunWorkerAsync();
        }

        private void DisplayUserSettings(ReadyToPatchState patchContext)
        {
            var userSettings = patchContext.UserSettings;
            VerboseLogging = userSettings.VerboseLogging;
            MergeLeveledLists = userSettings.MergeLeveledLists;
            MergeLeveledCharacters = userSettings.MergeLeveledCharacters;
            OpenEncounterZones = userSettings.OpenEncounterZones;
            RaceVisualAutoMerge = userSettings.RaceVisualAutoMerge;
            ActorVisualAutoMerge = userSettings.ActorVisualAutoMerge;


            NotifyChanged(
                nameof(VerboseLogging),
                nameof(MergeLeveledLists),
                nameof(MergeLeveledCharacters),
                nameof(OpenEncounterZones),
                nameof(RaceVisualAutoMerge),
                nameof(ActorVisualAutoMerge));
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