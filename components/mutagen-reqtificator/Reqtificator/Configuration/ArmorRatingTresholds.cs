namespace Reqtificator.Configuration
{
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