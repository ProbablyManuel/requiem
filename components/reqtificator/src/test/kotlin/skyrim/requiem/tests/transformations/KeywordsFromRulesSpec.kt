package skyrim.requiem.tests.transformations

import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.WordSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import skyproc.FormID
import skyproc.KeywordSet
import skyproc.Mod
import skyrim.requiem.Armor
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.logic.AssignmentRule
import skyrim.requiem.logic.AssignmentType
import skyrim.requiem.logic.Assignments
import skyrim.requiem.transformations.KeywordsFromRules

class KeywordsFromRulesSpec : WordSpec() {

    init {

        class Fixture {
            val rule = mockk<AssignmentRule<Armor>>()
            val transformer = KeywordsFromRules(setOf(rule))
            val lastOverWrites = mockk<Mod>()
            val importHistory = mockk<List<Mod>>()
            val ctx = LoadOrderContent(importHistory, lastOverWrites)
            val assignment = Assignments(
                mapOf(
                    AssignmentType.keywords to mapOf(
                        "some rule" to setOf(FormID("000668", "Skyrim.esm"), FormID("000668", "Requiem.esp"))
                    )
                )
            )
            val emptyAssignment = Assignments(mapOf(AssignmentType.keywords to mapOf()))
        }

        "A KeywordsFromRules transformer" When {

            "filtering records" should {
                "ignore templated records" {
                    val f = Fixture()

                    val armorWithTemplate = mockk<Armor>(relaxed = true)
                    every { armorWithTemplate.isTemplated } returns true

                    f.transformer.selector(armorWithTemplate, f.ctx) shouldBe false
                }

                "ignore records which get no keywords assigned by the rules" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { f.rule.computeAssignments(armor, f.lastOverWrites) } returns f.emptyAssignment

                    f.transformer.selector(armor, f.ctx) shouldBe false
                }

                "select records which have no template and will get at least one keyword assigned" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    every { armor.isTemplated } returns false
                    every { f.rule.computeAssignments(armor, f.lastOverWrites) } returns f.assignment

                    f.transformer.selector(armor, f.ctx) shouldBe true
                }
            }

            "transforming records" should {
                "apply the keywords granted by the assignment rules" {
                    val f = Fixture()

                    val armor = mockk<Armor>(relaxed = true)
                    val keywords = mockk<KeywordSet>(relaxUnitFun = true)
                    every { f.rule.computeAssignments(armor, f.lastOverWrites) } returns f.assignment
                    every { armor.keywordSet } returns keywords

                    f.transformer.transformation(armor, f.ctx)

                    verify(exactly = 1) {
                        f.assignment.data.getValue(AssignmentType.keywords).values.flatten()
                            .forEach { keywords.addKeywordRef(it) }
                    }
                }
            }
        }
    }
}