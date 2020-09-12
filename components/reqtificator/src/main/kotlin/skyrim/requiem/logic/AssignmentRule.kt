package skyrim.requiem.logic

import com.typesafe.config.Config
import com.typesafe.config.ConfigList
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueType
import skyproc.ARMO
import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.MajorRecord
import skyproc.Mod
import skyproc.NPC_
import skyproc.WEAP
import skyproc.interfaces.KeywordRecord
import skyrim.requiem.OriginRule
import skyrim.requiem.exceptions.InvalidDataInLoadOrderException
import skyrim.requiem.exceptions.SetupRequirementFailedException
import skyrim.requiem.logic.conditions.ActorHasAllKeywords
import skyrim.requiem.logic.conditions.ActorHasAnyKeyword
import skyrim.requiem.logic.conditions.ActorHasNoKeywords
import skyrim.requiem.logic.conditions.HasAllKeywords
import skyrim.requiem.logic.conditions.HasAnyKeyword
import skyrim.requiem.logic.conditions.HasAnyRace
import skyrim.requiem.logic.conditions.HasNoKeyword
import skyrim.requiem.logic.conditions.HasNoRace
import skyrim.requiem.logic.conditions.RecordCondition

data class Assignments(
    val data: Map<AssignmentType, Map<OriginRule, Set<FormID>>>
) {
    fun appliedRules(type: AssignmentType): Set<OriginRule> = data[type].orEmpty().keys
    fun recordsToApply(type: AssignmentType): Set<FormID> = data[type].orEmpty().values.flatten().toSet()
}

