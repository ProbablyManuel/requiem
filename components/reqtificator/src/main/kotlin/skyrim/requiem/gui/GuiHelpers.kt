package skyrim.requiem.gui

import skyproc.MajorRecord
import skyproc.gui.SUMGUI

fun <T : MajorRecord> withContextUiUpdate(context: String, numRecords: Int, operation: () -> Set<T>): Set<T> {
    SUMGUI.progress.setMax(numRecords, context)
    SUMGUI.progress.bar = 0
    return operation()
}

fun <T : MajorRecord> withRecordUiUpdate(operation: () -> T): T {
    SUMGUI.progress.incrementBar()
    return operation()
}