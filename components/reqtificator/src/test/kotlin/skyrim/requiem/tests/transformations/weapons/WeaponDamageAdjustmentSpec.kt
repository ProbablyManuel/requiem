package skyrim.requiem.tests.transformations.weapons

import io.kotest.matchers.shouldBe
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
import skyrim.requiem.transformations.weapons.WeaponDamageAdjustment

class WeaponDamageAdjustmentSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.records.WeaponKt", "skyrim.requiem.ExtensionsKt")

    init {

        class Fixture {
            val transformer = WeaponDamageAdjustment()
            val ctx = mockk<LoadOrderContent>()
        }

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

                "ignore records marked to not rescale damage" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Dagger)
                    every { weapon.keywords } returns listOf(StaticReferences.Keywords.noDamageRescaling)

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                WEAP.WeaponType.values().forEach {
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

                val rangedWeapons = setOf(WEAP.WeaponType.Bow, WEAP.WeaponType.Crossbow)
                val meleeWeapons = WEAP.WeaponType.values().toSet() - rangedWeapons

                meleeWeapons.forEach {
                    "scale damage by a factor of 6 for $it melee weapons" {
                        val f = Fixture()

                        val weapon = mockk<Weapon>()
                        every { weapon.weaponClass } returns Some(it)
                        every { weapon.damage } returns 10
                        every { weapon.damage = 60 } returns Unit

                        f.transformer.transformation(weapon, f.ctx)

                        verify(exactly = 1) {
                            weapon.damage = 60
                        }
                    }
                }

                rangedWeapons.forEach {
                    "scale damage by a factor of 4 for $it ranged weapons" {
                        val f = Fixture()

                        val weapon = mockk<Weapon>()
                        every { weapon.weaponClass } returns Some(it)
                        every { weapon.damage } returns 10
                        every { weapon.damage = 40 } returns Unit

                        f.transformer.transformation(weapon, f.ctx)

                        verify(exactly = 1) {
                            weapon.damage = 40
                        }
                    }
                }
            }
        }
    }
}