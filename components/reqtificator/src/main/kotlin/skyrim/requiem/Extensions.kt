package skyrim.requiem

import org.apache.logging.log4j.LogManager
import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.LVLN
import skyproc.MajorRecord
import skyproc.Mod
import skyproc.NPC_
import skyproc.interfaces.KeywordRecord
import skyproc.interfaces.TemplatableRecord
import skyrim.requiem.exceptions.InvalidRecordException

val logger = LogManager.getLogger()

var NPC_.staminaOffset: Int
    get() = this.fatigueOffset
    set(value) {
        this.fatigueOffset = value
    }

val KeywordRecord.keywords: List<FormID>
    get() = this.keywordSet.keywordRefs

val TemplatableRecord.isNotTemplated: Boolean
    get() = !this.isTemplated

fun MajorRecord.formatted(): String {
    return "[ $type | $edid | $formStr | ${modImportedFrom.print()} ]"
}

fun Mod.resolveMajor(formID: FormID, vararg types: GRUP_TYPE): MajorRecord {
    return getMajor(formID, *types) ?: throw InvalidRecordException(formID, *types)
}

fun NPC_.resolveTemplates(context: Mod, flag: NPC_.TemplateFlag): Set<NPC_> {
    return resolveTemplates(context, setOf(flag)).map { it.getValue(flag) }.toSet()
}

fun NPC_.resolveTemplates(context: Mod, vararg flags: NPC_.TemplateFlag): Set<Map<NPC_.TemplateFlag, NPC_>> {
    return resolveTemplates(context, flags.toSet())
}

fun NPC_.resolveTemplates(
    context: Mod,
    flags: Set<NPC_.TemplateFlag> = NPC_.TemplateFlag.values().toSet()
): Set<Map<NPC_.TemplateFlag, NPC_>> {
    return if (template != FormID.NULL) {
        val (inherited, declared) = flags.partition { this[it] }
        val declaredFeatures = declared.map { it to this }.toMap()
        if (inherited.isNotEmpty()) {
            val inheritedFeatures = resolveTemplates(context, template, inherited.toSet())
            inheritedFeatures.map { it + declaredFeatures }.toSet()
        } else {
            setOf(declaredFeatures)
        }
    } else {
        if (NPC_.TemplateFlag.values().any { this[it] }) {
            logger.warn("record $this has inheritance flags set, but no template defined")
        }
        setOf(flags.map { it to this }.toMap())
    }
}

fun LVLN.resolveTemplates(context: Mod, vararg flags: NPC_.TemplateFlag): Set<Map<NPC_.TemplateFlag, NPC_>> {
    return resolveTemplates(context, flags.toSet())
}

fun LVLN.resolveTemplates(
    context: Mod,
    flags: Set<NPC_.TemplateFlag> = NPC_.TemplateFlag.values().toSet()
): Set<Map<NPC_.TemplateFlag, NPC_>> {
    return entryForms.flatMap { resolveTemplates(context, it, flags) }.toSet()
}

private fun resolveTemplates(
    context: Mod,
    template: FormID,
    flags: Set<NPC_.TemplateFlag>
): Set<Map<NPC_.TemplateFlag, NPC_>> {
    return when (val record = context.resolveMajor(template, GRUP_TYPE.NPC_, GRUP_TYPE.LVLN)) {
        is NPC_ -> record.resolveTemplates(context, flags)
        is LVLN -> record.resolveTemplates(context, flags)
        else -> throw IllegalStateException("should have been caught by context.resolveMajor")
    }
}