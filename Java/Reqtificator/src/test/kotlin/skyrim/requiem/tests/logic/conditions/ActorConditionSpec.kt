package skyrim.requiem.tests.logic.conditions

import io.kotlintest.be
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.should
import io.mockk.every
import io.mockk.mockk
import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.Mod
import skyproc.NPC_
import skyproc.RACE
import skyrim.requiem.keywords
import skyrim.requiem.logic.conditions.ActorCondition
import skyrim.requiem.logic.conditions.ActorHasAllKeywords
import skyrim.requiem.logic.conditions.ActorHasAnyKeyword
import skyrim.requiem.logic.conditions.ActorHasNoKeywords
import skyrim.requiem.logic.conditions.HasAnyRace
import skyrim.requiem.logic.conditions.HasNoRace
import skyrim.requiem.resolveMajor
import skyrim.requiem.resolveTemplates
import skyrim.requiem.tests.WordSpecWithStaticMockks

class ActorConditionSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.ExtensionsKt")
    override val objectMockks = arrayOf<Any>(ActorCondition)

    init {
        "ActorHasAllKeywords" should {
            "return true if each template has all specified keywords" {
                with(ActorCondition) {
                    val context = mockk<Mod>()
                    val requiredKeyword1 = FormID("123456", "key.esp")
                    val requiredKeyword2 = FormID("123789", "key.esp")
                    val otherKeyword = FormID("123ABC", "key.esp")

                    val actorWithKeywords = mockk<NPC_>()
                    every { actorWithKeywords.keywordsForeachTemplate(context) } returns setOf(
                        setOf(requiredKeyword1, requiredKeyword2),
                        setOf(requiredKeyword1, requiredKeyword2, otherKeyword)
                    )

                    val condition = ActorHasAllKeywords(setOf(requiredKeyword1, requiredKeyword2))
                    condition.test(actorWithKeywords, context) should be(true)
                }
            }

            "return false if at least one template does not have all specified keywords" {
                with(ActorCondition) {
                    val context = mockk<Mod>()
                    val requiredKeyword1 = FormID("123456", "key.esp")
                    val requiredKeyword2 = FormID("123789", "key.esp")
                    val otherKeyword = FormID("123ABC", "key.esp")

                    val actorWithKeywords = mockk<NPC_>()
                    every { actorWithKeywords.keywordsForeachTemplate(context) } returns setOf(
                        setOf(requiredKeyword1, requiredKeyword2),
                        setOf(requiredKeyword1, otherKeyword)
                    )

                    val condition = ActorHasAllKeywords(setOf(requiredKeyword1, requiredKeyword2))
                    condition.test(actorWithKeywords, context) should be(false)
                }
            }
        }

        "ActorHasAnyKeyword" should {
            "return true if each template has at least one of the specified keywords" {
                with(ActorCondition) {
                    val context = mockk<Mod>()
                    val requiredKeyword1 = FormID("123456", "key.esp")
                    val requiredKeyword2 = FormID("123789", "key.esp")
                    val otherKeyword = FormID("123ABC", "key.esp")

                    val actorWithKeywords = mockk<NPC_>()
                    every { actorWithKeywords.keywordsForeachTemplate(context) } returns setOf(
                        setOf(requiredKeyword1, otherKeyword),
                        setOf(requiredKeyword2, otherKeyword)
                    )

                    val condition = ActorHasAnyKeyword(setOf(requiredKeyword1, requiredKeyword2))
                    condition.test(actorWithKeywords, context) should be(true)
                }
            }

            "return false if at least one template does not have any of the specified keywords" {
                with(ActorCondition) {
                    val context = mockk<Mod>()
                    val requiredKeyword1 = FormID("123456", "key.esp")
                    val requiredKeyword2 = FormID("123789", "key.esp")
                    val otherKeyword = FormID("123ABC", "key.esp")

                    val actorWithKeywords = mockk<NPC_>()
                    every { actorWithKeywords.keywordsForeachTemplate(context) } returns setOf(
                        setOf(requiredKeyword1, requiredKeyword2),
                        setOf(otherKeyword)
                    )

                    val condition = ActorHasAnyKeyword(setOf(requiredKeyword1, requiredKeyword2))
                    condition.test(actorWithKeywords, context) should be(false)
                }
            }
        }

        "ActorHasNoKeyword" should {
            "return true if each template has none of the specified keywords" {
                with(ActorCondition) {
                    val context = mockk<Mod>()
                    val forbiddenKeyword = FormID("123456", "key.esp")
                    val otherKeyword1 = FormID("123789", "key.esp")
                    val otherKeyword2 = FormID("123ABC", "key.esp")

                    val actorWithKeywords = mockk<NPC_>()
                    every { actorWithKeywords.keywordsForeachTemplate(context) } returns setOf(
                        setOf(otherKeyword1, otherKeyword2),
                        setOf(otherKeyword2)
                    )

                    val condition = ActorHasNoKeywords(setOf(forbiddenKeyword))
                    condition.test(actorWithKeywords, context) should be(true)
                }
            }

            "return false if at least one template has any of the specified keywords" {
                with(ActorCondition) {
                    val context = mockk<Mod>()
                    val forbiddenKeyword = FormID("123456", "key.esp")
                    val otherKeyword1 = FormID("123789", "key.esp")
                    val otherKeyword2 = FormID("123ABC", "key.esp")

                    val actorWithKeywords = mockk<NPC_>()
                    every { actorWithKeywords.keywordsForeachTemplate(context) } returns setOf(
                        setOf(otherKeyword1, otherKeyword2),
                        setOf(otherKeyword1, forbiddenKeyword)
                    )

                    val condition = ActorHasNoKeywords(setOf(forbiddenKeyword))
                    condition.test(actorWithKeywords, context) should be(false)
                }
            }
        }

        "HasAnyRace" should {
            "return true if the possible races are a subset of the allowed races" {
                val context = mockk<Mod>()
                val possibleRace1 = FormID("123456", "race.esm")
                val possibleRace2 = FormID("123789", "race.esm")
                val template1 = mockk<NPC_>()
                val template2 = mockk<NPC_>()
                every { template1.race } returns possibleRace1
                every { template2.race } returns possibleRace2

                val actorWithMatchingRaces = mockk<NPC_>()
                every { actorWithMatchingRaces.resolveTemplates(context, NPC_.TemplateFlag.USE_TRAITS) } returns setOf(
                    template1,
                    template2
                )

                HasAnyRace(setOf(possibleRace1, possibleRace2)).test(actorWithMatchingRaces, context) should be(true)
            }

            "return false if the possible races are not a subset of the allowed races" {
                val context = mockk<Mod>()
                val possibleRace1 = FormID("123456", "race.esm")
                val possibleRace2 = FormID("123789", "race.esm")
                val otherRace = FormID("123ABC", "race.esm")
                val template1 = mockk<NPC_>()
                val template2 = mockk<NPC_>()
                every { template1.race } returns possibleRace1
                every { template2.race } returns otherRace

                val actorWithWrongRace = mockk<NPC_>()
                every { actorWithWrongRace.resolveTemplates(context, NPC_.TemplateFlag.USE_TRAITS) } returns setOf(
                    template1,
                    template2
                )

                HasAnyRace(setOf(possibleRace1, possibleRace2)).test(actorWithWrongRace, context) should be(false)
            }
        }

        "HasNoRace" should {
            "return true if the intersection of possible races and forbidden races is empty" {
                val context = mockk<Mod>()
                val forbiddenRace = FormID("123ABC", "race.esm")
                val otherRace1 = FormID("123456", "race.esm")
                val otherRace2 = FormID("123789", "race.esm")
                val template1 = mockk<NPC_>()
                val template2 = mockk<NPC_>()
                every { template1.race } returns otherRace1
                every { template2.race } returns otherRace2

                val actorWithoutForbiddenRace = mockk<NPC_>()
                every {
                    actorWithoutForbiddenRace.resolveTemplates(
                        context,
                        NPC_.TemplateFlag.USE_TRAITS
                    )
                } returns setOf(template1, template2)

                HasNoRace(setOf(forbiddenRace)).test(actorWithoutForbiddenRace, context) should be(true)
            }

            "return false if the intersection of possible races and forbidden races is nonempty" {
                val context = mockk<Mod>()
                val forbiddenRace1 = FormID("123456", "race.esm")
                val forbiddenRace2 = FormID("123ABC", "race.esm")
                val otherRace = FormID("123789", "race.esm")
                val template1 = mockk<NPC_>()
                val template2 = mockk<NPC_>()
                every { template1.race } returns forbiddenRace1
                every { template2.race } returns otherRace

                val actorWithForbiddenRace = mockk<NPC_>()
                every { actorWithForbiddenRace.resolveTemplates(context, NPC_.TemplateFlag.USE_TRAITS) } returns setOf(
                    template1,
                    template2
                )

                HasNoRace(setOf(forbiddenRace1, forbiddenRace2)).test(actorWithForbiddenRace, context) should be(false)
            }
        }
    }
}

class ActorConditionObjectSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.ExtensionsKt")

    init {
        "The ActorCondition object" should {
            "return the union of race and actor keywords for each possible template" {
                with(ActorCondition) {
                    val context = mockk<Mod>()
                    val actorKeyword1 = FormID("123456", "key.esp")
                    val actorKeyword2 = FormID("123789", "key.esp")
                    val raceKeyword1 = FormID("123ABC", "key.esp")
                    val raceKeyword2 = FormID("123DEF", "key.esp")

                    val template1 = mockk<NPC_>()
                    val template2 = mockk<NPC_>()
                    val raceFormId1 = FormID("123456", "race.esp")
                    val raceFormId2 = FormID("123789", "race.esp")
                    val race1 = mockk<RACE>()
                    val race2 = mockk<RACE>()
                    every { template1.keywords } returns listOf(actorKeyword1, actorKeyword2)
                    every { template2.keywords } returns listOf(actorKeyword1)
                    every { template1.race } returns raceFormId1
                    every { template2.race } returns raceFormId2
                    every { context.resolveMajor(raceFormId1, GRUP_TYPE.RACE) } returns race1
                    every { context.resolveMajor(raceFormId2, GRUP_TYPE.RACE) } returns race2
                    every { race1.keywords } returns listOf(raceKeyword1)
                    every { race2.keywords } returns listOf(raceKeyword2)

                    val actor = mockk<NPC_>()
                    every {
                        actor.resolveTemplates(
                            context,
                            NPC_.TemplateFlag.USE_KEYWORDS,
                            NPC_.TemplateFlag.USE_TRAITS
                        )
                    } returns
                        setOf(
                            mapOf(
                                NPC_.TemplateFlag.USE_KEYWORDS to template1, NPC_.TemplateFlag.USE_TRAITS to template1
                            ),
                            mapOf(
                                NPC_.TemplateFlag.USE_KEYWORDS to template1, NPC_.TemplateFlag.USE_TRAITS to template2
                            )
                        )

                    actor.keywordsForeachTemplate(context) shouldContainExactlyInAnyOrder setOf(
                        setOf(actorKeyword1, actorKeyword2, raceKeyword1),
                        setOf(actorKeyword1, actorKeyword2, raceKeyword2)
                    )
                }
            }
        }
    }
}