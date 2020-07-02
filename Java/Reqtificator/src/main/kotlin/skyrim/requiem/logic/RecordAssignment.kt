package skyrim.requiem.logic

import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.MajorRecord
import skyproc.NPC_
import skyproc.interfaces.KeywordRecord

enum class AssignmentType {
    keywords, perks, spells
}

interface RecordAssignment<T : MajorRecord> {
    val type: AssignmentType
    val assignments: Set<FormID>

    fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>>
}

data class KeywordAssignment<T>(
    override val assignments: Set<FormID>
) : RecordAssignment<T> where
T : skyproc.MajorRecord,
T : KeywordRecord {
    constructor(vararg keywords: FormID) : this(keywords.toSet())
    constructor(keywords: Collection<FormID>) : this(keywords.toSet())

    override val type = AssignmentType.keywords

    override fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>> {
        return mapOf(GRUP_TYPE.KYWD to assignments)
    }
}

data class PerkAssignment(
    override val assignments: Set<FormID>
) : RecordAssignment<NPC_> {
    constructor(vararg keywords: FormID) : this(keywords.toSet())
    constructor(keywords: Collection<FormID>) : this(keywords.toSet())

    override val type = AssignmentType.perks

    override fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>> {
        return mapOf(GRUP_TYPE.PERK to assignments)
    }
}

data class SpellAssignment(
    override val assignments: Set<FormID>
) : RecordAssignment<NPC_> {
    constructor(vararg keywords: FormID) : this(keywords.toSet())
    constructor(keywords: Collection<FormID>) : this(keywords.toSet())

    override val type = AssignmentType.spells

    override fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>> {
        return mapOf(GRUP_TYPE.SPEL to assignments)
    }
}