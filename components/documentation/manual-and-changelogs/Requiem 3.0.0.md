Requiem 3.0.0 - "Consign To Oblivion"
=====================================

The key feature of this version is a completely reworked armor penetration and arrow resistance system. All armor rating, damage reduction and armor penetration aspects of the game are now transparent and easy to understand. Armor penetration is now a viable tool for all weapons throughout the entire game and the differences between weapon damage types are emphasized further. In addition, this update contains a multitude of smaller improvements and bugfixes.

New Features
------------

* Expertise has been introduced for One-Handed, Two-Handed and Marksman and measures your armor penetration power.
* All effects that grant expertise are explicitly listed as such in perk, item or magic effect descriptions. Your total expertise for each skill can be checked in the Skills MCM.
* Melee armor penetration is applied only to power attacks; standard attacks have no armor penetration at all. All ranged attacks are armor penetrating, but ammunition quality is the determining factor.
* Each weapon now deals a dedicated damage type. (slash, crush, pierce or ranged)
* Each armor cuirass has its own weak points and strengths which determine how easy it is to penetrate the armor with a given damage type. (Can be checked in the active magic effects menu.)
* All weapons now gain notable armor penetration from perks, but blunt weapons generally remain the most efficient ones.
* Introduced perks for dagger specialization in the One-Handed tree. These provide expertise bonuses and a moderate sneak-attack bonus that applies to all targets, even those normally immune.
* Armor penetration and damage reduction calculation does not include any randomness.
* Maximum damage reduction from armor rating has been limited to 80%.
* Armor penetration can negate at most 80% of the target's armor rating.
* Hidden armor rating has been removed, cuirasses receive +55 armor rating instead, all other armor items +15 (applied after Reqtificator scaling).
* Creature resistances have been reworked to use the same rules as humanoid foes.
* In-game documentation is available in Skyrim's help menu.

Tweaks
------

* All hidden armor rating is removed; instead cuirasses get +55 armor rating and all other armor items get +15 armor rating. Note to mod authors: Armors with the REQ\_KW\_AlreadyReqtified keyword need to be manually updated to this change.
* Daedric weapons no longer have any hidden armor penetration bonuses.
* Draconic Blacksmithing requires either Ebony or Glass Smithing as a prerequisite. Ebony Smithing is required to crafting Dragonplate armor, Glass Smithing is a prerequisite for Dragonscale armor. Dragonbone Weapons can be crafted with either prerequisite perk. Daedric smithing is now an optional perk which can be taken after Ebony Smithing.
* Requiem's plugin description now contains the Smash.ForceAll tag to work better with Mator Smash.
* The skills MCM contains a new page displaying your expertise values.
* Several followers have one or more lockpicking perks and can now help you out with locks you encounter on your travels.
* Several edits to improve merging of leveled lists.
* (Re)Added the "WISpellColorful" keyword to the CandleLight spell, which makes NPCs comment about your magic.
* Various ebony items can be found on the Katariah.
* Dragons in the Forgotten Vale are always the strongest type.
* Guard start commenting 'So you can cast a few spells. Am I supposed to be impressed?' after you have learned five spells.
* Dawnguard and Vigilants of Stendarrs (including Vigilant Tyranus) are only hostile on sight to starving vampires. You can now visit Fort Dawnguard as a non-starving vampire to start the Dawnguard questline normally.
* Salma in Ironbind Barrow has appropriate training for the gear she's using.
* Renamed alchemy, food and enchantment effects to be more useful and consistent.
* The skeletons in the Hall of the Dead of Whiterun will no longer pursue you outside the catacombs.
* All Dragonpriest masks are now classified as clothing and have a weight of 2 units.
* A hint to use silver against undead is provided.
* Expertise bonuses from lockpicking perks are not shown in the active magic effects menu.
* Re-adds racial ability to horses.
* Horses have medium impact set.
* Load screens now include Requiem books and long screens are shorter.
* Drunks like free Cinnabar Beer.
* NPCs with with high level Marksmanship, One-Handed and Two-Handed perks also have the previous perks.

Bugfixes
--------

* The Oghma Infinium properly grants the reward also if you read the book before completing the quest.
* Blackblood Marauders no longer attack each other.
* Veren no longer attacks Thorek in the final fight.
* Delevels bows used by Goldenglow Estate mercenaries.
* Delevels leveled lists that contain enchanted rings.
* Delevels Fjola, Umana and Sulla Trebatius.
* Corrects chance to yield nothing of a leveled list that contains enchanted rings.
* Thalmor carry a dagger instead of a longsword as their backup weapon.
* The giant in Giant's Grove drops all his loot upon death.
* Silver Tanto is accepted as an ingredient for a certain Atronach Forge recipe instead of a Dwarven Longsword.
* LItemWeaponBlacksmithBow75 has the same content as LItemWeaponBowBlacksmith.
* Removes The Changed Ones, The Anticipations, Arcana Restored, The Night Mother's Truth and Rislav the Righteous from Requiem because these books already exist in the base game.
* Chief Burguk carries only one shield.
* Master Destruction perk now grants Lightning Cascade correctly when selected.
* Imperial soldiers and Penitus Oculatus agents carry an imperial dagger instead of a steel dagger.
* The Dawnguard blacksmith sells Silver Scimitars, Silver Katanas and Silver Tantos.
* Valdimar's class is set to level Heavy Armor.
* Empowering Veil of Silence doesn't increase upkeep cost.
* Lock bashing works properly on all wooden doors.
* All Alik'r have a chance to drop Venomous Spittle.
* NPCs use up ammunition when using the Dragonbone Bow.
* NPCs don't make comments about dangerous magic when the player has the Dawnguard Rune Shield equipped.
* Renames Frightening Orb to Fear because during "First Lessons" Faralda may ask you to cast a Fear spell.
* Sabre Cat Eye can be sold to vendors.
* Flurry increases weapon speed when wielding a single weapon.
* When dual-wielding two different weapons, Sword Focus only increases weapon speed of the sword.
* Summoned Potent Ice Wraiths don't drop lootable ice piles.
* Vilkas now has all follower-specific features, e.g. he can use bags of holding and benefit from the leadership perk.
* Drinking a potion of restore health plays a sound.
* Removed non-functional poison attacks from Invisible Entities.
* Several NPC-exclusive arrows/bolts have been given the same stats as their playable counterparts. (e.g. Dwemer Sphere bolts and Soulcairn Keeper arrows).
* The Legendary Steelplate armor (contributed from the mod SPOA Silver Knight Armor) textures have been updated and will no longer cause graphics issues on low-quality settings.
* Building the Frostbite Spider Trophy requires Venomous Spittle.
* Vampires have tripled stamina and magicka regeneration independent of race.

Reqtificator and Installation changes
-------------------------------------

* The Reqtificator now supports Java 11. Using Java 11, the UI will be rescaled on high-resolution screens to have a decent, readable size.
* Downgrading save games to older versions is not supported and Requiem will no longer allow this. In case you really want to downgrade to an older version, you must load a savegame that was made before updating to a newer version.
* Improved error diagnosis for Reqtificator launch failures.
* The optional Vanilla Dragonborn plugin now has an explicit dependency on Requiem.esp to enforce the correct load order.
* The in-game message boxes for installing and updating Requiem have their confirmation messages changed to clarify that you're expected to acknowledge the message.
* LOOT v0.13.0 and later should produce decent Requiem load orders again.

Typos, Grammar fixes and better wording
---------------------------------------

* Fixed a typo in Soul Husk's description.
* Changed the message a player receives when a new Word is learned.
