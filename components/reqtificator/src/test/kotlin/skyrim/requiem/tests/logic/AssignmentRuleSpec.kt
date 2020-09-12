package skyrim.requiem.tests.logic

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValue
import io.kotlintest.be
import io.kotlintest.matchers.collections.containExactly
import io.kotlintest.matchers.collections.containExactlyInAnyOrder
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.matchers.collections.shouldNotContainAll
import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import io.kotlintest.shouldNotThrow
import io.kotlintest.shouldThrow
import io.mockk.every
import io.mockk.mockk
import skyproc.ARMO
import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.Mod
import skyrim.requiem.exceptions.InvalidDataInLoadOrderException
import skyrim.requiem.exceptions.SetupRequirementFailedException
import skyrim.requiem.localization.TextReference
import skyrim.requiem.logic.AssignmentRule
import skyrim.requiem.logic.AssignmentType
import skyrim.requiem.logic.KeywordAssignment
import skyrim.requiem.logic.RecordAssignment
import skyrim.requiem.logic.conditions.HasAllKeywords
import skyrim.requiem.logic.conditions.HasAnyKeyword
import skyrim.requiem.logic.conditions.HasNoKeyword
import skyrim.requiem.logic.conditions.RecordCondition
import skyrim.requiem.tests.WordSpecWithStaticMockks

class AssignmentRuleSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.ExtensionsKt")

    init {
        val sampleKeywords = listOf(
            FormID("123456", "foo.esm"),
            FormID("ABCDEF", "bar.esp"),
            FormID("ABC123", "gus.esp"),
            FormID("DEF456", "blabla.esp")
        )

        "An AssignmentRule" When {
            "all assignment conditions evaluate to true" should {
                "return the associated FormIDs" {
                    val condition1 = mockk<RecordCondition<ARMO>>()
                    val condition2 = mockk<RecordCondition<ARMO>>()
                    every { condition1.test(any(), any()) } returns true
                    every { condition2.test(any(), any()) } returns true
                    val rule = AssignmentRule(
                        identifier = "some rule",
                        conditions = setOf(condition1, condition2),
                        assignments = setOf(KeywordAssignment(sampleKeywords.take(2)))
                    )
                    rule.computeAssignments(
                        mockk(),
                        mockk()
                    ).recordsToApply(AssignmentType.keywords) shouldContainExactlyInAnyOrder
                        sampleKeywords.take(2).toSet()
                }

                "include the FormIDs from matching sub nodes in the result" {
                    val condition1 = mockk<RecordCondition<ARMO>>()
                    val condition2 = mockk<RecordCondition<ARMO>>()
                    every { condition1.test(any(), any()) } returns true
                    every { condition2.test(any(), any()) } returns true
                    val rule = AssignmentRule(
                        identifier = "some rule",
                        conditions = setOf(condition1),
                        assignments = setOf(KeywordAssignment(sampleKeywords[0])),
                        subNodes = setOf(
                            AssignmentRule(
                                identifier = "some rule 2",
                                conditions = setOf(condition2),
                                assignments = setOf(KeywordAssignment(sampleKeywords[1]))
                            ),
                            AssignmentRule(
                                identifier = "some rule 3",
                                conditions = setOf(condition2),
                                assignments = setOf(KeywordAssignment(sampleKeywords[2]))
                            )
                        )
                    )
                    rule.computeAssignments(
                        mockk(),
                        mockk()
                    ).data.keys shouldContainExactly setOf(AssignmentType.keywords)
                    rule.computeAssignments(mockk(), mockk()).recordsToApply(AssignmentType.keywords) shouldContainAll
                        sampleKeywords.slice(1..2).toSet()
                }

                "merge the assignments from all matching subnodes with this node's assignments" {
                    val condition = mockk<RecordCondition<ARMO>>()
                    every { condition.test(any(), any()) } returns true
                    val rule = AssignmentRule(
                        identifier = "some rule",
                        conditions = setOf(condition),
                        assignments = setOf(KeywordAssignment(sampleKeywords[0])),
                        subNodes = setOf(
                            AssignmentRule(
                                identifier = "some rule 1",
                                conditions = setOf(condition),
                                assignments = setOf(KeywordAssignment(sampleKeywords[1]))
                            ),
                            AssignmentRule(
                                identifier = "some rule 2",
                                conditions = setOf(condition),
                                assignments = setOf(KeywordAssignment(sampleKeywords[2]))
                            )
                        )
                    )
                    rule.computeAssignments(
                        mockk(),
                        mockk()
                    ).recordsToApply(AssignmentType.keywords) shouldContainExactlyInAnyOrder
                        sampleKeywords.slice(0..2).toSet()
                }
            }

            "any assignment condition evaluates to false" should {

                "return none of the formIDs to assign" {
                    val condition1 = mockk<RecordCondition<ARMO>>()
                    val condition2 = mockk<RecordCondition<ARMO>>()
                    every { condition1.test(any(), any()) } returns true
                    every { condition2.test(any(), any()) } returns false
                    val rule = AssignmentRule(
                        identifier = "some rule",
                        conditions = setOf(condition1, condition2),
                        assignments = setOf(KeywordAssignment(sampleKeywords.take(2))),
                        subNodes = setOf()
                    )
                    rule.computeAssignments(
                        mockk(),
                        mockk()
                    ).recordsToApply(AssignmentType.keywords) shouldContainExactly setOf()
                }

                "not include results from matching sub nodes" {
                    val condition1 = mockk<RecordCondition<ARMO>>()
                    val condition2 = mockk<RecordCondition<ARMO>>()
                    every { condition1.test(any(), any()) } returns false
                    every { condition2.test(any(), any()) } returns true
                    val rule = AssignmentRule(
                        identifier = "some rule",
                        conditions = setOf(condition1),
                        assignments = setOf(KeywordAssignment(sampleKeywords[0])),
                        subNodes = setOf(
                            AssignmentRule(
                                identifier = "some rule 2",
                                conditions = setOf(condition2),
                                assignments = setOf(KeywordAssignment(sampleKeywords.takeLast(2)))
                            )
                        )
                    )
                    rule.computeAssignments(
                        mockk(),
                        mockk()
                    ).recordsToApply(AssignmentType.keywords) shouldNotContainAll
                        setOf(sampleKeywords.takeLast(2))
                }
            }

            "validating the existence of the contained FormIDs" should {

                "pass validation if the imported mods contain all records" {
                    val import = mockk<Mod>()
                    val form1 = FormID("123456", "a.esp")
                    val form2 = FormID("123ABC", "b.esm")
                    every { import.getMajor(form1, GRUP_TYPE.KYWD) } returns mockk()
                    every { import.getMajor(form2, GRUP_TYPE.KYWD) } returns mockk()
                    val rule = AssignmentRule<ARMO>(
                        identifier = "some rule",
                        conditions = setOf(HasAllKeywords(form1)),
                        assignments = setOf(KeywordAssignment(form2)),
                        subNodes = setOf()
                    )
                    shouldNotThrow<SetupRequirementFailedException> { rule.validateFormIds(import) }
                }

                "fail validation if the imported mods contain all records" {
                    val import = mockk<Mod>()
                    val form1 = FormID("123456", "a.esp")
                    val form2 = FormID("123ABC", "b.esm")
                    every { import.getMajor(form1, GRUP_TYPE.KYWD) } returns mockk()
                    every { import.getMajor(form2, GRUP_TYPE.KYWD) } returns null
                    val rule = AssignmentRule<ARMO>(
                        identifier = "some rule",
                        conditions = setOf(HasAllKeywords(form1)),
                        assignments = setOf(KeywordAssignment(form2)),
                        subNodes = setOf()
                    )
                    val exception = shouldThrow<InvalidDataInLoadOrderException> { rule.validateFormIds(import) }
                    exception.messageTemplate should be(
                        TextReference(
                            "patch.keywords.unknownForm",
                            listOf(form2, GRUP_TYPE.KYWD)
                        )
                    )
                }

                "fail if a sub node fails the validation" {
                    val import = mockk<Mod>()
                    val form1 = FormID("123456", "a.esp")
                    val form2 = FormID("123ABC", "b.esm")
                    every { import.getMajor(form1, GRUP_TYPE.KYWD) } returns mockk()
                    every { import.getMajor(form2, GRUP_TYPE.KYWD) } returns null
                    val rule = AssignmentRule<ARMO>(
                        identifier = "some rule",
                        conditions = setOf(HasAllKeywords(form1)),
                        assignments = setOf(),
                        subNodes = setOf(
                            AssignmentRule(
                                identifier = "some rule",
                                conditions = setOf(HasAllKeywords(form2)),
                                assignments = setOf(KeywordAssignment(form1)),
                                subNodes = setOf()
                            )
                        )
                    )
                    val exception = shouldThrow<InvalidDataInLoadOrderException> { rule.validateFormIds(import) }
                    exception.messageTemplate should be(
                        TextReference(
                            "patch.keywords.unknownForm",
                            listOf(form2, GRUP_TYPE.KYWD)
                        )
                    )
                }
            }
        }

        "An AssignmentRule companion object" When {
            "assembling AssignmentRules for armors from configuration files" should {
                "build rules from a well-formed config file" {
                    val config = ConfigFactory.parseString(
                        """
            |feature_pimpArmors {
            |  keywords_all = [ "123456 all.esp"]
            |  keywords_none = { "foo": "123456 none.esp" }
            |  keywords_any = [ "123456 any.esp", "123789 any.esp" ]
            |  keywords_assign = 123456 assign.esm
            |
            |  subnode1 {
            |    keywords_all = 123789 all.esp
            |    keywords_assign = 123789 assign.esm
            |  }
            |  subnode2 {
            |    keywords_none = 123789 none.esp
            |    keywords_assign = 123ABC assign.esm
            |  }
            |}
          """.trimMargin()
                    )

                    val expected = AssignmentRule<ARMO>(
                        identifier = "pimpArmors",
                        conditions = setOf(
                            HasAllKeywords(FormID("123456", "all.esp")),
                            HasNoKeyword(FormID("123456", "none.esp")),
                            HasAnyKeyword(
                                FormID("123456", "any.esp"),
                                FormID("123789", "any.esp")
                            )
                        ),
                        assignments = setOf(KeywordAssignment(FormID("123456", "assign.esm"))),
                        subNodes = setOf(
                            AssignmentRule(
                                identifier = "pimpArmors.subnode1",
                                conditions = setOf(HasAllKeywords(FormID("123789", "all.esp"))),
                                assignments = setOf(KeywordAssignment(FormID("123789", "assign.esm")))
                            ),
                            AssignmentRule(
                                identifier = "pimpArmors.subnode2",
                                conditions = setOf(HasNoKeyword(FormID("123789", "none.esp"))),
                                assignments = setOf(KeywordAssignment(FormID("123ABC", "assign.esm")))
                            )
                        )
                    )

                    val conditions: (List<String>, ConfigValue) -> RecordCondition<ARMO>? =
                        AssignmentRule.Companion::keywordConditions
                    val assignments: (List<String>, ConfigValue) -> RecordAssignment<ARMO>? =
                        AssignmentRule.Companion::keywordAssignments

                    AssignmentRule.fromConfig(config, conditions, assignments) should haveSize(1)
                    AssignmentRule.fromConfig(config, conditions, assignments).first() should be(expected)
                }
            }

            "parsing keyword declarations" should {
                "parse a valid keyword (quoted" {
                    val config = ConfigFactory.parseString(
                        """
            |test  = "123456 any.esp"
          """.trimMargin()
                    )

                    AssignmentRule.getFormIDs(config.getValue("test"), listOf()) should
                        containExactly(FormID("123456", "any.esp"))
                }

                "parse a valid keyword (unquoted)" {
                    val config = ConfigFactory.parseString(
                        """
            |test  = 123456 any.esp
          """.trimMargin()
                    )

                    AssignmentRule.getFormIDs(config.getValue("test"), listOf()) should
                        containExactly(FormID("123456", "any.esp"))
                }

                "parse valid keyword lists" {
                    val config = ConfigFactory.parseString(
                        """
            |test  = [ "123456 any.esp", "123789 any.esp" ]
          """.trimMargin()
                    )

                    AssignmentRule.getFormIDs(config.getValue("test"), listOf()) should
                        containExactlyInAnyOrder(FormID("123456", "any.esp"), FormID("123789", "any.esp"))
                }

                "parse valid keyword objects" {
                    val config = ConfigFactory.parseString(
                        """
            |test  = { "foo": "123456 any.esp", "bar": "123789 any.esp" }
          """.trimMargin()
                    )

                    AssignmentRule.getFormIDs(config.getValue("test"), listOf()) should
                        containExactlyInAnyOrder(FormID("123456", "any.esp"), FormID("123789", "any.esp"))
                }

                "parse valid nexted keyword declarations" {
                    val config = ConfigFactory.parseString(
                        """
            |test  = [ "ABCDEF all.esm", { "foo": "123456 any.esp", "bar": "123789 any.esp" } ]
          """.trimMargin()
                    )

                    AssignmentRule.getFormIDs(config.getValue("test"), listOf()) should
                        containExactlyInAnyOrder(
                            FormID("123456", "any.esp"),
                            FormID("123789", "any.esp"),
                            FormID("ABCDEF", "all.esm")
                        )
                }

                "throw a SetupRequirementFailedException for invalid keywords" {
                    val config = ConfigFactory.parseString(
                        """
            |test  = "123XYZ any.esp"
          """.trimMargin()
                    )

                    val exception = shouldThrow<SetupRequirementFailedException> {
                        AssignmentRule.getFormIDs(config.getValue("test"), listOf("fail", "test"))
                    }
                    exception.messageTemplate should be(
                        TextReference(
                            "patch.keywords.invalidForm",
                            listOf("123XYZ any.esp", "fail.test")
                        )
                    )
                }

                "throw a SetupRequirementFailedException for wrong data types" {
                    val config = ConfigFactory.parseString(
                        """
            |test  = 666
          """.trimMargin()
                    )

                    val exception = shouldThrow<SetupRequirementFailedException> {
                        AssignmentRule.getFormIDs(config.getValue("test"), listOf("fail", "test"))
                    }
                    exception.messageTemplate should be(
                        TextReference(
                            "patch.keywords.invalidForm",
                            listOf("666", "fail.test")
                        )
                    )
                }
            }
        }
    }
}