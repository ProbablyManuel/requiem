package skyrim.requiem.transformations.weapons

import org.apache.logging.log4j.LogManager
import skyproc.WEAP
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.Weapon
import skyrim.requiem.fptools.Some
import skyrim.requiem.isNotTemplated
import skyrim.requiem.keywords
import skyrim.requiem.records.weaponClass
import skyrim.requiem.transformations.RecordTransformer

object WeaponDamageAdjustment {
    private val logger = LogManager.getLogger()

    operator fun invoke() = RecordTransformer(
        selector = selector,
        transformation = transformation
    )

    private val selector: (Weapon, LoadOrderContent) -> Boolean = { record, _ ->
        record.isNotTemplated &&
            StaticReferences.Keywords.alreadyReqtified !in record.keywords &&
            StaticReferences.Keywords.noDamageRescaling !in record.keywords &&
            record.weaponClass.nonEmpty()
    }

    private val transformation: (Weapon, LoadOrderContent) -> Weapon = { record, _ ->
        when (record.weaponClass) {
            Some(WEAP.WeaponType.Crossbow) -> record.damage = record.damage * 4
            Some(WEAP.WeaponType.Bow) -> record.damage = record.damage * 4
            is Some -> record.damage = record.damage * 6
            else -> throw IllegalStateException("should not patch this record")
        }
        logger.debug("damage scaled")
        record
    }
}