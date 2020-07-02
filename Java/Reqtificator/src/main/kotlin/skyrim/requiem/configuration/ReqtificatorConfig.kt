package skyrim.requiem.configuration

import com.typesafe.config.Config
import skyrim.requiem.exceptions.InvalidConfigurationException
import skyrim.requiem.fptools.Either
import skyrim.requiem.fptools.flatMap

data class ReqtificatorConfig(
    val playerConfig: PlayerConfiguration,
    val ignoredWarnings: Set<IgnorableWarning>,
    val armors: ArmorProcessingConfiguration
) {
    companion object {

        operator fun invoke(config: Config): Either<InvalidConfigurationException, ReqtificatorConfig> {
            val source = "Reqtificator.conf"
            return IgnorableWarning.readFromConfig(config, source).flatMap { warnings ->
                ArmorProcessingConfiguration.readFromConfig(config, source).flatMap { armorConfiguration ->
                    PlayerConfiguration.readFromConfig(config, source).map { playerConfig ->
                        ReqtificatorConfig(
                            playerConfig = playerConfig,
                            ignoredWarnings = warnings,
                            armors = armorConfiguration
                        )
                    }
                }
            }
        }
    }
}