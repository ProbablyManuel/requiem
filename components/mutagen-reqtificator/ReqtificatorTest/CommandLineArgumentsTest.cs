using FluentAssertions;
using Mutagen.Bethesda;
using Reqtificator;
using Reqtificator.Configuration;
using Xunit;

namespace ReqtificatorTest
{
    public class CommandLineArgumentsTest
    {
        private static readonly UserSettings FromFile = new(
            VerboseLogging: false,
            MergeLeveledLists: true,
            MergeLeveledCharacters: true,
            OpenEncounterZones: true,
            ActorVisualAutoMerge: true,
            RaceVisualAutoMerge: true);

        [Fact]
        public void Should_open_the_window_without_arguments()
        {
            var arguments = CommandLineArguments.Parse([]);

            arguments.Headless.Should().BeFalse();
            arguments.ShowHelp.Should().BeFalse();
            arguments.IgnoreWarnings.Should().BeFalse();
            arguments.Release.Should().BeNull();
            arguments.ApplyOverrides(FromFile).Should().Be(FromFile);
            arguments.Errors.Should().BeEmpty();
        }

        [Theory]
        [InlineData("--game=SkyrimSE", GameRelease.SkyrimSE)]
        [InlineData("--game=SkyrimSEGog", GameRelease.SkyrimSEGog)]
        public void Should_recognize_the_game_release(string argument, GameRelease expected)
        {
            CommandLineArguments.Parse([argument]).Release.Should().Be(expected);
        }

        [Fact]
        public void Should_recognize_the_headless_flags()
        {
            var arguments = CommandLineArguments.Parse(["--headless", "--ignore-warnings"]);

            arguments.Headless.Should().BeTrue();
            arguments.IgnoreWarnings.Should().BeTrue();
            arguments.Errors.Should().BeEmpty();
        }

        [Theory]
        [InlineData("--help")]
        [InlineData("-h")]
        [InlineData("-?")]
        public void Should_recognize_help(string argument)
        {
            CommandLineArguments.Parse([argument]).ShowHelp.Should().BeTrue();
        }

        [Fact]
        public void Should_override_only_the_given_settings()
        {
            var arguments = CommandLineArguments.Parse(["--mergeLeveledLists=false", "--verboseLogging", "true"]);

            arguments.ApplyOverrides(FromFile).Should().Be(FromFile with
            {
                MergeLeveledLists = false,
                VerboseLogging = true,
            });
            arguments.Errors.Should().BeEmpty();
        }

        [Fact]
        public void Should_override_each_setting_on_its_own()
        {
            Overridden("--verboseLogging=true").Should().Be(FromFile with { VerboseLogging = true });
            Overridden("--mergeLeveledLists=false").Should().Be(FromFile with { MergeLeveledLists = false });
            Overridden("--mergeLeveledCharacters=false").Should().Be(FromFile with { MergeLeveledCharacters = false });
            Overridden("--openEncounterZones=false").Should().Be(FromFile with { OpenEncounterZones = false });
            Overridden("--actorVisualAutoMerge=false").Should().Be(FromFile with { ActorVisualAutoMerge = false });
            Overridden("--raceVisualAutoMerge=false").Should().Be(FromFile with { RaceVisualAutoMerge = false });
        }

        [Theory]
        [InlineData("--unknown")]
        [InlineData("--unknownOption=true")]
        [InlineData("--mergeLeveledLists=maybe")]
        [InlineData("--mergeLeveledLists")]
        [InlineData("--MergeLeveledLists=false")]
        [InlineData("--headless=false")]
        [InlineData("--game=Skyrim")]
        [InlineData("--game=Fallout4")]
        [InlineData("headless")]
        public void Should_report_invalid_arguments(string argument)
        {
            CommandLineArguments.Parse(["--headless", argument]).Errors.Should().NotBeEmpty();
        }

        [Fact]
        public void Should_keep_a_valid_game_release_when_other_arguments_are_invalid()
        {
            CommandLineArguments.Parse(["--game=SkyrimSEGog", "--unknown"]).Release.Should().Be(GameRelease.SkyrimSEGog);
        }

        [Fact]
        public void Should_ignore_an_invalid_game_release()
        {
            CommandLineArguments.Parse(["--game=Skyrim"]).Release.Should().BeNull();
        }

        private static UserSettings Overridden(string argument)
        {
            return CommandLineArguments.Parse([argument]).ApplyOverrides(FromFile);
        }
    }
}
