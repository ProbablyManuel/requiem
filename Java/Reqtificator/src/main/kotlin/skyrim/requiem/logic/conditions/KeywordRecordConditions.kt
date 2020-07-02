package skyrim.requiem.logic.conditions

import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.MajorRecord
import skyproc.Mod
import skyproc.interfaces.KeywordRecord
import skyrim.requiem.keywords

data class HasAllKeywords<T>(
    val keywords: Set<FormID>
) : RecordCondition<T> where
T : MajorRecord,
T : KeywordRecord {
    constructor(vararg keywords: FormID) : this(keywords.toSet())

    override fun test(record: T, context: Mod): Boolean {
        return keywords.all { it in record.keywords }
    }

    override fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>> {
        return mapOf(GRUP_TYPE.KYWD to keywords)
    }
}

data class HasAnyKeyword<T>(
    val keywords: Set<FormID>
) : RecordCondition<T> where
T : MajorRecord,
T : KeywordRecord {
    constructor(vararg keywords: FormID) : this(keywords.toSet())

    override fun test(record: T, context: Mod): Boolean {
        return keywords.any { it in record.keywords }
    }

    override fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>> {
        return mapOf(GRUP_TYPE.KYWD to keywords)
    }
}

data class HasNoKeyword<T>(
    val keywords: Set<FormID>
) : RecordCondition<T> where
T : MajorRecord,
T : KeywordRecord {
    constructor(vararg keywords: FormID) : this(keywords.toSet())

    override fun test(record: T, context: Mod): Boolean {
        return keywords.none { it in record.keywords }
    }

    override fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>> {
        return mapOf(GRUP_TYPE.KYWD to keywords)
    }
}