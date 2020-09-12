package skyrim.requiem.tests

import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import io.mockk.every
import io.mockk.mockk
import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.LVLN
import skyproc.Mod
import skyproc.NPC_
import skyrim.requiem.exceptions.InvalidRecordException
import skyrim.requiem.resolveTemplates

class ExtensionsSpec : WordSpec() {
    init {

        "NPC_ extensions" When {

            "resolving the possible base actors for a property of an actor" should {
                "return the actor itself if there is no inheritance" {
                    val actor = mockk<NPC_>()
                    val context = mockk<Mod>()

                    every { actor[NPC_.TemplateFlag.USE_TRAITS] } returns false
                    every { actor[NPC_.TemplateFlag.USE_KEYWORDS] } returns false
                    every { actor.template } returns FormID("ABCDEF", "template.esm")

                    actor.resolveTemplates(
                        context,
                        NPC_.TemplateFlag.USE_TRAITS,
                        NPC_.TemplateFlag.USE_KEYWORDS
                    ) shouldContainExactlyInAnyOrder setOf(
                        mapOf(
                            NPC_.TemplateFlag.USE_TRAITS to actor,
                            NPC_.TemplateFlag.USE_KEYWORDS to actor
                        )
                    )
                }

                "return the actor itself if there is no template defined" {
                    val actor = mockk<NPC_>()
                    val context = mockk<Mod>()

                    every { actor[NPC_.TemplateFlag.USE_TRAITS] } returns true
                    every { actor[NPC_.TemplateFlag.USE_KEYWORDS] } returns true
                    every { actor.template } returns FormID.NULL

                    actor.resolveTemplates(
                        context,
                        NPC_.TemplateFlag.USE_TRAITS,
                        NPC_.TemplateFlag.USE_KEYWORDS
                    ) shouldContainExactlyInAnyOrder setOf(
                        mapOf(
                            NPC_.TemplateFlag.USE_TRAITS to actor,
                            NPC_.TemplateFlag.USE_KEYWORDS to actor
                        )
                    )
                }

                "return the template actors when inheriting the properties from other actors" {
                    val actor = mockk<NPC_>()
                    val template = mockk<NPC_>()
                    val context = mockk<Mod>()
                    val templateFid = FormID("ABCDEF", "template.esm")

                    every { actor[NPC_.TemplateFlag.USE_TRAITS] } returns true
                    every { actor[NPC_.TemplateFlag.USE_KEYWORDS] } returns false
                    every { actor.template } returns templateFid
                    every { context.getMajor(templateFid, GRUP_TYPE.NPC_, GRUP_TYPE.LVLN) } returns template
                    every { template[NPC_.TemplateFlag.USE_TRAITS] } returns false
                    every { template.template } returns null

                    actor.resolveTemplates(
                        context,
                        NPC_.TemplateFlag.USE_TRAITS,
                        NPC_.TemplateFlag.USE_KEYWORDS
                    ) shouldContainExactlyInAnyOrder setOf(
                        mapOf(
                            NPC_.TemplateFlag.USE_TRAITS to template,
                            NPC_.TemplateFlag.USE_KEYWORDS to actor
                        )
                    )
                }

                "return the possible base actors when inheriting the properties from a leveled character" {
                    val actor = mockk<NPC_>()
                    val imported = mockk<Mod>()
                    val templateFid = FormID("ABCDEF", "template.esm")
                    val template = mockk<LVLN>()
                    val leveledListEntry1 = FormID("123ABC", "entry.esm")
                    val leveledListEntry2 = FormID("123DEF", "entry.esm")
                    val baseActor1 = mockk<NPC_>()
                    val baseActor2 = mockk<NPC_>()

                    every { actor[NPC_.TemplateFlag.USE_TRAITS] } returns true
                    every { actor[NPC_.TemplateFlag.USE_KEYWORDS] } returns false
                    every { actor.template } returns templateFid
                    every { imported.getMajor(templateFid, GRUP_TYPE.NPC_, GRUP_TYPE.LVLN) } returns template
                    every { template.entryForms } returns arrayListOf(leveledListEntry1, leveledListEntry2)
                    every { imported.getMajor(leveledListEntry1, GRUP_TYPE.NPC_, GRUP_TYPE.LVLN) } returns baseActor1
                    every { imported.getMajor(leveledListEntry2, GRUP_TYPE.NPC_, GRUP_TYPE.LVLN) } returns baseActor2
                    every { baseActor1.template } returns FormID.NULL
                    every { baseActor2.template } returns FormID.NULL
                    every { baseActor1[any<NPC_.TemplateFlag>()] } returns false
                    every { baseActor2[any<NPC_.TemplateFlag>()] } returns false

                    actor.resolveTemplates(
                        imported,
                        NPC_.TemplateFlag.USE_TRAITS,
                        NPC_.TemplateFlag.USE_KEYWORDS
                    ) shouldContainExactlyInAnyOrder setOf(
                        mapOf(
                            NPC_.TemplateFlag.USE_TRAITS to baseActor1,
                            NPC_.TemplateFlag.USE_KEYWORDS to actor
                        ),
                        mapOf(
                            NPC_.TemplateFlag.USE_TRAITS to baseActor2,
                            NPC_.TemplateFlag.USE_KEYWORDS to actor
                        )
                    )
                }

                "throw an exception if the template's FormID cannot be resolved" {
                    val actor = mockk<NPC_>()
                    val context = mockk<Mod>()
                    val templateFid = FormID("ABCDEF", "template.esm")

                    every { actor[NPC_.TemplateFlag.USE_TRAITS] } returns true
                    every { actor.template } returns templateFid
                    every { context.getMajor(templateFid, GRUP_TYPE.NPC_, GRUP_TYPE.LVLN) } returns null

                    shouldThrow<InvalidRecordException> {
                        actor.resolveTemplates(
                            context,
                            NPC_.TemplateFlag.USE_TRAITS
                        )
                    }
                }
            }

            "resolving the properties of a leveled character" should {

                "return all possible values of the property of the leveled list entries" {
                    val lChar = mockk<LVLN>()
                    val context = mockk<Mod>()
                    val subFid1 = FormID("ABC123", "template.esm")
                    val subFid2 = FormID("ABC456", "template.esm")
                    val subTemplate1 = mockk<NPC_>()
                    val subTemplate2 = mockk<NPC_>()

                    every { lChar.entryForms } returns arrayListOf(subFid1, subFid2)
                    every { context.getMajor(subFid1, GRUP_TYPE.NPC_, GRUP_TYPE.LVLN) } returns subTemplate1
                    every { context.getMajor(subFid2, GRUP_TYPE.NPC_, GRUP_TYPE.LVLN) } returns subTemplate2
                    every { subTemplate1.template } returns FormID.NULL
                    every { subTemplate2.template } returns FormID.NULL
                    every { subTemplate1[any<NPC_.TemplateFlag>()] } returns false
                    every { subTemplate2[any<NPC_.TemplateFlag>()] } returns false

                    lChar.resolveTemplates(
                        context,
                        NPC_.TemplateFlag.USE_TRAITS,
                        NPC_.TemplateFlag.USE_KEYWORDS
                    ) shouldContainExactlyInAnyOrder setOf(
                        mapOf(
                            NPC_.TemplateFlag.USE_TRAITS to subTemplate1,
                            NPC_.TemplateFlag.USE_KEYWORDS to subTemplate1
                        ),
                        mapOf(
                            NPC_.TemplateFlag.USE_TRAITS to subTemplate2,
                            NPC_.TemplateFlag.USE_KEYWORDS to subTemplate2
                        )
                    )
                }

                "throw an exception if an entry cannot be resolved" {
                    val lChar = mockk<LVLN>()
                    val context = mockk<Mod>()
                    val subFid1 = FormID("ABC123", "template.esm")
                    val subFid2 = FormID("ABC456", "template.esm")
                    val subTemplate1 = mockk<NPC_>()

                    every { lChar.entryForms } returns arrayListOf(subFid1, subFid2)
                    every { context.getMajor(subFid1, GRUP_TYPE.NPC_, GRUP_TYPE.LVLN) } returns subTemplate1
                    every { context.getMajor(subFid2, GRUP_TYPE.NPC_, GRUP_TYPE.LVLN) } returns null
                    every { subTemplate1.template } returns null
                    every { subTemplate1[NPC_.TemplateFlag.USE_TRAITS] } returns false

                    shouldThrow<InvalidRecordException> {
                        lChar.resolveTemplates(
                            context,
                            NPC_.TemplateFlag.USE_TRAITS
                        )
                    }
                }
            }
        }
    }
}