using System.Collections.Generic;
using System.Collections.Immutable;
using System.IO;
using System.Linq;
using Hocon;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Events;
using Serilog;

namespace Reqtificator.Configuration
{
    internal record ReqtificatorConfig(PlayerConfig PlayerConfig, ArmorPatchingConfiguration ArmorSettings)
    {
        public static ReqtificatorConfig ParseConfig(IReadOnlyList<Config> configs)
        {
            var fallbacks = configs.Skip(1);
            var mergedConfig = fallbacks.Aggregate(configs.First(), (acc, conf) => acc.WithFallback(conf));

            return new ReqtificatorConfig(
                PlayerConfig: new PlayerConfig(
                    HealthOffset: mergedConfig.GetInt("playerRecord.healthOffset"),
                    MagickaOffset: mergedConfig.GetInt("playerRecord.magickaOffset"),
                    StaminaOffset: mergedConfig.GetInt("playerRecord.staminaOffset"),
                    SpellsToRemove: mergedConfig.GetStringList("playerRecord.spellsToRemove")
                        // backwards compatibility with old SkyProc notation (space instead of Colon)
                        .Select(f => (f[6] == ' ') ? f[..6] + ':' + f[7..] : f)
                        .Select(it => new FormLink<Spell>(FormKey.Factory(it))).ToImmutableList()
                ),
                ArmorSettings: new ArmorPatchingConfiguration(
                    HeavyArmorRatingThresholds: new ArmorRatingThresholds(
                        Body: mergedConfig.GetInt("armors.armorRatingThresholds.heavy.body"),
                        Feet: mergedConfig.GetInt("armors.armorRatingThresholds.heavy.feet"),
                        Hands: mergedConfig.GetInt("armors.armorRatingThresholds.heavy.hands"),
                        Head: mergedConfig.GetInt("armors.armorRatingThresholds.heavy.head"),
                        Shield: mergedConfig.GetInt("armors.armorRatingThresholds.heavy.shield")
                    ),
                    LightArmorRatingThresholds: new ArmorRatingThresholds(
                        Body: mergedConfig.GetInt("armors.armorRatingThresholds.light.body"),
                        Feet: mergedConfig.GetInt("armors.armorRatingThresholds.light.feet"),
                        Hands: mergedConfig.GetInt("armors.armorRatingThresholds.light.hands"),
                        Head: mergedConfig.GetInt("armors.armorRatingThresholds.light.head"),
                        Shield: mergedConfig.GetInt("armors.armorRatingThresholds.light.shield")
                    )
                )
            );
        }

        public static ReqtificatorConfig LoadFromConfigs(string baseFolder,
            ImmutableList<ILoadOrderListingGetter> activeMods,
            InternalEvents _events)
        {
            var rawConfigs = activeMods
                .Select(m => Path.Combine(baseFolder, m.ModKey.FileName.NameWithoutExtension, "Reqtificator.conf"))
                .Where(File.Exists)
                .WithIndex()
                .Tap(f => Log.Information($"loading Reqtificator config (priority {f.Index + 1}) from '{f.Item}'"))
                .Select(f => HoconConfigurationFactory.FromFile(f.Item))
                .ToImmutableList();

            if (rawConfigs.Count == 0) { _events.PublishState(ReqtificatorState.Stopped(Events.Outcomes.ReqtificatorOutcome.MissingRequiemConfig)); }

            return ParseConfig(rawConfigs);
        }
    }

    public record PlayerConfig(int HealthOffset, int MagickaOffset, int StaminaOffset,
        IReadOnlyList<IFormLinkGetter<ISpellGetter>> SpellsToRemove);

    public record ArmorPatchingConfiguration(
        ArmorRatingThresholds HeavyArmorRatingThresholds,
        ArmorRatingThresholds LightArmorRatingThresholds
    );

    public record ArmorRatingThresholds(
        float Body,
        float Feet,
        float Hands,
        float Head,
        float Shield
    );
}