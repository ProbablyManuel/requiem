package skyrim.requiem.configuration

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.ConfigResolveOptions
import org.apache.logging.log4j.LogManager
import skyproc.Mod
import skyproc.ModListing
import skyrim.requiem.exceptions.InvalidConfigurationException
import skyrim.requiem.exceptions.SetupRequirementFailedException
import skyrim.requiem.fptools.Either
import skyrim.requiem.fptools.Left
import skyrim.requiem.fptools.Right
import skyrim.requiem.localization.TextReference
import skyrim.requiem.localization.UrlReference
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.file.Path
import java.nio.file.Paths

object ConfigReader {

    val userConfigPath: Path = Paths.get(".", "Data", "UserSettings.conf")

    private val logger = LogManager.getLogger()

    fun findRuleFiles(location: Path, baseName: String, loadOrder: List<Mod>): List<Path> {
        val requiem = ModListing("Requiem.esp")
        return loadOrder.dropWhile { it.info != requiem }
            .filter { it.masters.contains(requiem) || it == requiem }
            .map { location.resolve("${baseName}_${it.name}.conf") }
            .filter { it.toFile().exists() }
    }

    fun findConfigurationFiles(baseFolder: Path, fileName: Path, loadOrder: List<Mod>): List<Path> {
        val requiem = ModListing("Requiem.esp")
        return loadOrder.dropWhile { it.info != requiem }
            .filter { it.masters.contains(requiem) || it == requiem }
            .map { baseFolder.resolve(it.nameNoSuffix).resolve(fileName) }
            .filter { it.toFile().exists() }
    }

    fun loadAndMergeConfigFiles(toImport: List<Path>): Config {
        val imported = toImport.fold(ConfigFactory.empty()) { config, path ->
            try {
                ConfigFactory.parseFile(path.toFile()).withFallback(config)
            } catch (e: ConfigException) {
                throw SetupRequirementFailedException(
                    "patch.keywords.configReadError", path.toAbsolutePath().toString(),
                    e.localizedMessage
                )
            } catch (e: NullPointerException) {
                throw SetupRequirementFailedException(
                    "patch.keywords.configReadError", path.toAbsolutePath().toString(),
                    TextReference("patch.keywords.configReadNull")
                )
            }
        }
        return try {
            imported.resolve(ConfigResolveOptions.noSystem())
        } catch (e: ConfigException) {
            throw SetupRequirementFailedException(
                "patch.keywords.configResolveError", e.localizedMessage,
                UrlReference("urls.service_desk")
            )
        }
    }

    fun <T> Config.extract(source: String, extractor: (Config) -> T): Either<InvalidConfigurationException, T> {
        fun buildException(e: ConfigException, advice: Any): InvalidConfigurationException {
            return InvalidConfigurationException(
                "error_handling.configParsingFailure",
                source,
                e.localizedMessage,
                advice,
                UrlReference("urls.reqtificatorConfig")
            )
        }

        return try {
            Right(extractor(this))
        } catch (e: ConfigException) {
            Left(
                when (e) {
                    is ConfigException.Missing -> buildException(e, TextReference("error_handling.missingConfigValue"))
                    is ConfigException.WrongType -> buildException(e, TextReference("error_handling.wrongConfigValue"))
                    else -> buildException(e, "")
                }
            )
        }
    }

    fun dumpConfigurationToFile(config: Config, fileName: String) {
        try {
            BufferedWriter(OutputStreamWriter(FileOutputStream(fileName), "utf-8")).use { writer ->
                val opts = ConfigRenderOptions.defaults().setOriginComments(true)
                writer.write(config.root().render(opts))
            }
        } catch (e: ConfigException) {
            logger.error("failed to print merged configuration file", e)
        } catch (e: IOException) {
            logger.error("failed to print merged configuration file", e)
        }
    }
}