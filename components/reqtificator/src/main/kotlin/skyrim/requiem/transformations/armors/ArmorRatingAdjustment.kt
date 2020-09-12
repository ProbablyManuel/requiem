package skyrim.requiem.transformations.armors

import org.apache.logging.log4j.LogManager
import skyproc.genenums.ArmorType
import skyrim.requiem.Armor
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.configuration.ArmorProcessingConfiguration
import skyrim.requiem.configuration.ArmorRatingThresholdConfiguration
import skyrim.requiem.configuration.ArmorRatingThresholdSection
import skyrim.requiem.fptools.None
import skyrim.requiem.fptools.Some
import skyrim.requiem.keywords
import skyrim.requiem.records.armorPart
import skyrim.requiem.records.armorType
import skyrim.requiem.records.ArmorPart
import skyrim.requiem.transformations.RecordTransformer

object ArmorRatingAdjustment {
    private val logger = LogManager.getLogger()

    operator fun invoke(config: ArmorProcessingConfiguration) = RecordTransformer(
        selector = selector(config.armorRatingThresholds),
        transformation = transformation
    )

    private val selector: (ArmorRatingThresholdSection) -> (Armor, LoadOrderContent) -> Boolean = { config ->
        { record, _ ->
            val threshold = record.armorType.flatMap { type ->
                when (type) {
                    ArmorType.LIGHT -> Some(config.light)
                    ArmorType.HEAVY -> Some(config.heavy)
                    ArmorType.CLOTHING -> None<ArmorRatingThresholdConfiguration>()
                }.flatMap { section ->
                    record.armorPart.map { part ->
                        when (part) {
                            ArmorPart.Body -> section.body
                            ArmorPart.Feet -> section.feet
                            ArmorPart.Hands -> section.hands
                            ArmorPart.Head -> section.head
                            ArmorPart.Shield -> section.shield
                        }
                    }
                }
            }
            !record.isTemplated &&
                record.armorType.map { it in setOf(ArmorType.LIGHT, ArmorType.HEAVY) }.getOrElse { false } &&
                StaticReferences.Keywords.alreadyReqtified !in record.keywords &&
                StaticReferences.Keywords.noArmorRatingRescaling !in record.keywords &&
                threshold.map { record.armorRatingFloat < it }.getOrElse { true }
        }
    }

    private val transformation: (Armor, LoadOrderContent) -> Armor = { record, _ ->
        val fixedBonus = if (record.keywords.contains(StaticReferences.Keywords.armorBody)) 66.0F else 18.0F
        val factor = when (record.armorType) {
            Some(ArmorType.LIGHT) -> 3.3F
            Some(ArmorType.HEAVY) -> 6.6F
            else -> throw IllegalStateException("should not transform this record")
        }
        record.armorRatingFloat = record.armorRatingFloat * factor + fixedBonus
        logger.debug("adjusted armor rating")
        record
    }
}
