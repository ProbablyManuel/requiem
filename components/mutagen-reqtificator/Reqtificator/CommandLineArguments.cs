using System.Collections.Generic;
using System.CommandLine;
using System.CommandLine.Help;
using System.Linq;
using Mutagen.Bethesda;
using Reqtificator.Configuration;

namespace Reqtificator
{
    internal sealed class CommandLineArguments
    {
        private static readonly Option<bool> HeadlessOption = new("--headless")
        {
            Description = "create the patch without opening the window, using the patch options from Reqtificator/UserSettings.json",
            Arity = ArgumentArity.Zero,
        };

        private static readonly Option<GameRelease?> GameOption = CreateGameOption();

        private static readonly Option<bool> IgnoreWarningsOption = new("--ignore-warnings")
        {
            Description = "create the patch even when the setup check reports a warning",
            Arity = ArgumentArity.Zero,
        };

        private static readonly Option<bool?> VerboseLoggingOption = CreateSettingOption("--verboseLogging");
        private static readonly Option<bool?> MergeLeveledListsOption = CreateSettingOption("--mergeLeveledLists");
        private static readonly Option<bool?> MergeLeveledCharactersOption = CreateSettingOption("--mergeLeveledCharacters");
        private static readonly Option<bool?> OpenEncounterZonesOption = CreateSettingOption("--openEncounterZones");
        private static readonly Option<bool?> ActorVisualAutoMergeOption = CreateSettingOption("--actorVisualAutoMerge");
        private static readonly Option<bool?> RaceVisualAutoMergeOption = CreateSettingOption("--raceVisualAutoMerge");

        private static readonly RootCommand Command = CreateCommand();

        private readonly ParseResult _result;

        private CommandLineArguments(ParseResult result)
        {
            _result = result;
        }

        public bool Headless => _result.GetResult(HeadlessOption) is { Implicit: false };

        public bool ShowHelp => _result.Action is HelpAction;

        public bool IgnoreWarnings => _result.GetResult(IgnoreWarningsOption) is { Implicit: false };

        public GameRelease? Release => _result.GetResult(GameOption) is { } game && !game.Errors.Any() ? _result.GetValue(GameOption) : null;

        public IReadOnlyList<string> Errors => [.. _result.Errors.Select(error => error.Message)];

        public static CommandLineArguments Parse(IReadOnlyList<string> args)
        {
            return new CommandLineArguments(Command.Parse(args));
        }

        public UserSettings ApplyOverrides(UserSettings settings)
        {
            return settings with
            {
                VerboseLogging = _result.GetValue(VerboseLoggingOption) ?? settings.VerboseLogging,
                MergeLeveledLists = _result.GetValue(MergeLeveledListsOption) ?? settings.MergeLeveledLists,
                MergeLeveledCharacters = _result.GetValue(MergeLeveledCharactersOption) ?? settings.MergeLeveledCharacters,
                OpenEncounterZones = _result.GetValue(OpenEncounterZonesOption) ?? settings.OpenEncounterZones,
                ActorVisualAutoMerge = _result.GetValue(ActorVisualAutoMergeOption) ?? settings.ActorVisualAutoMerge,
                RaceVisualAutoMerge = _result.GetValue(RaceVisualAutoMergeOption) ?? settings.RaceVisualAutoMerge,
            };
        }

        public void PrintHelpOrErrors()
        {
            _ = _result.Invoke();
        }

        private static Option<GameRelease?> CreateGameOption()
        {
            var option = new Option<GameRelease?>("--game") { Description = "the game release (default: detected)" };
            _ = option.AcceptOnlyFromAmong(nameof(GameRelease.SkyrimSE), nameof(GameRelease.SkyrimSEGog));
            return option;
        }

        private static Option<bool?> CreateSettingOption(string name)
        {
            return new Option<bool?>(name)
            {
                Description = "override this patch option for this run only, without saving it",
                Arity = ArgumentArity.ExactlyOne,
                HelpName = "true|false",
            };
        }

        private static RootCommand CreateCommand()
        {
            var command = new RootCommand(
                "Creates the Requiem for the Indifferent patch. Without --headless, the Reqtificator opens its window as usual and only --game is used.\n\n" +
                "Exit codes with --headless: 0 patch created, 1 error, 2 stopped on a warning, 64 invalid arguments.")
            {
                HeadlessOption,
                GameOption,
                IgnoreWarningsOption,
                VerboseLoggingOption,
                MergeLeveledListsOption,
                MergeLeveledCharactersOption,
                OpenEncounterZonesOption,
                ActorVisualAutoMergeOption,
                RaceVisualAutoMergeOption,
            };
            _ = command.Options.Remove(command.Options.OfType<VersionOption>().Single());
            return command;
        }
    }
}
