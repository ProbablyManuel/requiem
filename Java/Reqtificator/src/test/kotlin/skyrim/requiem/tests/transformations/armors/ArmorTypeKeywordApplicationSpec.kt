package skyrim.requiem.tests.transformations.armors

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import skyproc.KeywordSet
import skyproc.Mod
import skyproc.genenums.ArmorType
import skyrim.requiem.Armor
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.fptools.None
import skyrim.requiem.fptools.Some
import skyrim.requiem.keywords
import skyrim.requiem.records.armorType
import skyrim.requiem.tests.WordSpecWithStaticMockks
import skyrim.requiem.transformations.armors.ArmorTypeKeywordApplication

class ArmorTypeKeywordApplicationSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.records.ArmorKt", "skyrim.requiem.ExtensionsKt")

    init {

        class Fixture {
            val transformer = ArmorTypeKeywordApplication()
            val lastOverWrites = mockk<Mod>()
            val importHistory = mockk<List<Mod>>()
            val keywords = mockk<KeywordSet>(relaxUnitFun = true)
            val ctx = LoadOrderContent(importHistory, lastOverWrites)
        }

        "An ArmorTypeKeywordApplication transformer" When {

            "filtering records" should {
                "ignore templated records" {
                    val f = Fixture()

                    val armorWithTemplate = mockk<Armor>(relaxed = true)
                    every { armorWithTemplate.isTemplated } returns true
                    every { armorWithTemplate.armorType } returns Some(ArmorType.LIGHT)

                    f.transformer.selector(armorWithTemplate, f.ctx) shouldBe false
                }

                "ignore records that are flagged as clothing" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns Some(ArmorType.CLOTHING)

                    f.transformer.selector(armor, f.ctx) shouldBe false
                }

                "ignore records that have no valid armor type flag" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns None()

                    f.transformer.selector(armor, f.ctx) shouldBe false
                }

                "ignore light armor records that already have the armor type keyword" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns Some(ArmorType.LIGHT)
                    every { armor.keywords } returns listOf(StaticReferences.Keywords.armorLight)

                    f.transformer.selector(armor, f.ctx) shouldBe false
                }

                "ignore heavy armor records that already have the armor type keyword" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns Some(ArmorType.HEAVY)
                    every { armor.keywords } returns listOf(StaticReferences.Keywords.armorHeavy)

                    f.transformer.selector(armor, f.ctx) shouldBe false
                }

                "select records which do not have a template defined" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { armor.armorType } returns Some(ArmorType.LIGHT)
                    every { armor.keywords } returns listOf()

                    f.transformer.selector(armor, f.ctx) shouldBe true
                }
            }

            "transforming records" should {
                "add the appropriate keyword to light armor parts" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.armorType } returns Some(ArmorType.LIGHT)
                    every { armor.keywordSet } returns f.keywords

                    f.transformer.transformation(armor, f.ctx)

                    verify(exactly = 1) {
                        f.keywords.addKeywordRef(StaticReferences.Keywords.armorLight)
                    }
                }

                "add the appropriate keyword to heavy armor parts" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.armorType } returns Some(ArmorType.HEAVY)
                    every { armor.keywordSet } returns f.keywords

                    f.transformer.transformation(armor, f.ctx)

                    verify(exactly = 1) {
                        f.keywords.addKeywordRef(StaticReferences.Keywords.armorHeavy)
                    }
                }
            }
        }
    }
}