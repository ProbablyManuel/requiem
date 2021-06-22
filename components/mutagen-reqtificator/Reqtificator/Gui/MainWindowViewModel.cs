using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Windows.Input;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Reqtificator.Configuration;

namespace Reqtificator.Gui
{
    internal class MainWindowViewModel : INotifyPropertyChanged
    {
        private readonly InternalEvents _events;

        public event PropertyChangedEventHandler? PropertyChanged;

        public ObservableCollection<ModViewModel> Mods { get; }

        public bool VerboseLogging { get; set; }
        public bool MergeLeveledLists { get; set; }
        public bool MergeLeveledCharacters { get; set; }
        public bool OpenEncounterZones { get; set; }
        public ICommand PatchCommand => new DelegateCommand(RequestPatch);

        public string ProgramStatus { get; private set; } = "ready";

        private void RequestPatch()
        {
            ProgramStatus = "patching";
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(ProgramStatus)));
            UserSettings updatedUserSettings =
                new(VerboseLogging, MergeLeveledLists, MergeLeveledCharacters, OpenEncounterZones,
                    Mods.Where(m => m.NpcVisuals).Select(m => m.ModKey).ToList().ToImmutableList(), Mods
                        .Where(m => m.RaceVisuals).Select(m => m.ModKey).ToImmutableList());
            _events.RequestPatch(updatedUserSettings);
        }

        public MainWindowViewModel(InternalEvents eventsQueue, UserSettings loadedUserConfig,
            IReadOnlyList<IModListingGetter> loadOrder)
        {
            _events = eventsQueue;
            VerboseLogging = loadedUserConfig.VerboseLogging;
            MergeLeveledLists = loadedUserConfig.MergeLeveledLists;
            MergeLeveledCharacters = loadedUserConfig.MergeLeveledCharacters;
            OpenEncounterZones = loadedUserConfig.OpenEncounterZones;

            Mods = new ObservableCollection<ModViewModel>();
            foreach (ModKey mod in loadOrder.Select(x => x.ModKey))
            {
                var npcVisuals = loadedUserConfig.NpcVisualTemplateMods.Contains(mod);
                var raceVisuals = loadedUserConfig.RaceVisualTemplateMods.Contains(mod);
                Mods.Add(new ModViewModel(mod, npcVisuals, raceVisuals));
            }

            _events.PatchCompleted += (_, _1) =>
            {
                ProgramStatus = "done";
                PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(ProgramStatus)));
            };
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
    }
}