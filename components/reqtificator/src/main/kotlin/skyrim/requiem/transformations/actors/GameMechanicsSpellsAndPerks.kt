package skyrim.requiem.transformations.actors

import org.apache.logging.log4j.LogManager
import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.NPC_
import skyrim.requiem.Actor
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.logic.AssignmentRule
import skyrim.requiem.logic.AssignmentType
import skyrim.requiem.transformations.RecordTransformer

object GameMechanicsSpellsAndPerks {
    private val logger = LogManager.getLogger()

    operator fun invoke(
        assignmentRules: Collection<AssignmentRule<Actor>>,
        globalPerks: Set<FormID>
    ): RecordTransformer<Actor> = RecordTransformer(
        selector = selector,
        transformation = transformation(assignmentRules, globalPerks)
    )

    private val selector: (Actor, LoadOrderContent) -> Boolean = { record, context ->
            val hasTemplate = context.lastOverwrites.getMajor(record.template, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) != null
            ! (hasTemplate && record[NPC_.TemplateFlag.USE_SPELL_LIST])
        }

    private val transformation: (Collection<AssignmentRule<Actor>>, Set<FormID>) -> (Actor, LoadOrderContent) -> Actor = {
            rules, globalPerks ->
        { record, context ->
            rules.forEach { rule ->
                val toAssign = rule.computeAssignments(record, context.lastOverwrites)
                toAssign.recordsToApply(AssignmentType.perks).forEach { record.addPerk(it, 1) }
                toAssign.appliedRules(AssignmentType.perks).forEach { logger.debug("applied rule $it (perks)") }
                toAssign.recordsToApply(AssignmentType.spells).forEach { record.addSpell(it) }
                toAssign.appliedRules(AssignmentType.spells).forEach { logger.debug("applied rule $it (spells)") }
            }
            globalPerks.forEach { record.addPerk(it, 1) }
            if (globalPerks.isNotEmpty()) logger.debug("applied global perks (legacy mechanism)")
            record
        }
    }
}