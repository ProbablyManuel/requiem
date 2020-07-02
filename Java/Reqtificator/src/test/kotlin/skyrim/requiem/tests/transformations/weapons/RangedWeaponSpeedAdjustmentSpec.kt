package skyrim.requiem.tests.transformations.weapons

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import skyproc.KeywordSet
import skyproc.WEAP
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.Weapon
import skyrim.requiem.fptools.None
import skyrim.requiem.fptools.Some
import skyrim.requiem.keywords
import skyrim.requiem.records.weaponClass
import skyrim.requiem.tests.WordSpecWithStaticMockks
import skyrim.requiem.transformations.weapons.RangedWeaponSpeedAdjustment

class RangedWeaponSpeedAdjustmentSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.records.WeaponKt", "skyrim.requiem.ExtensionsKt")

    init {

        class Fixture {
            val transformer = RangedWeaponSpeedAdjustment()
            val ctx = mockk<LoadOrderContent>()
        }

        val rangedWeapons = setOf(WEAP.WeaponType.Bow, WEAP.WeaponType.Crossbow)
        val meleeWeapons = WEAP.WeaponType.values().toSet() - rangedWeapons

        "An RangedWeaponSpeedAdjustment transformer" When {

            "filtering records" should {
                "ignore templated records" {
                    val f = Fixture()

                    val weaponWithTemplate = mockk<Weapon>()
                    every { weaponWithTemplate.isTemplated } returns true
                    every { weaponWithTemplate.weaponClass } returns Some(WEAP.WeaponType.Bow)
                    every { weaponWithTemplate.keywords } returns listOf()
                    every { weaponWithTemplate[WEAP.WeaponFlag.NonPlayable] } returns false

                    f.transformer.selector(weaponWithTemplate, f.ctx) shouldBe false
                }

                "ignore records that have no valid weapon type" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns None()
                    every { weapon.keywords } returns listOf()
                    every { weapon[WEAP.WeaponFlag.NonPlayable] } returns false

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                "ignore records that are flagged as unplayable" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Bow)
                    every { weapon.keywords } returns listOf()
                    every { weapon[WEAP.WeaponFlag.NonPlayable] } returns true

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                "ignore records marked as already reqtified" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Bow)
                    every { weapon.keywords } returns listOf(StaticReferences.Keywords.alreadyReqtified)
                    every { weapon[WEAP.WeaponFlag.NonPlayable] } returns false

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                "ignore records marked to not rescale range weapon speed" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Bow)
                    every { weapon.keywords } returns listOf(StaticReferences.Keywords.noWeaponSpeedRescaling)
                    every { weapon[WEAP.WeaponFlag.NonPlayable] } returns false

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                meleeWeapons.forEach {
                    "ignore $it melee weapons" {
                        val f = Fixture()

                        val weapon = mockk<Weapon>()
                        every { weapon.isTemplated } returns false
                        every { weapon.weaponClass } returns Some(it)
                        every { weapon.keywords } returns listOf()
                        every { weapon[WEAP.WeaponFlag.NonPlayable] } returns false

                        f.transformer.selector(weapon, f.ctx) shouldBe false
                    }
                }

                rangedWeapons.forEach {
                    "select $it weapons not matching any of the exclusion criteria" {
                        val f = Fixture()

                        val weapon = mockk<Weapon>()
                        every { weapon.isTemplated } returns false
                        every { weapon.weaponClass } returns Some(it)
                        every { weapon.keywords } returns listOf()
                        every { weapon[WEAP.WeaponFlag.NonPlayable] } returns false

                        f.transformer.selector(weapon, f.ctx) shouldBe true
                    }
                }
            }

            "transforming records" should {

                "set speed and appropriate keyword for bows" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    val keywords = mockk<KeywordSet>()
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Bow)
                    every { weapon.keywordSet } returns keywords
                    every { weapon.speed = range(0.3703F, 0.3705F) } returns Unit
                    every { keywords.addKeywordRef(StaticReferences.Keywords.weaponBowHeavy) } returns Unit

                    f.transformer.transformation(weapon, f.ctx)

                    verify(exactly = 1) {
                        weapon.speed = range(0.3703F, 0.3705F)
                        keywords.addKeywordRef(StaticReferences.Keywords.weaponBowHeavy)
                    }
                }

                "set speed and appropriate keyword for crossbows" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    val keywords = mockk<KeywordSet>()
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Crossbow)
                    every { weapon.speed = range(0.4444F, 0.4446F) } returns Unit
                    every { weapon.keywordSet } returns keywords
                    every { keywords.addKeywordRef(StaticReferences.Keywords.weaponCrossbowHeavy) } returns Unit

                    f.transformer.transformation(weapon, f.ctx)

                    verify(exactly = 1) {
                        weapon.speed = range(0.4444F, 0.4446F)
                        keywords.addKeywordRef(StaticReferences.Keywords.weaponCrossbowHeavy)
                    }
                }
            }
        }
    }
}