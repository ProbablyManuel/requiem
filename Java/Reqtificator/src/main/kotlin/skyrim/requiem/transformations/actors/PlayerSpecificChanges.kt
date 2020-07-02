package skyrim.requiem.transformations.actors

import org.apache.logging.log4j.LogManager
import skyrim.requiem.Actor
import skyrim.requiem.LoadOrderContent
import skyrim.requiem.StaticReferences
import skyrim.requiem.configuration.PlayerConfiguration
import skyrim.requiem.staminaOffset
import skyrim.requiem.transformations.RecordTransformer

object PlayerSpecificChanges {
    private val logger = LogManager.getLogger()

    operator fun invoke(playerConfig: PlayerConfiguration) = RecordTransformer(
        selector = { r, _ -> r.form == StaticReferences.Actors.player },
        transformation = transformPlayerRecord(playerConfig)
    )

    private val transformPlayerRecord: (PlayerConfiguration) -> (Actor, LoadOrderContent) -> Actor = { playerConfig ->
        { player, _ ->
            player.healthOffset = playerConfig.healthOffset
            player.magickaOffset = playerConfig.magickaOffset
            player.staminaOffset = playerConfig.staminaOffset
            logger.debug("adjusted player attribute bonus")
            playerConfig.spellsToRemove.forEach { player.removeSpell(it) }
            logger.debug("removed player starting spells")
            player
        }
    }
}