package skyrim.requiem.tests.transformations.ammunition

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import skyrim.requiem.Ammunition
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.keywords
import skyrim.requiem.tests.WordSpecWithStaticMockks
import skyrim.requiem.transformations.ammunition.AmmunitionDamageAdjustment

class AmmunitionDamageAdjustmentSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.records.WeaponKt", "skyrim.requiem.ExtensionsKt")

    init {

        class Fixture {
            val transformer = AmmunitionDamageAdjustment()
            val ctx = mockk<LoadOrderContent>()
        }

        "An AmmunitionDamageAdjustment transformer" When {

            "filtering records" should {

                "ignore records that have zero damage" {
                    val f = Fixture()

                    val ammo = mockk<Ammunition>()
                    every { ammo.damage } returns 0f
                    every { ammo.keywords } returns listOf()

                    f.transformer.selector(ammo, f.ctx) shouldBe false
                }

                "ignore records marked as already reqtified" {
                    val f = Fixture()

                    val ammo = mockk<Ammunition>()
                    every { ammo.damage } returns 1f
                    every { ammo.keywords } returns listOf(StaticReferences.Keywords.alreadyReqtified)

                    f.transformer.selector(ammo, f.ctx) shouldBe false
                }

                "ignore records marked to not rescale damage" {
                    val f = Fixture()

                    val ammo = mockk<Ammunition>()
                    every { ammo.damage } returns 1f
                    every { ammo.keywords } returns listOf(StaticReferences.Keywords.noDamageRescaling)

                    f.transformer.selector(ammo, f.ctx) shouldBe false
                }

                "select ammunition records not matching any of the exclusion criteria" {
                    val f = Fixture()

                    val ammo = mockk<Ammunition>()
                    every { ammo.damage } returns 1f
                    every { ammo.keywords } returns listOf()

                    f.transformer.selector(ammo, f.ctx) shouldBe true
                }
            }
        }

        "transforming records" should {

            "scale damage by a factor of 4" {
                val f = Fixture()

                val ammo = mockk<Ammunition>()
                every { ammo.damage } returns 10f
                every { ammo.damage = range(30.99f, 40.01f) } returns Unit

                f.transformer.transformation(ammo, f.ctx)

                verify(exactly = 1) {
                    ammo.damage = range(30.99f, 40.01f)
                }
            }
        }
    }
}