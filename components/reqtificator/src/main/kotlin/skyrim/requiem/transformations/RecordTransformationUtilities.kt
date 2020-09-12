package skyrim.requiem.transformations

import Reqtificator.FormIDStash
import skyproc.FLST
import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.MajorRecord
import skyproc.Mod
import skyproc.ModListing
import skyproc.ScriptRef
import skyrim.requiem.Actor
import skyrim.requiem.Ammunition
import skyrim.requiem.Armor
import skyrim.requiem.Weapon
import skyrim.requiem.configuration.ArmorProcessingConfiguration
import skyrim.requiem.configuration.PlayerConfiguration
import skyrim.requiem.exceptions.InvalidDataInLoadOrderException
import skyrim.requiem.localization.UrlReference
import skyrim.requiem.logic.AssignmentRule
import skyrim.requiem.transformations.actors.PlayerSpecificChanges
import skyrim.requiem.transformations.actors.GameMechanicsScripts
import skyrim.requiem.transformations.actors.GameMechanicsSpellsAndPerks
import skyrim.requiem.transformations.actors.VisualReplacerMerge
import skyrim.requiem.transformations.ammunition.AmmunitionDamageAdjustment
import skyrim.requiem.transformations.armors.ArmorRatingAdjustment
import skyrim.requiem.transformations.armors.ArmorTypeKeywordApplication
import skyrim.requiem.transformations.weapons.MeleeWeaponRangeAdjustment
import skyrim.requiem.transformations.weapons.NpcAmmoUsageAdjustment
import skyrim.requiem.transformations.weapons.RangedWeaponSpeedAdjustment
import skyrim.requiem.transformations.weapons.WeaponDamageAdjustment

object RecordTransformationUtilities {

    fun actorTransformation(
        playerConfig: PlayerConfiguration,
        assignmentRules: Collection<AssignmentRule<Actor>>,
        globalPerks: Set<FormID>,
        globalScripts: Set<ScriptRef>,
        visualTemplateProviders: Set<ModListing>
    ): RecordTransformer<Actor> =
        PlayerSpecificChanges(playerConfig)
            .andThen(GameMechanicsSpellsAndPerks(assignmentRules, globalPerks))
            .andThen(GameMechanicsScripts(globalScripts))
            .andThen(VisualReplacerMerge(visualTemplateProviders))

    fun ammunitionTransformations(): RecordTransformer<Ammunition> =
        AmmunitionDamageAdjustment()

    fun armorTransformations(
        config: ArmorProcessingConfiguration,
        assignmentRules: Collection<AssignmentRule<Armor>>
    ): RecordTransformer<Armor> =
        ArmorRatingAdjustment(config)
            .andThen(ArmorTypeKeywordApplication())
            .andThen(KeywordsFromRules(assignmentRules))

    fun weaponTransformations(
        assignmentRules: Collection<AssignmentRule<Weapon>>
    ): RecordTransformer<Weapon> =
        WeaponDamageAdjustment()
            .andThen(MeleeWeaponRangeAdjustment())
            .andThen(RangedWeaponSpeedAdjustment())
            .andThen(NpcAmmoUsageAdjustment())
            .andThen(KeywordsFromRules(assignmentRules))

    fun <T : MajorRecord> commitRecordsToPatch(records: Set<T>, generatedPatch: Mod) {
        records.forEach { generatedPatch.addRecord(it) }
    }

    fun loadMechanicsPerksFromPlugins(context: Mod): Set<FormID> {
        val perkList = context.getMajor(FormIDStash.formlist_GM_perks, GRUP_TYPE.FLST)!! as FLST

        return perkList.recordHistory.fold(setOf()) { acc, version ->
            (version!! as FLST).formIDEntries.forEach {
                if (context.getMajor(it, GRUP_TYPE.PERK) == null) {
                    throw InvalidDataInLoadOrderException(
                        "patch.actors.wrong_type_in_mechanics_perks_list",
                        it,
                        version.modImportedFrom.print(),
                        UrlReference("urls.mechanics_perks")
                    )
                }
            }
            acc + (version as FLST).formIDEntries
        }
    }
}