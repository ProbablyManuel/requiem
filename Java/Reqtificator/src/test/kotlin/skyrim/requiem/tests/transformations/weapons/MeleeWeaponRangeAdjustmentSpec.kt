package skyrim.requiem.tests.transformations.weapons

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import skyproc.WEAP
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.Weapon
import skyrim.requiem.fptools.None
import skyrim.requiem.fptools.Some
import skyrim.requiem.keywords
import skyrim.requiem.records.weaponClass
import skyrim.requiem.tests.WordSpecWithStaticMockks
import skyrim.requiem.transformations.weapons.MeleeWeaponRangeAdjustment

class MeleeWeaponRangeAdjustmentSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.records.WeaponKt", "skyrim.requiem.ExtensionsKt")

    init {

        class Fixture {
            val transformer = MeleeWeaponRangeAdjustment()
            val ctx = mockk<LoadOrderContent>()
        }

        val rangedWeapons = setOf(WEAP.WeaponType.Bow, WEAP.WeaponType.Crossbow)
        val meleeWeapons = WEAP.WeaponType.values().toSet() - rangedWeapons

        "An WeaponDamageAdjustment transformer" When {

            "filtering records" should {
                "ignore templated records" {
                    val f = Fixture()

                    val weaponWithTemplate = mockk<Weapon>()
                    every { weaponWithTemplate.isTemplated } returns true
                    every { weaponWithTemplate.weaponClass } returns Some(WEAP.WeaponType.Dagger)
                    every { weaponWithTemplate.keywords } returns listOf()

                    f.transformer.selector(weaponWithTemplate, f.ctx) shouldBe false
                }

                "ignore records that have no valid weapon type" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns None()
                    every { weapon.keywords } returns listOf()

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                "ignore records marked as already reqtified" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Dagger)
                    every { weapon.keywords } returns listOf(StaticReferences.Keywords.alreadyReqtified)

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                "ignore records marked to not rescale weapon reach" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Dagger)
                    every { weapon.keywords } returns listOf(StaticReferences.Keywords.noWeaponReachRescaling)

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                rangedWeapons.forEach {
                    "ignore $it ranged weapons" {
                        val f = Fixture()

                        val weapon = mockk<Weapon>()
                        every { weapon.isTemplated } returns false
                        every { weapon.weaponClass } returns Some(it)
                        every { weapon.keywords } returns listOf()

                        f.transformer.selector(weapon, f.ctx) shouldBe false
                    }
                }

                meleeWeapons.forEach {
                    "select $it weapons not matching any of the exclusion criteria" {
                        val f = Fixture()

                        val weapon = mockk<Weapon>()
                        every { weapon.isTemplated } returns false
                        every { weapon.weaponClass } returns Some(it)
                        every { weapon.keywords } returns listOf()

                        f.transformer.selector(weapon, f.ctx) shouldBe true
                    }
                }
            }

            "transforming records" should {

                "reduce reach by 30%" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.reach } returns 10.0F
                    every { weapon.reach = range(6.99F, 7.01F) } returns Unit

                    f.transformer.transformation(weapon, f.ctx)

                    verify(exactly = 1) {
                        weapon.reach = range(6.99F, 7.01F)
                    }
                }
            }
        }
    }
}