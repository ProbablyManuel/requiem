using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Hocon;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Xunit;

namespace ReqtificatorTest.Configuration
{
    public class ReqtificatorConfigTest
    {
        private const string BaseConfigRaw = @"
            playerRecord {
                healthOffset = 0
                magickaOffset = 1
                staminaOffset = 2
                spellsToRemove = [
                    ""012FCD:Skyrim.esm"",
                    ""012FCC:Skyrim.esm""
                ]
            }
            armors {
                armorRatingThresholds {
                    heavy {
                        body = 74
                        feet = 27
                        hands = 27
                        head = 35
                        shield = 54
                    }
                    light {
                        body = 62
                        feet = 18
                        hands = 18
                        head = 26
                        shield = 44
                    }
                }
            }";

        private static readonly ReqtificatorConfig ExpectedBase = new ReqtificatorConfig(
            PlayerConfig: new PlayerConfig(
                HealthOffset: 0,
                MagickaOffset: 1,
                StaminaOffset: 2,
                SpellsToRemove: new List<IFormLinkGetter<ISpellGetter>>
                {
                    new FormLink<ISpellGetter>(FormKey.Factory("012FCD:Skyrim.esm")),
                    new FormLink<ISpellGetter>(FormKey.Factory("012FCC:Skyrim.esm"))
                }.ToImmutableList()
            ),
            ArmorSettings: new ArmorPatchingConfiguration(
                HeavyArmorRatingThresholds: new ArmorRatingThresholds(
                    Body: 74,
                    Feet: 27,
                    Hands: 27,
                    Head: 35,
                    Shield: 54
                ),
                LightArmorRatingThresholds: new ArmorRatingThresholds(
                    Body: 62,
                    Feet: 18,
                    Hands: 18,
                    Head: 26,
                    Shield: 44
                )
            )
        );

        private static readonly Config BaseConfig = HoconConfigurationFactory.ParseString(BaseConfigRaw);

        [Fact]
        public void Should_parse_the_default_config_successfully()
        {
            var parsedConfig = ReqtificatorConfig.ParseConfig(new[] { BaseConfig });
            parsedConfig.ArmorSettings.Should().Be(ExpectedBase.ArmorSettings);
            parsedConfig.PlayerConfig.SpellsToRemove.Should().Contain(ExpectedBase.PlayerConfig.SpellsToRemove);
            parsedConfig.PlayerConfig.HealthOffset.Should().Be(ExpectedBase.PlayerConfig.HealthOffset);
            parsedConfig.PlayerConfig.MagickaOffset.Should().Be(ExpectedBase.PlayerConfig.MagickaOffset);
            parsedConfig.PlayerConfig.StaminaOffset.Should().Be(ExpectedBase.PlayerConfig.StaminaOffset);
        }

        [Fact]
        public void Should_correctly_apply_partial_overrides_from_extra_config_files()
        {
            var extraConfig = HoconConfigurationFactory.ParseString("{playerRecord.healthOffset = 42}");
            var parsedConfig = ReqtificatorConfig.ParseConfig(new[] { extraConfig, BaseConfig });
            parsedConfig.PlayerConfig.HealthOffset.Should().Be(42);
            parsedConfig.PlayerConfig.MagickaOffset.Should().Be(ExpectedBase.PlayerConfig.MagickaOffset);
            parsedConfig.PlayerConfig.StaminaOffset.Should().Be(ExpectedBase.PlayerConfig.StaminaOffset);
        }

        [Fact]
        public void Should_determine_winning_overrides_for_each_field_individually()
        {
            var lastConfig = HoconConfigurationFactory.ParseString("{playerRecord.healthOffset = 42}");
            var intermediateConfig = HoconConfigurationFactory.ParseString(
                "{playerRecord.healthOffset = 13, playerRecord.magickaOffset = -25}");
            var parsedConfig = ReqtificatorConfig.ParseConfig(new[] { lastConfig, intermediateConfig, BaseConfig });
            parsedConfig.PlayerConfig.HealthOffset.Should().Be(42);
            parsedConfig.PlayerConfig.MagickaOffset.Should().Be(-25);
            parsedConfig.PlayerConfig.StaminaOffset.Should().Be(ExpectedBase.PlayerConfig.StaminaOffset);
        }

        [Fact]
        public void Should_parse_skyproc_style_formIds_correctly()
        {
            var extraConfig = HoconConfigurationFactory.ParseString(
                @"{playerRecord.spellsToRemove = [""012FCD Skyrim.esm"",""012FCC Skyrim.esm""]}");
            var parsedConfig = ReqtificatorConfig.ParseConfig(new[] { extraConfig, BaseConfig });
            parsedConfig.PlayerConfig.SpellsToRemove.Should().Contain(ExpectedBase.PlayerConfig.SpellsToRemove);
        }
    }
}