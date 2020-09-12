package skyrim.requiem.transformations.armors

import org.apache.logging.log4j.LogManager
import skyproc.genenums.ArmorType
import skyrim.requiem.Armor
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.fptools.Some
import skyrim.requiem.keywords
import skyrim.requiem.records.armorType
import skyrim.requiem.transformations.RecordTransformer

object ArmorTypeKeywordApplication {
    private val logger = LogManager.getLogger()

    operator fun invoke() = RecordTransformer(
        selector = selector,
        transformation = transformation
    )

    private val selector: (Armor, LoadOrderContent) -> Boolean = { record, _ ->
        val keywordMissing = when (record.armorType) {
            Some(ArmorType.HEAVY) -> StaticReferences.Keywords.armorHeavy !in record.keywords
            Some(ArmorType.LIGHT) -> StaticReferences.Keywords.armorLight !in record.keywords
            else -> false
        }
        !record.isTemplated && keywordMissing
    }

    private val transformation: (Armor, LoadOrderContent) -> Armor = { record, _ ->
        when (record.armorType) {
            Some(ArmorType.HEAVY) -> record.keywordSet.addKeywordRef(StaticReferences.Keywords.armorHeavy)
            Some(ArmorType.LIGHT) -> record.keywordSet.addKeywordRef(StaticReferences.Keywords.armorLight)
            else -> throw IllegalStateException("should not transform this record")
        }
        logger.debug("added armor type keyword")
        record
    }
}