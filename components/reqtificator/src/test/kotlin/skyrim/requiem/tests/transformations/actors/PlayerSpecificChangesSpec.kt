package skyrim.requiem.tests.transformations.actors

import com.typesafe.config.ConfigFactory
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import skyproc.FormID
import skyproc.Mod
import skyrim.requiem.Actor
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.configuration.PlayerConfiguration
import skyrim.requiem.staminaOffset
import skyrim.requiem.tests.right
import skyrim.requiem.transformations.actors.PlayerSpecificChanges

class PlayerSpecificChangesSpec : WordSpec() {
    init {

        class Fixture {
            val spellToRemove1 = FormID("012FCD", "Skyrim.esm")
            val spellToRemove2 = FormID("012FCC", "Skyrim.esm")
            val rawConfig = ConfigFactory.parseString("""
                playerRecord {
                  healthOffset = -13
                  magickaOffset = 42
                  staminaOffset = 666
                  spellsToRemove = [
                    "012FCD Skyrim.esm",
                    "012FCC Skyrim.esm"
                  ]
                }
            """.trimIndent())
            val config = PlayerConfiguration.readFromConfig(rawConfig, "foo").right()
            val transformer = PlayerSpecificChanges(config)
            val lastOverWrites = mockk<Mod>()
            val importHistory = mockk<List<Mod>>()
            val ctx = LoadOrderContent(importHistory, lastOverWrites)
        }

        "A PlayerSpecificChanges transformer" When {

            "filtering records" should {
                "accept the player record" {
                    val f = Fixture()

                    val player = mockk<Actor>(relaxed = true)
                    every { player.form } returns FormID("000007", "Skyrim.esm")

                    f.transformer.selector(player, f.ctx) shouldBe true
                }

                "ignore others record from Skyrim.esm" {
                    val f = Fixture()

                    val notPlayer = mockk<Actor>(relaxed = true)
                    every { notPlayer.form } returns FormID("000666", "Skyrim.esm")

                    f.transformer.selector(notPlayer, f.ctx) shouldBe false
                }

                "ignore records from other plugins" {
                    val f = Fixture()

                    val notPlayer = mockk<Actor>(relaxed = true)
                    every { notPlayer.form } returns FormID("000007", "Dawnguard.esm")

                    f.transformer.selector(notPlayer, f.ctx) shouldBe false
                }
            }

            "transforming the player record" should {
                "apply the attribute offsets specified in the configuration" {
                    val f = Fixture()

                    val player = mockk<Actor>(relaxed = true)
                    every { player.form } returns FormID("000007", "Skyrim.esm")

                    val result = f.transformer.transformation(player, f.ctx)

                    result shouldBe player

                    verify {
                        player.healthOffset = -13
                        player.magickaOffset = 42
                        player.staminaOffset = 666
                    }
                }

                "remove the starting spells specified in the configuration" {
                    val f = Fixture()

                    val player = mockk<Actor>(relaxed = true)
                    every { player.form } returns FormID("000007", "Skyrim.esm")

                    val result = f.transformer.transformation(player, f.ctx)

                    result shouldBe player

                    verify(exactly = 1) {
                        player.removeSpell(f.spellToRemove1)
                        player.removeSpell(f.spellToRemove2)
                    }
                }
            }
        }
    }
}