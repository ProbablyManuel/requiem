package skyrim.requiem.configuration

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.ConfigValueFactory
import skyrim.requiem.configuration.ConfigReader.extract
import skyrim.requiem.exceptions.InvalidConfigurationException
import skyrim.requiem.fptools.Either
import java.io.File

enum class IgnorableWarning {
    EquippingOverhaulInstalled,
    BashedPatchUsed,
    RecommendedModCountExceeded,
    BugFixesSksePluginMissing,
    CrashFixesSksePluginMissing,
    CobbFixesSksePluginMissing;

    companion object {
        val configKey = "ignoredWarnings"

        fun writeIgnoredWarningsToFile(ignored: Set<IgnorableWarning>, file: File) {
            val node = values().fold(ConfigFactory.empty()) { config, warning ->
                config.withValue(warning.name.decapitalize(), ConfigValueFactory.fromAnyRef(warning in ignored))
            }
            file.writeText(node.atKey(configKey).root().render(ConfigRenderOptions.concise().setFormatted(true)))
        }

        fun readFromConfig(config: Config, source: String): Either<InvalidConfigurationException, Set<IgnorableWarning>> {
            return config.extract(source) {
                values().filter { config.getBoolean("$configKey.${it.name.decapitalize()}") }.toSet()
            }
        }
    }
}