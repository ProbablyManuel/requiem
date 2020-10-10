package skyrim.requiem.tests.transformations.armors

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import skyproc.Mod
import skyproc.genenums.ArmorType
import skyrim.requiem.Armor
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.configuration.ArmorProcessingConfiguration
import skyrim.requiem.configuration.ArmorRatingThresholdConfiguration
import skyrim.requiem.configuration.ArmorRatingThresholdSection
import skyrim.requiem.fptools.None
import skyrim.requiem.fptools.Some
import skyrim.requiem.keywords
import skyrim.requiem.records.armorType
import skyrim.requiem.tests.WordSpecWithStaticMockks
import skyrim.requiem.transformations.armors.ArmorRatingAdjustment

class ArmorRatingAdjustmentSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.records.ArmorKt", "skyrim.requiem.ExtensionsKt")

    init {

        class Fixture {
            val config = ArmorProcessingConfiguration(
                ArmorRatingThresholdSection(
                    heavy = ArmorRatingThresholdConfiguration(
                        body = 50,
                        feet = 100,
                        hands = 100,
                        head = 100,
                        shield = 100
                    ),
                    light = ArmorRatingThresholdConfiguration(
                        body = 100,
                        feet = 100,
                        hands = 100,
                        head = 100,
                        shield = 100
                    )
                )
            )
            val transformer = ArmorRatingAdjustment(config)
            val lastOverWrites = mockk<Mod>()
            val importHistory = mockk<List<Mod>>()
            val ctx = LoadOrderContent(importHistory, lastOverWrites)
        }

        "An ArmorRatingAdjustment transformer" When {

            "filtering records" should {
                "ignore templated records" {
                    val f = Fixture()

                    val armorWithTemplate = mockk<Armor>(relaxed = true)
                    every { armorWithTemplate.isTemplated } returns true
                    every { armorWithTemplate.armorType } returns Some(ArmorType.LIGHT)
                    every { armorWithTemplate.keywords } returns listOf()
                    every { armorWithTemplate.armorRatingFloat } returns 155.0f

                    f.transformer.selector(armorWithTemplate, f.ctx) shouldBe false
                }

                "ignore records that are flagged as clothing" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns Some(ArmorType.CLOTHING)
                    every { armor.keywords } returns listOf()

                    f.transformer.selector(armor, f.ctx) shouldBe false
                }

                "ignore records that have no valid armor type flag" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns None()
                    every { armor.keywords } returns listOf()
                    every { armor.armorRatingFloat } returns 155.0f

                    f.transformer.selector(armor, f.ctx) shouldBe false
                }

                "ignore records flagged to skip  armor rating rescaling" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns Some(ArmorType.LIGHT)
                    every { armor.keywords } returns listOf(StaticReferences.Keywords.noArmorRatingRescaling)
                    every { armor.armorRatingFloat } returns 5.0f

                    f.transformer.selector(armor, f.ctx) shouldBe false
                }

                "ignore records marked as already reqtified" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns Some(ArmorType.LIGHT)
                    every { armor.keywords } returns listOf(StaticReferences.Keywords.alreadyReqtified)
                    every { armor.armorRatingFloat } returns 155.0f

                    f.transformer.selector(armor, f.ctx) shouldBe false
                }

                "ignore records with an armor rating above the item-type specific treshold" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns Some(ArmorType.HEAVY)
                    every { armor.keywords } returns listOf(StaticReferences.Keywords.armorBody)
                    every { armor.armorRatingFloat } returns 55.0f

                    f.transformer.selector(armor, f.ctx) shouldBe false
                }

                "select records without item-type specific keywords regardless of their actual armor rating" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns Some(ArmorType.HEAVY)
                    every { armor.keywords } returns listOf()
                    every { armor.armorRatingFloat } returns 155.0f

                    f.transformer.selector(armor, f.ctx) shouldBe true
                }

                "select records which do not match any of the exclusion criteria" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns Some(ArmorType.LIGHT)
                    every { armor.keywords } returns listOf()
                    every { armor.armorRatingFloat } returns 55.0f

                    f.transformer.selector(armor, f.ctx) shouldBe true
                }
            }

            "transforming records" should {
                "scale light cuirass armor ratings by 3.3 and add a fixed 66 points on top" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.armorRatingFloat } returns 10.0F
                    every { armor.armorType } returns Some(ArmorType.LIGHT)
                    every { armor.keywords } returns listOf(StaticReferences.Keywords.armorBody)

                    f.transformer.transformation(armor, f.ctx)

                    verify(exactly = 1) {
                        armor.armorRatingFloat = range(98.95F, 99.05F)
                    }
                }

                "scale light non-cuirass armor ratings by 3.3 and add a fixed 18 points on top" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.armorRatingFloat } returns 10.0F
                    every { armor.armorType } returns Some(ArmorType.LIGHT)
                    every { armor.keywords } returns listOf()

                    f.transformer.transformation(armor, f.ctx)

                    verify(exactly = 1) {
                        armor.armorRatingFloat = range(50.95F, 51.05F)
                    }
                }

                "scale heavy cuirass armor ratings by 6.6 and add a fixed 66 points on top" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.armorRatingFloat } returns 10.0F
                    every { armor.armorType } returns Some(ArmorType.HEAVY)
                    every { armor.keywords } returns listOf(StaticReferences.Keywords.armorBody)

                    f.transformer.transformation(armor, f.ctx)

                    verify(exactly = 1) {
                        armor.armorRatingFloat = range(131.95F, 132.05F)
                    }
                }

                "scale heavy non-cuirass armor ratings by 6.6 and add a fixed 18 points on top" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.armorRatingFloat } returns 10.0F
                    every { armor.armorType } returns Some(ArmorType.HEAVY)
                    every { armor.keywords } returns listOf()

                    f.transformer.transformation(armor, f.ctx)

                    verify(exactly = 1) {
                        armor.armorRatingFloat = range(83.95F, 84.05F)
                    }
                }
            }
        }
    }
}