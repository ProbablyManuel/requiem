package skyrim.requiem.logic.conditions

import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.MajorRecord
import skyproc.Mod

interface RecordCondition<T : MajorRecord> {
    fun getAllFormIds(): Map<GRUP_TYPE, Collection<FormID>>
    fun test(record: T, context: Mod): Boolean
}