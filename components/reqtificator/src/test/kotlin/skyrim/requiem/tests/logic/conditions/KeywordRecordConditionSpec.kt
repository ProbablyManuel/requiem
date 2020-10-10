package skyrim.requiem.tests.logic.conditions

import io.kotest.matchers.be
import io.kotest.matchers.should
import io.mockk.every
import io.mockk.mockk
import skyproc.ARMO
import skyproc.FormID
import skyrim.requiem.keywords
import skyrim.requiem.logic.conditions.HasAllKeywords
import skyrim.requiem.logic.conditions.HasAnyKeyword
import skyrim.requiem.logic.conditions.HasNoKeyword
import skyrim.requiem.tests.WordSpecWithStaticMockks

class KeywordRecordConditionSpec : WordSpecWithStaticMockks() {
    override val staticMockks = arrayOf("skyrim.requiem.ExtensionsKt")

    init {
        val sampleKeywords = listOf(
            FormID("123456", "foo.esm"),
            FormID("ABCDEF", "bar.esp"),
            FormID("ABC123", "gus.esp")
        )

        "HasAllKeywords" should {
            "return true if the record has all the specified keywords" {
                val record = mockk<ARMO>()
                every { record.keywords } returns sampleKeywords

                val condition = HasAllKeywords<ARMO>(sampleKeywords.take(2).toSet())
                condition.test(record, mockk()) should be(true)
            }

            "return false if the record does not have all the specified keywords" {
                val record = mockk<ARMO>()
                every { record.keywords } returns sampleKeywords.takeLast(2)

                val condition = HasAllKeywords<ARMO>(sampleKeywords.take(2).toSet())
                condition.test(record, mockk()) should be(false)
            }
        }

        "HasAnyKeyword" should {
            "return true if the record has at least one of the specified keywords" {
                val record = mockk<ARMO>()
                every { record.keywords } returns sampleKeywords.takeLast(2)

                val condition = HasAnyKeyword<ARMO>(sampleKeywords.take(2).toSet())
                condition.test(record, mockk()) should be(true)
            }

            "return false if the record does not have any of the specified keywords" {
                val record = mockk<ARMO>()
                every { record.keywords } returns sampleKeywords.takeLast(2)

                val condition = HasAnyKeyword<ARMO>(sampleKeywords.take(1).toSet())
                condition.test(record, mockk()) should be(false)
            }
        }

        "HasNoKeyword" should {
            "return true if the record has none of the specified keywords" {
                val record = mockk<ARMO>()
                every { record.keywords } returns sampleKeywords.takeLast(2)

                val condition = HasNoKeyword<ARMO>(sampleKeywords.take(1).toSet())
                condition.test(record, mockk()) should be(true)
            }

            "return false if the record has at least one of the specified keywords" {
                val record = mockk<ARMO>()
                every { record.keywords } returns sampleKeywords.takeLast(2)

                val condition = HasNoKeyword<ARMO>(sampleKeywords.take(2).toSet())
                condition.test(record, mockk()) should be(false)
            }
        }
    }
}