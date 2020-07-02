package skyrim.requiem.transformations.ammunition

import org.apache.logging.log4j.LogManager
import skyrim.requiem.Ammunition
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.keywords
import skyrim.requiem.transformations.RecordTransformer

object AmmunitionDamageAdjustment {
    private val logger = LogManager.getLogger()

    operator fun invoke() = RecordTransformer(
        selector = selector,
        transformation = transformation
    )

    private val selector: (Ammunition, LoadOrderContent) -> Boolean = { record, _ ->
        StaticReferences.Keywords.alreadyReqtified !in record.keywords &&
        StaticReferences.Keywords.noDamageRescaling !in record.keywords &&
        record.damage > 0.0f
    }

    private val transformation: (Ammunition, LoadOrderContent) -> Ammunition = { record, _ ->
        record.damage = record.damage * 4
        logger.debug("damage scaled")
        record
    }
}