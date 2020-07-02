package skyrim.requiem.transformations

import org.apache.logging.log4j.LogManager
import skyproc.MajorRecord
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.andThen
import skyrim.requiem.exceptions.ReqtificatorException
import skyrim.requiem.exceptions.UnexpectedFailureException
import skyrim.requiem.gui.withContextUiUpdate
import skyrim.requiem.gui.withRecordUiUpdate
import skyrim.requiem.logging.withContextLogging
import skyrim.requiem.logging.withRecordLogging
import java.lang.Exception

data class RecordTransformer<T : MajorRecord>(
    val selector: (T, LoadOrderContent) -> Boolean,
    val transformation: (T, LoadOrderContent) -> T
) {
    private val logger = LogManager.getLogger()

    fun transformRecords(
        records: Collection<T>,
        context: LoadOrderContent,
        logContextName: String,
        uiDisplayName: String
    ): Set<T> {
        val toProcess = records.filter { !it[MajorRecord.MajorFlags.Deleted] && selector(it, context) }
        return withContextLogging(logger, logContextName) {
            withContextUiUpdate(uiDisplayName, toProcess.size) {
                toProcess
                    .map { record ->
                        withRecordLogging(record) {
                            withRecordUiUpdate { this(record, context) }
                        }
                    }
                    .toSet()
            }
        }
    }

    operator fun invoke(record: T, context: LoadOrderContent): T {
        return try {
            transformation(record, context)
        } catch (e: Exception) {
            when (e) {
                is ReqtificatorException -> throw e
                else -> throw UnexpectedFailureException(record, e)
            }
        }
    }

    fun andThen(next: RecordTransformer<T>): RecordTransformer<T> {
        return RecordTransformer(
            selector = { r, ctx -> this.selector(r, ctx) || next.selector(r, ctx) },
            transformation = { r, ctx ->
                fun RecordTransformer<T>.transformWithFallback(): ((T) -> T) = { r -> if (selector(r, ctx)) transformation(r, ctx) else r }
                this.transformWithFallback().andThen(next.transformWithFallback())(r)
            }
        )
    }
}