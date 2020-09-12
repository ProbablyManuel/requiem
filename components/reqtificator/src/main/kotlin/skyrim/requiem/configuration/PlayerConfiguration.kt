package skyrim.requiem.configuration

import com.typesafe.config.Config
import skyproc.FormID
import skyrim.requiem.configuration.ConfigReader.extract
import skyrim.requiem.exceptions.InvalidConfigurationException
import skyrim.requiem.fptools.Either

data class PlayerConfiguration(
    val healthOffset: Int,
    val magickaOffset: Int,
    val staminaOffset: Int,
    val spellsToRemove: List<FormID>
) {
    companion object {
        val configKey = "playerRecord"

        fun readFromConfig(config: Config, source: String): Either<InvalidConfigurationException, PlayerConfiguration> {
            return config.extract(source) {
                PlayerConfiguration(
                    healthOffset = config.getInt("$configKey.healthOffset"),
                    magickaOffset = config.getInt("$configKey.magickaOffset"),
                    staminaOffset = config.getInt("$configKey.staminaOffset"),
                    spellsToRemove = config.getStringList("$configKey.spellsToRemove").map {
                        val splitAt = it.indexOf(" ")
                        FormID(it.take(splitAt), it.drop(splitAt + 1))
                    }
                )
            }
        }
    }
}