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

object NpcAmmoUsageAdjustment {
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
            !record[WEAP.WeaponFlag.NPCsUseAmmo] &&
            record.weaponClass.map { it in setOf(WEAP.WeaponType.Bow, WEAP.WeaponType.Crossbow) }.getOrElse { false }
    }

    private val transformation: (Weapon, LoadOrderContent) -> Weapon = { record, _ ->
        record[WEAP.WeaponFlag.NPCsUseAmmo] = true
        logger.debug("enabled NPC ammo usage")
        record
    }
}