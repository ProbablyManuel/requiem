package skyrim.requiem.transformations

import org.apache.logging.log4j.LogManager
import skyproc.MajorRecord
import skyproc.interfaces.KeywordRecord
import skyproc.interfaces.TemplatableRecord
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.logic.AssignmentRule
import skyrim.requiem.logic.AssignmentType

object KeywordsFromRules {
    private val logger = LogManager.getLogger()

    operator fun <T> invoke(rules: Collection<AssignmentRule<T>>): RecordTransformer<T>
        where T : MajorRecord, T : KeywordRecord, T : TemplatableRecord {
        return RecordTransformer(
            selector = selector(rules),
            transformation = transformation(rules)
        )
    }

    private fun <T> selector(rules: Collection<AssignmentRule<T>>): (T, LoadOrderContent) -> Boolean
        where T : MajorRecord, T : KeywordRecord, T : TemplatableRecord {
        return { record, context ->
            !record.isTemplated && rules.any { rule ->
                rule.computeAssignments(record, context.lastOverwrites).recordsToApply(AssignmentType.keywords)
                    .isNotEmpty()
            }
        }
    }

    private fun <T> transformation(rules: Collection<AssignmentRule<T>>): (T, LoadOrderContent) -> T
        where T : MajorRecord, T : KeywordRecord, T : TemplatableRecord {
        return { record, context ->
            rules.forEach { rule ->
                val toAssign = rule.computeAssignments(record, context.lastOverwrites)
                toAssign.recordsToApply(AssignmentType.keywords).forEach { record.keywordSet.addKeywordRef(it) }
                toAssign.appliedRules(AssignmentType.keywords).forEach { logger.debug("applied rule $it (keywords)") }
            }
            record
        }
    }
}