package skyrim.requiem.tests.transformations

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrow
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import io.mockk.every
import io.mockk.mockk
import skyproc.MajorRecord
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.exceptions.InvalidDataInLoadOrderException
import skyrim.requiem.exceptions.UnexpectedFailureException
import skyrim.requiem.transformations.RecordTransformer

class RecordTransformerSpec : WordSpec() {
    init {

        class Fixture {
            val selector: (MajorRecord, LoadOrderContent) -> Boolean = { _, _ -> true }
            val trafo = mockk<(MajorRecord, LoadOrderContent) -> MajorRecord>()
            val ctx = mockk<LoadOrderContent>()
            val transformer = RecordTransformer(selector, trafo)
        }

        "A RecordTransformer" should {

            "return a record that was successfully transformed" {
                val f = Fixture()

                val record = mockk<MajorRecord>()
                every { f.trafo(record, f.ctx) } returns record

                shouldNotThrow<Exception> { f.transformer(record, f.ctx) }
                f.transformer(record, f.ctx) shouldBe record
            }

            "bubble up instances of ReqtificatorException unchanged" {
                val f = Fixture()

                val record = mockk<MajorRecord>()
                val exception = InvalidDataInLoadOrderException("blabla")
                every { f.trafo(record, f.ctx) } throws exception

                val result = shouldThrow<InvalidDataInLoadOrderException> { f.transformer(record, f.ctx) }
                result shouldBe exception
            }

            "wrap unexpected exceptions into an UnexpectedFailureException with additional context" {
                val f = Fixture()

                val record = mockk<MajorRecord>()
                val exception = IllegalArgumentException("blubber")
                every { f.trafo(record, f.ctx) } throws exception

                val result = shouldThrow<UnexpectedFailureException> { f.transformer(record, f.ctx) }
                result shouldBe UnexpectedFailureException(record, exception)
            }
        }
    }
}