Requiem 5.0.0 - "From the Ashes"
================================

The main feature of this version is official support for the Skyrim Special Edition. Other changes have been limited to the extend necessary for the port. The Skyrim Legendary Edition is no longer supported.

Tweaks
------

* The dialogue option to travel to Solstheim is restored to the captain in Windhelm.
* Miraak's cultists are only encountered in Solstheim.
* Dragonborn loading screens are restored. Loading screens that you wouldn't expect to see in Skyrim only appear in Solstheim.
* The Ebony Warrior is restored. However, due to the encounter's extremely high level requirement it remains effectively disabled.
* The old books "Zerus Morphus and the Helm that Wasn't" and "Zerus Morphus and the Chaos Wizards" are removed.

Bugfixes
--------

* Synchronize with Unofficial Skyrim Special Edition Patch v4.2.6a.

Installation
------------

* The option to enable access to the Dragoborn DLC is removed because it's no longer necessary.
* SSE Engine Fixes is a recommended dependency as it fixes an important bug in the game's engine for Requiem.
* Consistency files for the Reqtificator are stored in `Documents\My Games\Skyrim Special Edition\Requiem`.

Compatibility and Modding Support
---------------------------------

* Visual automerging identifies the mod providing the visual template automatically on a per record basis for both actors and races without any input from the user.
* Updated Bash tags are added to `BashTags\Requiem.txt` because the list is too long to fit in the plugin description.
* Modgroups file for SSEEdit is available.
* Rule configuration files used to distribute keywords, spells and perks are processed in isolation. All the placeholders referenced in the file must be declared in the same file. It also is no longer possible to partially override existing rules by redeclaring their nodes in other files.
* Arrays in the rule configuration files only support elements of the same type, it is no longer possible to e.g. mix object and string elements in the same array.
* Playable custom races are patched with the basic changes required to be compliant with Requiem mechanics. For more advanced custom races, you should still consider using a handmade compatibility patch. Vampire variants of custom races are not patched.
* Requiem's assets are shipped as a BSA instead of loose files.
* The Reqtificator no longer allows priority-overrides in leveled list merges. The corresponding formlists `RFTI_List_LLMerge_HighPriority` (`xxAD36E7`) and `RFTI_List_LLMerge_MediumPriority` (`xxAD36E6`) have been deleted.
* Configuration files for the Reqtificator moved to the folders `Reqtificator/Config` and `Reqtificator/Data` inside the `Data` folder of the Skyrim installation.

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* Disabled content is disabled by comparing to the `TRUEGlobal` variable which is how the base game handles such conditions.
* Edits to Dragonborn tempering recipes added by the Unofficial Patch are removed because Requiem has no real reason to edit them.
* Tempering recipes for chitin armor have proper EditorIDs.
* Unused golden elven weapon meshes are removed.
* Clashing EditorIDs are renamed.
* Content of nullified illusion records is reverted to their vanilla state.
* Elsi isn't marked as unique because the Creation Kit doesn't allow this flag on actors that have a leveled template.
* All nullified recipes are disabled by means of assigning `REQ_DisableRecipe` as workbench keyword.
* Plugin description observes the length limit imposed by the CK.
* Detecting if a Bashed Patch was used to merge leveled lists is implemented without the usage of `Game.GetFormFromFile`.
