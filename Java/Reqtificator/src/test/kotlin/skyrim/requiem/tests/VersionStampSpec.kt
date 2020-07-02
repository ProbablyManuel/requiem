package skyrim.requiem.tests

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import skyrim.requiem.VersionStamp

class VersionStampSpec : WordSpec() {

    init {

        "A VersionStamp" When {

            "parsing input data" should {

                "return a valid version stamp if the input is valid" {
                    val input = 30402

                    VersionStamp(input) shouldBe VersionStamp(3, 4, 2)
                }

                "throw an error if the input is invalid" {
                    val input = -30402

                    shouldThrow<IllegalArgumentException> { VersionStamp(input) }
                }
            }

            "formatting the version number for printing" should {
                "yield a correctly formatted version number" {
                    val stamp = VersionStamp(3, 4, 2)

                    stamp.toString() shouldBe "3.4.2"
                }
            }
        }
    }
}
