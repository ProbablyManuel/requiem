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

object RangedWeaponSpeedAdjustment {
    private val logger = LogManager.getLogger()

    operator fun invoke() = RecordTransformer(
        selector = selector,
        transformation = transformation
    )

    private val selector: (Weapon, LoadOrderContent) -> Boolean = { record, _ ->
        record.isNotTemplated &&
            StaticReferences.Keywords.alreadyReqtified !in record.keywords &&
            StaticReferences.Keywords.noWeaponSpeedRescaling !in record.keywords &&
            !record[WEAP.WeaponFlag.NonPlayable] &&
            record.weaponClass.map { it in setOf(WEAP.WeaponType.Bow, WEAP.WeaponType.Crossbow) }.getOrElse { false }
    }

    private val transformation: (Weapon, LoadOrderContent) -> Weapon = { record, _ ->
        when (record.weaponClass) {
            Some(WEAP.WeaponType.Crossbow) -> {
                record.speed = 0.4445F
                record.keywordSet.addKeywordRef(StaticReferences.Keywords.weaponCrossbowHeavy)
            }
            Some(WEAP.WeaponType.Bow) -> {
                record.speed = 0.3704F
                record.keywordSet.addKeywordRef(StaticReferences.Keywords.weaponBowHeavy)
            }
            else -> throw IllegalStateException("should not patch this record")
        }
        logger.debug("attack speed scaled (ranged weapon)")
        record
    }
}