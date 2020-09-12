package Reqtificator.logging_and_gui;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;

public class TextManager {

    private final static Logger logger = LogManager.getLogger();

    private final Config englishTexts;
    private final Config userLanguageTexts;
    private final Locale language;

    public TextManager(String englishFile, Locale language,
                       Path translatedFile) {
        englishTexts = ConfigFactory.parseResources(englishFile);
        if (translatedFile.toFile().exists()) {
            userLanguageTexts = ConfigFactory.parseFile(
                    translatedFile.toFile());
            logger.info("translated texts loaded: {}",
                    translatedFile.toString());
        } else {
            userLanguageTexts = englishTexts;
            if (!language.equals(Locale.ENGLISH)) {
                logger.warn("translated texts not found: {}",
                        translatedFile.toString());
            }
        }
        this.language = language;
    }

    public String format(String key, String... arguments) {
        return format(key, false, arguments);
    }

    public String format(String key, boolean useInternal, String... arguments) {
        String rawText;
        try {
            if (useInternal) {
                rawText = englishTexts.getString(key);
            } else {
                rawText = userLanguageTexts.getString(key);
            }
            MessageFormat format = new MessageFormat(rawText, language);
            return format.format(arguments);
        } catch (ConfigException.Missing ex) {
            logger.error("path not found in string table: {}", key, ex);
            if (englishTexts.hasPath(key)) {
                rawText = englishTexts.getString(key);
                MessageFormat format = new MessageFormat(rawText, language);
                return format.format(arguments);
            } else {
                return "<TEXT NOT FOUND>";
            }
        }
    }
}