data class AssignmentRule<T : MajorRecord>(
    val identifier: String,
    val conditions: Set<RecordCondition<T>>,
    val assignments: Set<RecordAssignment<T>> = setOf(),
    val subNodes: Set<AssignmentRule<T>> = setOf()
) {
    fun computeAssignments(record: T, context: Mod): Assignments {
        fun AssignmentRule<T>.recursiveAssignments(): Map<AssignmentType, Map<OriginRule, Set<FormID>>> {
            return if (conditions.all { it.test(record, context) }) {
                val subs = subNodes.map { it.recursiveAssignments() }
                val initial = assignments.groupBy { it.type }.mapValues { (_, value) ->
                    value.map { identifier to it.assignments }.toMap()
                }
                subs.fold(initial) { acc, elem ->
                    elem.filterKeys { it in acc }.mapValues { elem.getValue(it.key) + acc.getValue(it.key) } +
                        elem.filterKeys { it !in acc } +
                        acc.filterKeys { it !in elem }
                }
            } else {
                mapOf()
            }
        }
        return Assignments(recursiveAssignments())
    }

    fun validateFormIds(imported: Mod) {
        fun checkExistence(form: FormID, type: GRUP_TYPE) {
            if (imported.getMajor(form, type) == null)
                throw InvalidDataInLoadOrderException("patch.keywords.unknownForm", form, type)
        }

        conditions.forEach { forms ->
            forms.getAllFormIds().forEach { entry ->
                entry.value.forEach {
                    checkExistence(it, entry.key)
                }
            }
        }
        assignments.forEach { forms ->
            forms.getAllFormIds().forEach { entry ->
                entry.value.forEach {
                    checkExistence(it, entry.key)
                }
            }
        }
        subNodes.forEach { it.validateFormIds(imported) }
    }

    companion object {

        private val formIdRegex = """^([0-9A-Fa-f]{6}) (.+\.es[pm])$""".toRegex()

        fun fromWeaponConfig(config: Config, imported: Mod): Collection<AssignmentRule<WEAP>> {
            val rules = fromConfig<WEAP>(config, ::keywordConditions, ::keywordAssignments)
            rules.forEach { it.validateFormIds(imported) }
            return rules
        }

        fun fromArmorConfig(config: Config, imported: Mod): Collection<AssignmentRule<ARMO>> {
            val rules = fromConfig<ARMO>(config, ::keywordConditions, ::keywordAssignments)
            rules.forEach { it.validateFormIds(imported) }
            return rules
        }

        fun fromActorConfig(config: Config, imported: Mod): Collection<AssignmentRule<NPC_>> {
            val rules = fromConfig<NPC_>(config, ::actorConditions, ::actorAssignments)
            rules.forEach { it.validateFormIds(imported) }
            return rules
        }

        fun <T : MajorRecord> fromConfig(
            config: Config,
            extractConditions: (List<String>, ConfigValue) -> RecordCondition<T>?,
            extractAssignments: (List<String>, ConfigValue) -> RecordAssignment<T>?
        ): Collection<AssignmentRule<T>> {

            fun extractRule(path: List<String>, config: ConfigObject): AssignmentRule<T> {
                val conditions = config.mapValues { extractConditions(path + it.key, it.value) }
                    .filterValues { it != null }
                    .mapValues { it.value!! }
                val assignments = config.mapValues { extractAssignments(path + it.key, it.value) }
                    .filterValues { it != null }
                    .mapValues { it.value!! }
                val subNodes = config.filterKeys { it !in conditions && it !in assignments }
                    .map { extractRule(path + it.key, it.value as ConfigObject) }

                return AssignmentRule(
                    identifier = path.joinToString("."),
                    conditions = conditions.values.toSet(),
                    assignments = assignments.values.toSet(),
                    subNodes = subNodes.toSet()
                )
            }

            return config.root().filter { it.key.startsWith("feature_") }.map {
                extractRule(listOf(it.key.substring(8)), it.value as ConfigObject)
            }
        }

        fun getBoolean(config: ConfigValue?, location: List<String>): Boolean {
            if (config?.valueType() == ConfigValueType.BOOLEAN) {
                return config.unwrapped() as Boolean
            } else {
                throw SetupRequirementFailedException(
                    "patch.keywords.invalidBoolean",
                    config?.unwrapped() as String,
                    location.joinToString(".")
                )
            }
        }

        fun getFormIDs(config: ConfigValue?, location: List<String>): Set<FormID> {
            return when (config) {
                null -> setOf()
                is ConfigList -> config.flatMap { getFormIDs(it, location) }.toSet()
                is ConfigObject -> config.values.flatMap { getFormIDs(it, location) }.toSet()
                else ->
                    if (config.valueType() == ConfigValueType.STRING) {
                        when (val match = formIdRegex.find(config.unwrapped() as String)) {
                            null -> throw SetupRequirementFailedException(
                                "patch.keywords.invalidForm",
                                config.unwrapped() as String,
                                location.joinToString(".")
                            )
                            else -> setOf(FormID(match.groupValues[1], match.groupValues[2]))
                        }
                    } else {
                        throw SetupRequirementFailedException(
                            "patch.keywords.invalidForm",
                            config.unwrapped().toString(),
                            location.joinToString(".")
                        )
                    }
            }
        }

        fun <T> keywordConditions(keys: List<String>, config: ConfigValue): RecordCondition<T>? where
            T : MajorRecord,
            T : KeywordRecord {
            return when (keys.last()) {
                "keywords_all" -> HasAllKeywords(getFormIDs(config, keys))
                "keywords_any" -> HasAnyKeyword(getFormIDs(config, keys))
                "keywords_none" -> HasNoKeyword(getFormIDs(config, keys))
                else -> null
            }
        }

        fun actorConditions(keys: List<String>, config: ConfigValue): RecordCondition<NPC_>? {
            return when (keys.last()) {
                "keywords_all" -> ActorHasAllKeywords(getFormIDs(config, keys))
                "keywords_any" -> ActorHasAnyKeyword(getFormIDs(config, keys))
                "keywords_none" -> ActorHasNoKeywords(getFormIDs(config, keys))
                "race_any" -> HasAnyRace(getFormIDs(config, keys))
                "race_none" -> HasNoRace(getFormIDs(config, keys))
                else -> null
            }
        }

        fun <T> keywordAssignments(keys: List<String>, config: ConfigValue): RecordAssignment<T>? where
            T : MajorRecord,
            T : KeywordRecord {
            return when (keys.last()) {
                "keywords_assign" -> KeywordAssignment(getFormIDs(config, keys))
                else -> null
            }
        }

        fun actorAssignments(keys: List<String>, config: ConfigValue): RecordAssignment<NPC_>? {
            return when (keys.last()) {
                "perks_assign" -> PerkAssignment(getFormIDs(config, keys))
                "spells_assign" -> SpellAssignment(getFormIDs(config, keys))
                else -> null
            }
        }
    }
}
