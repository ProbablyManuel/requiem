package skyrim.requiem.tests.transformations.actors

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.Mod
import skyproc.NPC_
import skyrim.requiem.Actor
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.logic.AssignmentRule
import skyrim.requiem.logic.AssignmentType
import skyrim.requiem.logic.Assignments
import skyrim.requiem.transformations.actors.GameMechanicsSpellsAndPerks

class GameMechanicsSpellsAndPerksSpec : WordSpec() {
    init {

        class Fixture(globalPerks: Set<FormID> = setOf()) {
            val rule = mockk<AssignmentRule<Actor>>()
            val transformer = GameMechanicsSpellsAndPerks(setOf(rule), globalPerks)
            val lastOverWrites = mockk<Mod>()
            val importHistory = mockk<List<Mod>>()
            val ctx = LoadOrderContent(importHistory, lastOverWrites)
            val template = FormID("000667", "Skyrim.esm")
            val assignment = Assignments(
                mapOf(
                    AssignmentType.perks to mapOf(
                        "some rule" to setOf(FormID("000668", "Skyrim.esm"), FormID("000668", "Requiem.esp"))
                    ),
                    AssignmentType.spells to mapOf(
                        "some better rule" to setOf(FormID("012668", "Skyrim.esm"), FormID("012668", "Requiem.esp"))
                    )
                )

            )
        }

        "A GameMechanicsSpellsAndPerks transformer" When {

            "filtering records" should {
                "select the player record" {
                    val f = Fixture()

                    val player = mockk<Actor>(relaxed = true)
                    every { player.form } returns FormID("000007", "Skyrim.esm")
                    every { player.get(NPC_.TemplateFlag.USE_SPELL_LIST) } returns false
                    every { player.template } returns FormID.NULL
                    every { f.lastOverWrites.getMajor(FormID.NULL, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) } returns null

                    f.transformer.selector(player, f.ctx) shouldBe true
                }

                "ignore records which inherit spells and perks and have a valid template" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    every { actor.form } returns FormID("000666", "Skyrim.esm")
                    every { actor.get(NPC_.TemplateFlag.USE_SPELL_LIST) } returns true
                    every { actor.template } returns f.template
                    every { f.lastOverWrites.getMajor(f.template, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) } returns mockk()

                    f.transformer.selector(actor, f.ctx) shouldBe false
                }

                "select records which inherit spells and perks, but have no template set" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    every { actor.form } returns FormID("000666", "Skyrim.esm")
                    every { actor.get(NPC_.TemplateFlag.USE_SPELL_LIST) } returns true
                    every { actor.template } returns FormID.NULL
                    every { f.lastOverWrites.getMajor(FormID.NULL, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) } returns null

                    f.transformer.selector(actor, f.ctx) shouldBe true
                }

                "select records which have a template set but do not inherit spells and perks" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    every { actor.form } returns FormID("000666", "Skyrim.esm")
                    every { actor.get(NPC_.TemplateFlag.USE_SPELL_LIST) } returns false
                    every { actor.template } returns f.template
                    every { f.lastOverWrites.getMajor(f.template, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) } returns mockk()

                    f.transformer.selector(actor, f.ctx) shouldBe true
                }

                "select records without inheritance" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    every { actor.form } returns FormID("000666", "Skyrim.esm")
                    every { actor.get(NPC_.TemplateFlag.USE_SPELL_LIST) } returns false
                    every { actor.template } returns FormID.NULL
                    every { f.lastOverWrites.getMajor(FormID.NULL, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) } returns null

                    f.transformer.selector(actor, f.ctx) shouldBe true
                }
            }

            "transforming records" should {
                "apply the perks granted by the assignment rules" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)
                    every { f.rule.computeAssignments(actor, f.lastOverWrites) } returns f.assignment

                    f.transformer.transformation(actor, f.ctx)

                    verify(exactly = 1) {
                        f.assignment.data.getValue(AssignmentType.perks).values.flatten()
                            .forEach { actor.addPerk(it, 1) }
                    }
                }

                "apply the spells granted by the assignment rules" {
                    val f = Fixture()

                    val actor = mockk<Actor>(relaxed = true)

                    every { f.rule.computeAssignments(actor, f.lastOverWrites) } returns f.assignment

                    f.transformer.transformation(actor, f.ctx)

                    verify(exactly = 1) {
                        f.assignment.data.getValue(AssignmentType.spells).values.flatten()
                            .forEach { actor.addSpell(it) }
                    }
                }

                "apply the perks from the legacy global distribution" {
                    val perks = setOf(FormID("123456Skyrim.esm"), FormID("123456Requiem.esp"))
                    val f = Fixture(perks)

                    val actor = mockk<Actor>(relaxed = true)

                    every { f.rule.computeAssignments(actor, f.lastOverWrites) } returns f.assignment

                    f.transformer.transformation(actor, f.ctx)

                    verify(exactly = 1) {
                        perks.forEach { actor.addPerk(it, 1) }
                    }
                }
            }
        }
    }
}