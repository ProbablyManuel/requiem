package skyrim.requiem.localization

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.apache.logging.log4j.LogManager
import skyrim.requiem.fptools.None
import skyrim.requiem.fptools.Option
import skyrim.requiem.fptools.Some
import java.nio.file.Paths
import java.text.MessageFormat
import java.util.Locale

class TextFormatter(
    private val stringTable: Config,
    private val locale: Locale,
    private val fallbackStringTable: Config
) {
    private val logger = LogManager.getLogger()

    fun format(ref: TranslationReference): String {
        fun Config.findString(path: String): Option<String> {
            return if (this.hasPath(path)) {
                Some(this.getString(path))
            } else {
                logger.warn("path not found in string table: $path")
                None()
            }
        }

        return when (ref) {
            is TextReference -> stringTable.findString(ref.path).orElse { fallbackStringTable.findString(ref.path) }
            is UrlReference -> fallbackStringTable.findString(ref.path)
        }.map { templateString ->
            val formattedArgs = when (ref) {
                is TextReference -> {
                    ref.arguments.map {
                        when (it) {
                            is String -> it
                            is UrlReference -> format(it)
                            is TextReference -> format(it)
                            else -> it.toString()
                        }
                    }.toTypedArray()
                }
                is UrlReference -> arrayOf()
            }
            MessageFormat(templateString, locale).format(formattedArgs)
        }.getOrElse { "<TEXT NOT FOUND>" }
    }

    companion object {
        operator fun invoke(locale: Locale): TextFormatter {
            val logger = LogManager.getLogger()

            val englishTexts = ConfigFactory.parseResources("Reqtificator_EN.conf")
            val translationPath = Paths.get("translations", "Reqtificator_" + locale.language + ".conf")
            return if (translationPath.toFile().exists()) {
                val userLanguageTexts = ConfigFactory.parseFile(translationPath.toFile())
                logger.info("translated texts loaded: $translationPath")
                TextFormatter(userLanguageTexts, locale, englishTexts)
            } else {
                if (locale != Locale.ENGLISH) {
                    logger.info("translated texts not found: $translationPath")
                }
                TextFormatter(englishTexts, Locale.ENGLISH, englishTexts)
            }
        }
    }
}