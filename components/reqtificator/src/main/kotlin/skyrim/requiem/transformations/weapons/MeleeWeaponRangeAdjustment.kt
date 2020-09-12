package skyrim.requiem.transformations.weapons

import org.apache.logging.log4j.LogManager
import skyproc.WEAP
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.Weapon
import skyrim.requiem.isNotTemplated
import skyrim.requiem.keywords
import skyrim.requiem.records.weaponClass
import skyrim.requiem.transformations.RecordTransformer

object MeleeWeaponRangeAdjustment {
    private val logger = LogManager.getLogger()

    operator fun invoke() = RecordTransformer(
        selector = selector,
        transformation = transformation
    )

    private val selector: (Weapon, LoadOrderContent) -> Boolean = { record, _ ->
        record.isNotTemplated &&
            StaticReferences.Keywords.alreadyReqtified !in record.keywords &&
            StaticReferences.Keywords.noWeaponReachRescaling !in record.keywords &&
            record.weaponClass.map { it !in setOf(WEAP.WeaponType.Bow, WEAP.WeaponType.Crossbow) }.getOrElse { false }
    }

    private val transformation: (Weapon, LoadOrderContent) -> Weapon = { record, _ ->
        record.reach = record.reach * 0.7F
        logger.debug("range scaled (melee weapon)")
        record
    }
}