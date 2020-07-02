package skyrim.requiem.logic.conditions

import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.Mod
import skyproc.NPC_
import skyproc.RACE
import skyrim.requiem.keywords
import skyrim.requiem.resolveMajor
import skyrim.requiem.resolveTemplates

object ActorCondition {
    fun NPC_.keywordsForeachTemplate(context: Mod): Set<Set<FormID>> {
        return resolveTemplates(context, NPC_.TemplateFlag.USE_KEYWORDS, NPC_.TemplateFlag.USE_TRAITS).map {
            val actorKeywords = it.getValue(NPC_.TemplateFlag.USE_KEYWORDS).keywords.toSet()
            val traitRace = context.resolveMajor(it.getValue(NPC_.TemplateFlag.USE_TRAITS).race, GRUP_TYPE.RACE) as RACE
            actorKeywords + traitRace.keywords.toSet()
        }.toSet()
    }
}

data class ActorHasAnyKeyword(
    val keywords: Set<FormID>
) : RecordCondition<NPC_> {

    override fun test(record: NPC_, context: Mod): Boolean {
        return with(ActorCondition) {
            record.keywordsForeachTemplate(context).all { it.intersect(keywords).isNotEmpty() }
        }
    }

    override fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>> {
        return mapOf(GRUP_TYPE.KYWD to keywords)
    }
}

data class ActorHasAllKeywords(
    val keywords: Set<FormID>
) : RecordCondition<NPC_> {

    override fun test(record: NPC_, context: Mod): Boolean {
        return with(ActorCondition) {
            record.keywordsForeachTemplate(context).all { it.containsAll(keywords) }
        }
    }

    override fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>> {
        return mapOf(GRUP_TYPE.KYWD to keywords)
    }
}

data class ActorHasNoKeywords(
    val keywords: Set<FormID>
) : RecordCondition<NPC_> {

    override fun test(record: NPC_, context: Mod): Boolean {
        return with(ActorCondition) {
            record.keywordsForeachTemplate(context).all { it.intersect(keywords).isEmpty() }
        }
    }

    override fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>> {
        return mapOf(GRUP_TYPE.KYWD to keywords)
    }
}

data class HasAnyRace(
    val races: Set<FormID>
) : RecordCondition<NPC_> {

    override fun test(record: NPC_, context: Mod): Boolean {
        val templates = record.resolveTemplates(context, NPC_.TemplateFlag.USE_TRAITS)
        return races.containsAll(templates.map { it.race })
    }

    override fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>> {
        return mapOf(GRUP_TYPE.RACE to races)
    }
}

data class HasNoRace(
    val races: Set<FormID>
) : RecordCondition<NPC_> {

    override fun test(record: NPC_, context: Mod): Boolean {
        val templates = record.resolveTemplates(context, NPC_.TemplateFlag.USE_TRAITS)
        return races.intersect(templates.map { it.race }).isEmpty()
    }

    override fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>> {
        return mapOf(GRUP_TYPE.RACE to races)
    }
}