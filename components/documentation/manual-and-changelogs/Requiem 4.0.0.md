Requiem 4.0.0 - "Threshold"
===========================

The presentation of core game concepts like armor rating and armor penetration has been polished to present themselves in a more intuitive way to the player. A variety of no longer used assets has been removed or replaced with new assets. Guards have new armors that don't outclass the military and the Vigilants also have new armors. The Halvar Brothers now have a small background story and more unique items to loot. This update also contains several major quality of life improvements for mod authors like the possibility to assign final armor ratings in plugins and way better organized bandit records.

New Features
------------

* Melee guards and housecarls wear armors from [Varied Guard Armor](https://www.nexusmods.com/skyrim/mods/96744) and the stats of their armors are adjusted to match the new designs.
* Vigilants of Stendarr wear the armor from [Mage's Plate Armor](https://www.nexusmods.com/skyrim/mods/92916). They usually wear a full set of armor with useful enchantments.
* The Jarl's Longhouses in Dawnstar, Falkreath, Morthal and Winterhold have master-locked chests with rich treasures in the Jarl's room.
* The Halvar Brothers have a little background story. The Warhammer of Inebriation is a Nordic warhammer and its enchantment is only fueled by alcohol. Halvar the Other has his own unique loot.

Tweaks
------

* Armor rating is reworked to be more intuitive. Each point of armor rating reduces incoming damage by 0.1% (previously 0.12%) to allow straightforward calculations of damage reduction based on the armor rating. All existing sources of armor rating are adjusted to provide the same damage reduction as before.
* One-handed, two-handed and marksman "expertise" is renamed to "armor penetration". All values are adjusted for a 1:1 translation and tweaked as follows.
    * Marksman perks don't grant armor penetration. Armor penetration from ammunition is increased.
    * Most one-handed and two-handed perks don't grant a small amount of armor penetration.
    * Armor penetration from the Warrior and Thief stone is increased.
    * Armor penetration from the blessing of Auriel is increased.
    * Amulet and blessing of Talos increase one-handed and two-handed armor penetration. They don't reduce shout cooldown.
    * Potions and ingredients increase damage instead of armor penetration. Magnitude of potions is halved. Only the potions found in Thieves Guild caches increase armor penetration.
    * (Ancient) Shrouded Gloves and Linwe's Gauntlets increases armor penetration with all weapons.
    * Ironhand Gauntlets and Irileth's Ebony Gauntlets increase damage instead of armor penetration.
    * The Slighted don't have additional marksman armor penetration.
* Amulet and blessing of Akatosh reduce shout cooldown. They don't increase magicka regeneration.
* Thalmor Embassy and the Thalmor Headquarters in Solitude have a shrine of Auriel.
* Bag of holdings increase carry weight by 60 points.
* Daedric melee weapons and ammo deal 50% more damage to Dremora and atronachs. This doesn't extend to Daedric artifacts.
* ActorVariations are based on appropriate vanilla actors instead of player presets and provide more variety for the dominant races.
* The distorted vision caused by alcohol is less severe. The Charmed Necklace gives clear sight regardless of the level of inebriation.
* NPCs are affected by racial power attack cost and spell power modifiers.
* NPCs benefit from weight reduction for worn armors.
* Absorb spells affect undead.
* Spectral Arrow deals irresistible damage.
* Empowering Fear reduces armor rating by 200.
* Frightening Sphere reduces armor rating by 150.
* Blunt damage resistance of draugr and dragon priests is reduced.
* Slashing damage resistance of Dwarven centurions and enchanted spheres is reduced.
* Misdirection allows to pickpocket equipped jewelry.
* Lockpicking XP is reduced by 20%.
* Merchant respawn time is reduced to 5 days.
* Bandits can have potions of restore stamina.
* Alchemists always have some healing poultices and potions of restore health/magicka/stamina for sale.
* Price of Healing Poultice is reduced to 10 gold.
* Aedric and Daedric artifacts only require and benefit from Legendary Blacksmithing. Mundane artifacts only require and benefit from the corresponding material perk.
* Cuirass/helmet/gauntlets/boots have 50/20/15/15% of the set's total armor rating, weight and price. Shields have 30% of the set's armor rating and 25% of the set's weight and price.
* Armors have more logical and consistent prices.
* Blades, Daedric and Dawnguard armor have increased armor rating and weight.
* Wolf armor and most low-level light armors have reduced weight.
* Guard and Stormcloak armor have reduced armor rating and weight.
* Armor rating and weight of Ebony Mail are on par with ebony armor to match its visual appearance. Pierce resistance is increased.
* Auriel's Shield is light armor.
* Dawnguard Rune Shield is heavy armor.
* Masque of Clavicus Vile is clothing.
* Weight of the Greybeard's Robes is reduced and weight of Greybeard's Boots and Hood is increased.
* Greybeard's Boots and Hood reduce shout cooldown time.
* The cuirass worn by Knight-Paladin Gelebor can be looted by the player.
* Fine Armguards and Gilded Wristguards are clothing.
* Two Mage Robes that are identical to Blue Robes in all but name are removed.
* Clubs, katanas, longswords, sabers, scimitars and tantos are removed. Hand-placed references and NPCs carrying these weapons use corresponding standard weapons.
* Ebony longsword and dagger in Fort Greenwall are removed because they could be acquired too easily and are nearly impossible to find without metagaming.
* Glass weapons in the White Hall and Falkreath's Longhouse are removed because they were freely available.
* A glass crossbow can be found in the Thalmor Embassy, Barracks.
* Halvar brothers wear Nordic armor.
* Redguard clothes are changed to light armor on par with leather armor and are now known as Alik'r armor.
* Kematu and his guards wear enchanted Alik'r armor and jewelry instead of Hammerfell chainmail.
* Nazir's armor has the same stats as Alik'r armor and powerful enchantments.
* General Tullius wears his vanilla armor as a light armor with appropriate stats and enchantments.
* Imperial Legates and commanders wear Imperial heavy armor with the Imperial Officer's helmet.
* Lieutenant Salvarus wears Penitus Oculatus armor.
* Stormcloak Officers wear the Stormcloak Officer's helmet.
* Housecarls have a helmet in their inventory.
* A new help menu entry explains poisons and how poison resistance affects the strength of visual distortions.

Bugfixes
--------

* Armors added by the Dawnguard and Dragonborn DLC don't receive an armor rating boost from wearing a matching set.
* Falmer are reverted to their normal size so that they don't get stuck in their huts.
* Blacksmiths sell the indended amount of basic crafting materials.
* ActorVariations don't spawn gender and race variants that have no bandit dialogue.
* Blessing of Auriel lasts 24 hours.
* Orcish bolts increase armor penetration by the correct amount.
* Vexing Flanker doesn't affect ranged damage.
* Mystic Disruption doesn't mention irresistable damage because the perk has no such effect.
* Misdirection works on weapons that cannot be sold.
* Damage from knockdown attacks uses the correct armor rating formula.
* General Tullius and Kematu wear their necklace and ring.
* Vaialag doesn't attack the nearby ice wraith.
* Poachers near the mammoth graveyard respawn.
* All meat restores only stamina.
* Lightning Scroll uses the correct effects.
* Ebony Blade deals full damage to ghosts.
* Volendrung has the same speed and stagger as a Daedric warhammer.
* Keening, Volendrung and Wuuthrad have the correct noise level.
* Enchanted armors have the correct base price.
* Price of Falmer bows matches other Falmer weapons.
* All armors benefit from exactly one smithing perk.
* Tempering recipe for Bolar's Oathblade matches Blades sword.
* Duplicate tempering recipe for Keening is removed.
* Stormcloak Officer Armor and Reinforced Eastmarch Guard Armor are eligible for disguising as Stormcloak.
* Ulfric's Shield and Armor of the Old Gods can be disenchanted.
* Non-playable bows match the playable variant.
* Non-playable Amulet of Articulation matches the normal version.
* Ancient Nord Helmet worn by draugr isn't considered armor.
* Vampire Lord Ornament isn't considered light armor.
* Movement speed of the horse carrying the cart in the Helgen opening scene is reduced by 1%. Apparently this solves the random physics incidents in the opening scene.
* The Reqtificator doesn't include ITPO armor and race records in the generated patch.

* Several typos are corrected.
* Armor penetration bonus from ammunition references Ranged Combat Training instead of Precise Aim.

Installation
------------

* The ingame installation process is simplified. The ingame installation always triggers the first time the player closes the inventory or magic menu. Requiem will also periodically remind the player to finish the installation process. These changes should resolve some very rare occurrences where the ingame installation did not trigger properly.
* "Cobb Bug Fixes" are officially recommended to use and a warning will be displayed if this SKSE plugin is not installed.
* Ingame sanity checks for playable races are removed. These checks are now done only by the Reqtificator.
* The Reqtificator produces only a single log file to simplify troubleshooting.
* The previously deprecated `Reqtificator.jar` is removed from the distribution. The new Reqtificator setup introduced in 3.3.0 is now the only supported installation method.
* Ingame error messages for setup problems are more informative.

Compatibility and Modding Support
---------------------------------

* External configuration for the Reqtificator allows to disable the warnings the Reqtificator shows during installations like too many mods or missing SKSE plugins.
* The Reqtificator supports final armor ratings in plugins. Any armor that has an armor rating exceeding the type-specific threshold value will not have its armor rating rescaled. All armors in `Requiem.esp` have their final armor ratings assigned. The threshold values can be adjusted via external configuration.
* `REQ_AlreadyReqtified` is replaced by new keywords that provide the same features, but with more fine-grained control and clearly communicated effects. See [https://requiem.atlassian.net/wiki/spaces/MD/pages/1208844376/Keyword+Reference#Altering-the-Reqtificator%E2%80%99s-behavior-on-a-per-record-level](https://requiem.atlassian.net/wiki/spaces/MD/pages/1208844376/Keyword+Reference#Altering-the-Reqtificator%E2%80%99s-behavior-on-a-per-record-level) for details.
* Player-exclusive perks are distributed via Reqtificator rules instead of a script in the core of Requiem. This allows mod authors to easily extend Requiem's mechanism to distribute player-specific perks and spells without having to create custom quests or use conflict-prone edits on the player record.
* Armor material keywords are transformed into armor set keywords. Every piece of armor is part of exactly one set.
* Each smithing perk uses a single keyword to govern tempering bonuses. The Reqtificator distributes these keywords based on armor set keywords. In cases where the rules defined by Requiem don't yield the desired outcome, the tempering keyword can be manually added to a record to bypass a rule for this particular record.

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* The Reqtificator can provide a detailed output of the operations applied to each record to help debugging issues during development. This option can be enabled in the "Other Settings" tab of the Reqtificator main menu.
* ActorVariations are restructured to use appropriate vanilla records and no longer ship any FaceGeom data.
* Spells are directly distributed via Reqtificator rules instead of being piggybacked on perks.
* The Requiem Core Scripts suite is distributed over multiple quests to improve its maintainability.
* The `REQ_SpecialEquipment` script is removed. All instances are replaced by the more generic `REQ_AddItems`.
* FormID of Potion of Cure Poison and Potion of Resist Poison is swapped so that Potion of Cure Poison maps to its vanilla FormID.
* Bandit loot uses compact leveled lists.
* The weapons of Vigilants of Stendarr are inside leveled lists so that other mods can easily add new weapons to them.
* Healing Poultices and the recipe are moved to a separate leveled list that is directly added to merchant containers.
* `LItemPotionCureHMS` is no longer used by Alchemists and `LItemApothecaryPotionCureHMS75` contains only cures.
* Armor variants have a template if possible.
* Unused lesser leveled versions of Nightingale Armor, Shield of Solitude and Amulet of Articulation are properly nullified.
* The non-playable version of the Worn Shrouded Armor is non-playable again. The armor is not worn by any NPC in Requiem.
* Targe of the Blooded perk has different priorities to prevent the CK from shuffling the perk entries.
* Various improvement to the bandit template system.
    * Look templates inherit proper gameplay data in case they are still used somewhere.
    * Gameplay templates don't have fancy names like "Bandit - Axe & Shield - Level 24" so that their names can be propagated through the chain of inheritance.
    * Template flags are added whenever possible.
    * Bandits without the Use AI Data flag have proper AI data.
    * Bandits without the Use Def Pack List flag have `DefaultMasterPackageList` as default package list.
    * Use Script flag is removed if the template is a LVLN record because they can't inherit scripts anyway.
    * Bandits use the vanilla LChar records as templates instead of Requiem's new LVLN records. The LChar records then point to Requiem's new LVLN records.
    * Vanilla wizard bandits always spawn as trickster.
    * Vanilla missile bandits always spawn as marksman or crossbowman.
    * Voice spawns contain the same races as in the base game.
    * Some edits to Hajvarr Iron-Hand are reverted because the (hidden) quest already takes care of his name and respawning.
* EditorID of all bandits are renamed to clarify their usages.
* EditorID of all armors are renamed to clarify their usages.
* EditorID of version stamp global variables are renamed to clarify their usages.
* `KW` prefix is removed from all keywords.
* EditorID of all records that are used or distributed by the Reqtificator start with "RFTI_".
* Variable names in `ActorAssignmentRules_Requiem.esp.conf` are renamed to match the EditorID of the corresponding perk or spell.
* Various now unused assets are removed from the distribution:
    * Hedge Knight
    * Imperial Hero Armor
    * Redguard Knight Armor
    * Skyrim Knights
    * Third Era Weapons (except battlestaves)
* Armor addon and enchanted armor records that are de facto ITM records are deleted from the plugin.
* Greybeard Robes added by Requiem are deleted in favor of the ones from the base game.
* Empty leveled list that used to contain silver oil is deleted.
* Dragonborn DLC tempering recipes that don't really belong into `Requiem.esp` are deleted from the plugin.
* Redundant guard officer outfits are deleted.
* Deprecated records are deleted.
