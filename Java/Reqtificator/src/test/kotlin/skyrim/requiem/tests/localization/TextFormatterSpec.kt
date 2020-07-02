package skyrim.requiem.tests.localization

import com.typesafe.config.ConfigFactory
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import skyrim.requiem.localization.TextFormatter
import skyrim.requiem.localization.TextReference
import skyrim.requiem.localization.UrlReference
import java.util.Locale

class TextFormatterSpec : WordSpec() {
    init {

        val localConfig = ConfigFactory.parseString(
            """
                |foo.bar = DE
                |bla.rules = "{0} rules"
                |bla.multi = "{1} or {0}?"
            """.trimMargin()
        )
        val fallbackConfig = ConfigFactory.parseString(
            """
                |foo.bar = EN
                |foo.gus = requiem 
                |bla.rules = "{0} rules"
            """.trimMargin()
        )
        class TestDummy {
            override fun toString(): String = "fancy magic"
        }

        "A TextFormatter" When {

            "loading a text template for a TextReference" should {

                "return the value from the string table for the user's language if available" {
                    val formatter = TextFormatter(localConfig, Locale("de"), fallbackConfig)

                    formatter.format(TextReference("foo.bar")) shouldBe "DE"
                }

                "return the value from the default string table if the key is missing in one for the user's language" {
                    val formatter = TextFormatter(localConfig, Locale("de"), fallbackConfig)

                    formatter.format(TextReference("foo.gus")) shouldBe "requiem"
                }

                "return a place holder if neither user nor fallback string table have the key" {
                    val formatter = TextFormatter(localConfig, Locale("de"), fallbackConfig)

                    formatter.format(TextReference("foo.skyrim")) shouldBe "<TEXT NOT FOUND>"
                }
            }

            "loading a text template for an UrlReference" should {

                "return the value from the default string table even if the user's language string table has the key" {
                    val formatter = TextFormatter(localConfig, Locale("de"), fallbackConfig)

                    formatter.format(UrlReference("foo.bar")) shouldBe "EN"
                }

                "return the value from the default string table if the key is missing in one for the user's language" {
                    val formatter = TextFormatter(localConfig, Locale("de"), fallbackConfig)

                    formatter.format(UrlReference("foo.gus")) shouldBe "requiem"
                }

                "return a place holder if neither user nor fallback string table have the key" {
                    val formatter = TextFormatter(localConfig, Locale("de"), fallbackConfig)

                    formatter.format(UrlReference("foo.skyrim")) shouldBe "<TEXT NOT FOUND>"
                }
            }

            "formatting a message template with provided values" should {

                "insert strings unchanged into the template" {
                    val formatter = TextFormatter(localConfig, Locale("de"), fallbackConfig)

                    formatter.format(TextReference("bla.rules", "requiem")) shouldBe "requiem rules"
                }

                "insert the resolved text value for TextReferences into the template" {
                    val formatter = TextFormatter(localConfig, Locale("de"), fallbackConfig)

                    formatter.format(TextReference("bla.rules", TextReference("foo.bar"))) shouldBe "DE rules"
                }

                "insert resolved text value for UrlReferences into the template" {
                    val formatter = TextFormatter(localConfig, Locale("de"), fallbackConfig)

                    formatter.format(TextReference("bla.rules", UrlReference("foo.bar"))) shouldBe "EN rules"
                }

                "insert any other object's toString() representation into the template" {
                    val formatter = TextFormatter(localConfig, Locale("de"), fallbackConfig)

                    formatter.format(TextReference("bla.rules", TestDummy())) shouldBe "fancy magic rules"
                }

                "insert multiple arguments correctly into the template" {
                    val formatter = TextFormatter(localConfig, Locale("de"), fallbackConfig)

                    formatter.format(TextReference("bla.multi", "skyrim", "requiem")) shouldBe "requiem or skyrim?"
                }
            }
        }
    }
}