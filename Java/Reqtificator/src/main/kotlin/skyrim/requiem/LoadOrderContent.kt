package skyrim.requiem

import skyproc.Mod
import skyproc.ModListing

data class LoadOrderContent(
    val importHistory: List<Mod>,
    val lastOverwrites: Mod
) {
    fun findMod(listing: ModListing): Mod? = importHistory.find { it.info == listing }
    fun importedMods(): List<ModListing> = importHistory.map { it.info }
}