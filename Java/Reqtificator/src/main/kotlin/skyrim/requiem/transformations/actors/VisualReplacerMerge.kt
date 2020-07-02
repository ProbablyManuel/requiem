package skyrim.requiem.transformations.actors

import org.apache.logging.log4j.LogManager
import skyproc.MajorRecord
import skyproc.ModListing
import skyrim.requiem.Actor
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.transformations.RecordTransformer

object VisualReplacerMerge {
    private val logger = LogManager.getLogger()

    operator fun invoke(visualTemplateProviders: Set<ModListing>) = RecordTransformer(
        selector = selector(visualTemplateProviders),
        transformation = transformation(visualTemplateProviders)
    )

    private val selector: (Set<ModListing>) -> (Actor, LoadOrderContent) -> Boolean = { visualTemplateProviders ->
        { record, context ->
            (record.fromRequiem(context) || record.fromVisualTemplateProvider(visualTemplateProviders)) &&
                record.recordHistory.any { it.fromRequiem(context) } &&
                record.recordHistory.any { it.fromVisualTemplateProvider(visualTemplateProviders) } &&
                (record.form != StaticReferences.Actors.player)
        }
    }

    private val transformation: (Set<ModListing>) -> (Actor, LoadOrderContent) -> Actor = { visualTemplateProviders ->
        { record, context ->
            val gameplay = record.recordHistory.findLast { it.fromRequiem(context) } as Actor
            val visual = record.recordHistory.findLast { it.fromVisualTemplateProvider(visualTemplateProviders) } as Actor

            val result = gameplay.copyAttackdata(visual).copyTraits(visual)
            logger.info("merged gameplay data from ${gameplay.modImportedFrom} and visual data from ${visual.modImportedFrom}")
            result
        }
    }

    private fun MajorRecord.fromRequiem(context: LoadOrderContent): Boolean {
        val requiem = StaticReferences.requiem
        return modImportedFrom == requiem || context.findMod(modImportedFrom)?.masters.orEmpty().contains(requiem)
    }

    private fun MajorRecord.fromVisualTemplateProvider(visualTemplateProviders: Set<ModListing>): Boolean {
        return visualTemplateProviders.contains(modImportedFrom)
    }
}