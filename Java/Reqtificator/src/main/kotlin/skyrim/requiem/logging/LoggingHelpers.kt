package skyrim.requiem.logging

import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.ThreadContext
import skyproc.MajorRecord
import skyrim.requiem.formatted

fun <T : MajorRecord> withContextLogging(logger: Logger, context: String, operation: () -> Set<T>): Set<T> {
    ThreadContext.put("context", context)
    logger.info("started processing")
    val result = operation()
    logger.info("finished processing")
    ThreadContext.remove("context")
    return result
}

fun <T : MajorRecord> withRecordLogging(record: MajorRecord, operation: () -> T): T {
    ThreadContext.put("record", record.formatted())
    val result = operation()
    ThreadContext.remove("record")
    return result
}