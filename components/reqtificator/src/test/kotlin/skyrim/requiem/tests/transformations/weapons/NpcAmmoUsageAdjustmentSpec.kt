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
import skyrim.requiem.transformations.weapons.NpcAmmoUsageAdjustment

class NpcAmmoUsageAdjustmentSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.records.WeaponKt", "skyrim.requiem.ExtensionsKt")

    init {

        class Fixture {
            val transformer = NpcAmmoUsageAdjustment()
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
                    every { weaponWithTemplate[WEAP.WeaponFlag.NPCsUseAmmo] } returns false

                    f.transformer.selector(weaponWithTemplate, f.ctx) shouldBe false
                }

                "ignore records that have no valid weapon type" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns None()
                    every { weapon.keywords } returns listOf()
                    every { weapon[WEAP.WeaponFlag.NonPlayable] } returns false
                    every { weapon[WEAP.WeaponFlag.NPCsUseAmmo] } returns false

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                "ignore records that are flagged as non-playable" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Bow)
                    every { weapon.keywords } returns listOf()
                    every { weapon[WEAP.WeaponFlag.NonPlayable] } returns true
                    every { weapon[WEAP.WeaponFlag.NPCsUseAmmo] } returns false

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                "ignore records that are already flagged to have NPCs use ammo" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Bow)
                    every { weapon.keywords } returns listOf()
                    every { weapon[WEAP.WeaponFlag.NonPlayable] } returns false
                    every { weapon[WEAP.WeaponFlag.NPCsUseAmmo] } returns true

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                "ignore records marked as already reqtified" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Bow)
                    every { weapon.keywords } returns listOf(StaticReferences.Keywords.alreadyReqtified)
                    every { weapon[WEAP.WeaponFlag.NonPlayable] } returns false
                    every { weapon[WEAP.WeaponFlag.NPCsUseAmmo] } returns false

                    f.transformer.selector(weapon, f.ctx) shouldBe false
                }

                "ignore records marked to not rescale ranged weapon speed" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon.isTemplated } returns false
                    every { weapon.weaponClass } returns Some(WEAP.WeaponType.Bow)
                    every { weapon.keywords } returns listOf(StaticReferences.Keywords.noWeaponSpeedRescaling)
                    every { weapon[WEAP.WeaponFlag.NonPlayable] } returns false
                    every { weapon[WEAP.WeaponFlag.NPCsUseAmmo] } returns false

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
                        every { weapon[WEAP.WeaponFlag.NPCsUseAmmo] } returns false

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
                        every { weapon[WEAP.WeaponFlag.NPCsUseAmmo] } returns false

                        f.transformer.selector(weapon, f.ctx) shouldBe true
                    }
                }
            }

            "transforming records" should {

                "set the flag for NPCs to use ammo" {
                    val f = Fixture()

                    val weapon = mockk<Weapon>()
                    every { weapon[WEAP.WeaponFlag.NPCsUseAmmo] = true } returns Unit

                    f.transformer.transformation(weapon, f.ctx)

                    verify(exactly = 1) {
                        weapon[WEAP.WeaponFlag.NPCsUseAmmo] = true
                    }
                }
            }
        }
    }
}