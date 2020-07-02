package skyrim.requiem.configuration

import com.typesafe.config.Config
import skyrim.requiem.configuration.ConfigReader.extract
import skyrim.requiem.exceptions.InvalidConfigurationException
import skyrim.requiem.fptools.Either

data class ArmorProcessingConfiguration(
    val armorRatingThresholds: ArmorRatingThresholdSection
) {

    companion object {

        val configKey = "armors"
        val tresholdSection = "$configKey.armorRatingThresholds"

        fun readFromConfig(
            config: Config,
            source: String
        ): Either<InvalidConfigurationException, ArmorProcessingConfiguration> {
            fun extractThresholds(config: Config, basePath: String): ArmorRatingThresholdConfiguration {
                return ArmorRatingThresholdConfiguration(
                    body = config.getInt("$basePath.body"),
                    feet = config.getInt("$basePath.feet"),
                    hands = config.getInt("$basePath.hands"),
                    head = config.getInt("$basePath.head"),
                    shield = config.getInt("$basePath.shield")

                )
            }

            return config.extract(source) {
                ArmorProcessingConfiguration(
                    armorRatingThresholds = ArmorRatingThresholdSection(
                        heavy = extractThresholds(config, "$tresholdSection.heavy"),
                        light = extractThresholds(config, "$tresholdSection.light")
                    )
                )
            }
        }
    }
}

data class ArmorRatingThresholdSection(
    val heavy: ArmorRatingThresholdConfiguration,
    val light: ArmorRatingThresholdConfiguration
)

data class ArmorRatingThresholdConfiguration(
    val body: Int,
    val feet: Int,
    val hands: Int,
    val head: Int,
    val shield: Int
)