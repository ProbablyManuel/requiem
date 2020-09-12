package skyrim.requiem.transformations.actors

import org.apache.logging.log4j.LogManager
import skyproc.GRUP_TYPE
import skyproc.NPC_
import skyproc.ScriptRef
import skyrim.requiem.Actor
import skyrim.requiem.LeveledCharacter
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.transformations.RecordTransformer

object GameMechanicsScripts {
    private val logger = LogManager.getLogger()

    operator fun invoke(scriptsToAdd: Set<ScriptRef>) = RecordTransformer(
        selector = selector,
        transformation = transformation(scriptsToAdd)
    )

    private val selector: (Actor, LoadOrderContent) -> Boolean = { record, context ->
        when (context.lastOverwrites.getMajor(record.template, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_)) {
            is LeveledCharacter? -> true
            is Actor? -> !record[NPC_.TemplateFlag.USE_SCRIPTS]
            else -> false
        } && (record.form != StaticReferences.Actors.player)
    }

    private val transformation: (Set<ScriptRef>) -> (Actor, LoadOrderContent) -> Actor = { scriptsToAdd ->
        { record, context ->
            scriptsToAdd.forEach { record.scriptPackage.addScript(it) }
            if (scriptsToAdd.isNotEmpty()) logger.debug("applied global scripts")
            if (context.lastOverwrites.getMajor(record.template, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_) is LeveledCharacter) {
                record[NPC_.TemplateFlag.USE_SCRIPTS] = false
            }
            record
        }
    }
}