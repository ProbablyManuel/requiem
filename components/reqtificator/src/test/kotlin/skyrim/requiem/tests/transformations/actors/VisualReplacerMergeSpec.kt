package skyrim.requiem.tests.transformations.actors

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import skyproc.FormID
import skyproc.MajorRecord
import skyproc.Mod
import skyproc.ModListing
import skyproc.NPC_
import skyrim.requiem.Actor
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.tests.WordSpecWithStaticMockks
import skyrim.requiem.transformations.actors.VisualReplacerMerge
import skyrim.requiem.transformations.actors.copyAttackdata
import skyrim.requiem.transformations.actors.copyTraits

class VisualReplacerMergeSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.transformations.actors.ActorCopyToolsKt")

    init {
        class Fixture {
            val requiem = ModListing("Requiem.esp")
            val requiemPlugin = ModListing("Subquiem.esp")
            val provider = ModListing("Optics.esm")
            val transformer = VisualReplacerMerge(setOf(provider))
            val lastOverWrites = mockk<Mod>()
            val import1 = mockk<Mod>()
            val import2 = mockk<Mod>()
            val import3 = mockk<Mod>()
            val ctx = LoadOrderContent(listOf(import1, import2, import3), lastOverWrites)
            val history1 = mockk<Actor>()
            val history2 = mockk<Actor>()
            val history3 = mockk<Actor>()
            val history: ArrayList<MajorRecord> = arrayListOf(history1, history2, history3)
        }

        "A VisualReplacerMerge transformer" When {

            "filtering records" should {
                "ignore the player record" {
                    val f = Fixture()

                    every { f.history3.form } returns FormID("000007", "Skyrim.esm")
                    every { f.history3.recordHistory } returns f.history
                    every { f.import1.info } returns ModListing("foo.esp")
                    every { f.import2.info } returns f.requiem
                    every { f.import3.info } returns f.provider
                    every { f.import1.masters } returns arrayListOf()
                    every { f.import2.masters } returns arrayListOf()
                    every { f.import3.masters } returns arrayListOf()
                    every { f.history1.modImportedFrom } returns ModListing("foo.esp")
                    every { f.history2.modImportedFrom } returns f.requiem
                    every { f.history3.modImportedFrom } returns f.provider

                    f.transformer.selector(f.history3, f.ctx) shouldBe false
                }

                "ignore records which are not modified by Requiem or its dependencies (any load order index)" {
                    val f = Fixture()

                    every { f.history3.form } returns FormID("000666", "Skyrim.esm")
                    every { f.history3.recordHistory } returns f.history
                    every { f.import1.info } returns ModListing("foo.esp")
                    every { f.import2.info } returns ModListing("bar.esp")
                    every { f.import3.info } returns f.provider
                    every { f.import1.masters } returns arrayListOf()
                    every { f.import2.masters } returns arrayListOf()
                    every { f.import3.masters } returns arrayListOf()
                    every { f.history1.modImportedFrom } returns ModListing("foo.esp")
                    every { f.history2.modImportedFrom } returns ModListing("bar.esp")
                    every { f.history3.modImportedFrom } returns f.provider

                    f.transformer.selector(f.history3, f.ctx) shouldBe false
                }

                "ignore records which are not modified by any visual template provider mod (any load order index)" {
                    val f = Fixture()

                    every { f.history3.form } returns FormID("000666", "Skyrim.esm")
                    every { f.history3.recordHistory } returns f.history
                    every { f.import1.info } returns ModListing("foo.esp")
                    every { f.import2.info } returns ModListing("bar.esp")
                    every { f.import3.info } returns f.requiem
                    every { f.import1.masters } returns arrayListOf()
                    every { f.import2.masters } returns arrayListOf()
                    every { f.import3.masters } returns arrayListOf()
                    every { f.history1.modImportedFrom } returns ModListing("foo.esp")
                    every { f.history2.modImportedFrom } returns ModListing("bar.esp")
                    every { f.history3.modImportedFrom } returns f.requiem

                    f.transformer.selector(f.history3, f.ctx) shouldBe false
                }

                "ignore records whose last overwrite is neither a Requiem plugin nor a visual template provider" {
                    val f = Fixture()

                    every { f.history3.form } returns FormID("000666", "Skyrim.esm")
                    every { f.history3.recordHistory } returns f.history
                    every { f.import1.info } returns f.requiem
                    every { f.import2.info } returns f.provider
                    every { f.import3.info } returns ModListing("foo.esp")
                    every { f.import1.masters } returns arrayListOf()
                    every { f.import2.masters } returns arrayListOf()
                    every { f.import3.masters } returns arrayListOf()
                    every { f.history1.modImportedFrom } returns f.requiem
                    every { f.history2.modImportedFrom } returns f.provider
                    every { f.history3.modImportedFrom } returns ModListing("foo.esp")

                    f.transformer.selector(f.history3, f.ctx) shouldBe false
                }

                "select records last edited by Requiem with a previously loaded visual data provider" {
                    val f = Fixture()

                    every { f.history3.form } returns FormID("000666", "Skyrim.esm")
                    every { f.history3.recordHistory } returns f.history
                    every { f.import1.info } returns ModListing("foo.esp")
                    every { f.import2.info } returns f.provider
                    every { f.import3.info } returns f.requiem
                    every { f.import1.masters } returns arrayListOf()
                    every { f.import2.masters } returns arrayListOf()
                    every { f.import3.masters } returns arrayListOf()
                    every { f.history1.modImportedFrom } returns ModListing("foo.esp")
                    every { f.history2.modImportedFrom } returns f.provider
                    every { f.history3.modImportedFrom } returns f.requiem

                    f.transformer.selector(f.history3, f.ctx) shouldBe true
                }

                "select records last edited by a Requiem-plugin with a previously loaded visual data provider" {
                    val f = Fixture()

                    every { f.history3.form } returns FormID("000666", "Skyrim.esm")
                    every { f.history3.recordHistory } returns f.history
                    every { f.import1.info } returns ModListing("foo.esp")
                    every { f.import2.info } returns f.provider
                    every { f.import3.info } returns f.requiemPlugin
                    every { f.import1.masters } returns arrayListOf()
                    every { f.import2.masters } returns arrayListOf()
                    every { f.import3.masters } returns arrayListOf()
                    every { f.history1.modImportedFrom } returns ModListing("foo.esp")
                    every { f.history2.modImportedFrom } returns f.provider
                    every { f.history3.modImportedFrom } returns f.requiemPlugin
                    every { f.import3.masters } returns arrayListOf(f.requiem)

                    f.transformer.selector(f.history3, f.ctx) shouldBe true
                }

                "select records last edited by a visual data provider and a previous Requiem edit" {
                    val f = Fixture()

                    every { f.history3.form } returns FormID("000666", "Skyrim.esm")
                    every { f.history3.recordHistory } returns f.history
                    every { f.import1.info } returns ModListing("foo.esp")
                    every { f.import2.info } returns f.requiem
                    every { f.import3.info } returns f.provider
                    every { f.import1.masters } returns arrayListOf()
                    every { f.import2.masters } returns arrayListOf()
                    every { f.import3.masters } returns arrayListOf()
                    every { f.history1.modImportedFrom } returns ModListing("foo.esp")
                    every { f.history2.modImportedFrom } returns f.requiem
                    every { f.history3.modImportedFrom } returns f.provider

                    f.transformer.selector(f.history3, f.ctx) shouldBe true
                }

                "select records last edited by a visual data provider and a previous Requiem-plugin edit" {
                    val f = Fixture()

                    every { f.history3.form } returns FormID("000666", "Skyrim.esm")
                    every { f.history3.recordHistory } returns f.history
                    every { f.import1.info } returns ModListing("foo.esp")
                    every { f.import2.info } returns f.requiemPlugin
                    every { f.import3.info } returns f.provider
                    every { f.import1.masters } returns arrayListOf()
                    every { f.import2.masters } returns arrayListOf()
                    every { f.import3.masters } returns arrayListOf()
                    every { f.history1.modImportedFrom } returns ModListing("foo.esp")
                    every { f.history2.modImportedFrom } returns f.requiemPlugin
                    every { f.history3.modImportedFrom } returns f.provider
                    every { f.import2.masters } returns arrayListOf(f.requiem)

                    f.transformer.selector(f.history3, f.ctx) shouldBe true
                }
            }

            "transforming records" should {
                "copy the traits from the template actor" {
                    val f = Fixture()

                    every { f.history3.form } returns FormID("000666", "Skyrim.esm")
                    every { f.history3.recordHistory } returns f.history
                    every { f.import1.info } returns ModListing("foo.esp")
                    every { f.import2.info } returns f.requiemPlugin
                    every { f.import3.info } returns f.provider
                    every { f.import1.masters } returns arrayListOf()
                    every { f.import2.masters } returns arrayListOf()
                    every { f.import3.masters } returns arrayListOf()
                    every { f.history1.modImportedFrom } returns ModListing("foo.esp")
                    every { f.history2.modImportedFrom } returns f.requiemPlugin
                    every { f.history3.modImportedFrom } returns f.provider
                    every { f.import2.masters } returns arrayListOf(f.requiem)

                    every { f.history2[NPC_.TemplateFlag.USE_ATTACK_DATA] = false } returns Unit
                    every { f.history2[NPC_.TemplateFlag.USE_TRAITS] = false } returns Unit

                    every { f.history2.copyTraits(f.history3) } returns f.history2
                    every { f.history2.copyAttackdata(f.history3) } returns f.history2

                    f.transformer.transformation(f.history3, f.ctx)

                    verify(exactly = 1) {
                        f.history2.copyTraits(f.history3)
                    }
                }

                "copy the attack data from the template actor" {
                    val f = Fixture()

                    every { f.history3.form } returns FormID("000666", "Skyrim.esm")
                    every { f.history3.recordHistory } returns f.history
                    every { f.import1.info } returns ModListing("foo.esp")
                    every { f.import2.info } returns f.requiemPlugin
                    every { f.import3.info } returns f.provider
                    every { f.import1.masters } returns arrayListOf()
                    every { f.import2.masters } returns arrayListOf()
                    every { f.import3.masters } returns arrayListOf()
                    every { f.history1.modImportedFrom } returns ModListing("foo.esp")
                    every { f.history2.modImportedFrom } returns f.requiemPlugin
                    every { f.history3.modImportedFrom } returns f.provider
                    every { f.import2.masters } returns arrayListOf(f.requiem)

                    every { f.history2[NPC_.TemplateFlag.USE_ATTACK_DATA] = false } returns Unit
                    every { f.history2[NPC_.TemplateFlag.USE_TRAITS] = false } returns Unit

                    every { f.history2.copyTraits(f.history3) } returns f.history2
                    every { f.history2.copyAttackdata(f.history3) } returns f.history2

                    f.transformer.transformation(f.history3, f.ctx)

                    verify(exactly = 1) {
                        f.history2.copyAttackdata(f.history3)
                    }
                }
            }
        }
    }
}