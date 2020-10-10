package skyrim.requiem.tests.transformations.actors

import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.Mod
import skyproc.NPC_
import skyproc.ScriptPackage
import skyproc.ScriptRef
import skyrim.requiem.Actor
import skyrim.requiem.LeveledCharacter
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.transformations.actors.GameMechanicsScripts

class GameMechanicsScriptsSpec : WordSpec() {
    init {

        class Fixture {
            val script = mockk<ScriptRef>()
            val transformer = GameMechanicsScripts(setOf(script))
            val lastOverWrites = mockk<Mod>()
            val importHistory = mockk<List<Mod>>()
            val ctx = LoadOrderContent(importHistory, lastOverWrites)
            val template = FormID("000667", "Skyrim.esm")
        }

        "A GameMechanicsScripts transformer" When {

            "filtering records" should {
                "ignore the player record" {
                    val f = Fixture()

                    val player = mockk<Actor>(relaxed = true)
                    every { player.form } returns FormID("000007", "Skyrim.esm")
                    every { player.get(NPC_.TemplateFlag.USE_SCRIPTS) } returns false
                    every { player.template } returns FormID.NULL
                    every { f.lastOverWrites.getMajor(FormID.NULL, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) } returns null

                    f.transformer.selector(player, f.ctx) shouldBe false
                }

                "ignore records which inherit scripts from a valid actor template" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    every { actor.form } returns FormID("000666", "Skyrim.esm")
                    every { actor.get(NPC_.TemplateFlag.USE_SCRIPTS) } returns true
                    every { actor.template } returns f.template
                    every { f.lastOverWrites.getMajor(f.template, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) } returns mockk<Actor>()

                    f.transformer.selector(actor, f.ctx) shouldBe false
                }

                "select records which inherit scripts but have no valid template" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    every { actor.form } returns FormID("000666", "Skyrim.esm")
                    every { actor.get(NPC_.TemplateFlag.USE_SCRIPTS) } returns true
                    every { actor.template } returns FormID.NULL
                    every { f.lastOverWrites.getMajor(FormID.NULL, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) } returns null

                    f.transformer.selector(actor, f.ctx) shouldBe true
                }

                "select records which inherit scripts but have a leveled character as template" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    every { actor.form } returns FormID("000666", "Skyrim.esm")
                    every { actor.get(NPC_.TemplateFlag.USE_SCRIPTS) } returns true
                    every { actor.template } returns f.template
                    every {
                        f.lastOverWrites.getMajor(
                            f.template,
                            GRUP_TYPE.LVLN,
                            GRUP_TYPE.NPC_
                        )
                    } returns mockk<LeveledCharacter>()

                    f.transformer.selector(actor, f.ctx) shouldBe true
                }

                "select records which have a template set but do not inherit scripts" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    every { actor.form } returns FormID("000666", "Skyrim.esm")
                    every { actor.get(NPC_.TemplateFlag.USE_SCRIPTS) } returns false
                    every { actor.template } returns f.template
                    every { f.lastOverWrites.getMajor(f.template, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) } returns mockk<Actor>()

                    f.transformer.selector(actor, f.ctx) shouldBe true
                }

                "select records without inheritance" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    every { actor.form } returns FormID("000666", "Skyrim.esm")
                    every { actor.get(NPC_.TemplateFlag.USE_SCRIPTS) } returns false
                    every { actor.template } returns FormID.NULL
                    every { f.lastOverWrites.getMajor(FormID.NULL, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) } returns null

                    f.transformer.selector(actor, f.ctx) shouldBe true
                }
            }

            "transforming records" should {
                "add the scripts to the actor" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    val scriptPackage = mockk<ScriptPackage>(relaxed = true)
                    every { actor[NPC_.TemplateFlag.USE_SCRIPTS] } returns true
                    every { actor.template } returns f.template
                    every { f.lastOverWrites.getMajor(f.template, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) } returns mockk<Actor>()
                    every { actor.scriptPackage } returns scriptPackage

                    f.transformer.transformation(actor, f.ctx)

                    verify(exactly = 1) {
                        scriptPackage.addScript(f.script)
                    }
                }

                "remove the inheritance flag if the template is a leveled character" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    val scriptPackage = mockk<ScriptPackage>(relaxed = true)
                    every { actor[NPC_.TemplateFlag.USE_SCRIPTS] } returns true
                    every { actor.template } returns f.template
                    every {
                        f.lastOverWrites.getMajor(
                            f.template,
                            GRUP_TYPE.LVLN,
                            GRUP_TYPE.NPC_
                        )
                    } returns mockk<LeveledCharacter>()
                    every { actor.scriptPackage } returns scriptPackage

                    f.transformer.transformation(actor, f.ctx)

                    verify(exactly = 1) {
                        actor[NPC_.TemplateFlag.USE_SCRIPTS] = false
                        scriptPackage.addScript(f.script)
                    }
                }
            }
        }
    }
}