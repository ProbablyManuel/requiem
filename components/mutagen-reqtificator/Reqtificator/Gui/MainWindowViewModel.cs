using Mutagen.Bethesda;
using Reqtificator.Configuration;
using Serilog;
using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Windows.Input;

namespace Reqtificator.Gui
{
    public class MainWindowViewModel : INotifyPropertyChanged
    {
        private static readonly InternalEvents _events = InternalEvents.Instance;

        public event PropertyChangedEventHandler? PropertyChanged;

        public ObservableCollection<ModViewModel> Mods { get; } = new ObservableCollection<ModViewModel>();

        public bool VerboseLogging { get; set; }
        public bool MergeLeveledLists { get; set; }
        public bool MergeLeveledCharacters { get; set; }
        public bool OpenEncounterZones { get; set; }
        public ICommand PatchCommand => new DelegateCommand(RequestPatch);

        private void RequestPatch()
        {
            UserSettings updatedUserSettings = new(VerboseLogging, MergeLeveledLists, MergeLeveledCharacters, OpenEncounterZones,
                Mods.Where(m => m.NpcVisuals).Select(m => m.ModKey).ToList().ToImmutableList(), Mods.Where(m => m.RaceVisuals).Select(m => m.ModKey).ToImmutableList());
            _events.RequestPatch(new UserSettingsEventArgs(updatedUserSettings));
        }

        public MainWindowViewModel()
        {
            Log.Information("Window created; listening to events");
            _events.LoadOrderSettingsChanged += (s, los) =>
            {
                UpdateMods(los);
            };

            _events.UserOptionsChanged += (s, us) =>
            {
                VerboseLogging = us.VerboseLogging;
                MergeLeveledLists = us.MergeLeveledLists;
                MergeLeveledCharacters = us.MergeLeveledCharacters;
                OpenEncounterZones = us.OpenEncounterZones;
                NotifyUserSettingsChanged();
            };
        }

        private void NotifyUserSettingsChanged()
        {
            List<string> properties = new() { "VerboseLogging", "MergeLeveledLists", "MergeLeveledCharacters", "OpenEncounterZones" };
            foreach (string? property in properties)
            {
                PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(property));
            }
        }

        private void UpdateMods(LoadOrderSettingsEventArgs los)
        {
            Mods.Clear();
            foreach (ModKey mod in los.LoadOrder)
            {
                bool npcVisuals = los.NpcVisualTemplateMods.Contains(mod);
                bool raceVisuals = los.RaceVisualTemplateMods.Contains(mod);
                Mods.Add(new ModViewModel(mod, npcVisuals, raceVisuals));
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

            void ICommand.Execute(object? parameter) { action(); }

            bool ICommand.CanExecute(object? parameter)
            {
                return true;
            }
        }

    }
}
