Requiem 5.4.5 - "Towers and Shadows" Bugfix Pack #5
===================================================

Tweaks
------

* All breakdown recipes are standardized to yield half of the item's main component.
* Most armors and weapons can be broken down at a tanning rack or smelter.
* Tempering Forsworn weapons requires leather strips.
* Guards may comment on the armors and weapons added by Requiem.
* All armors added by Requiem have proper warmth keywords.
* Warmth rating of armors is forwarded from [Requiem - WACCF CCOR ACE Patches](https://www.nexusmods.com/skyrimspecialedition/mods/31758)
* Wolf armor has a more reasonable price.
* Generic arrow leveled lists do not spawn enchanted iron and steel arrows.

Bug Fixes
---------

* Non-power attacks do not break wooden bows.
* Health bonus in werewolf form is restored.
* Arrows damage stamina and magicka on hit.
* Perfected Art has no effect when the player rather than the target is wearing heavy armor.
* Extra shock damage to construct is resisted shock resistance. 
* Weapons enchanted with Turn Undead have the same naming convention as other enchanted items.
* Imperial Dagger, Mace, and Shortsword can be tempered.
* Stamina Damage enchantment is not considered a Destruction effect.
* Stalhrim Light Gauntlets do not receive extra unarmed damage from Combat Training.
* Tantos have the correct equip sound.
* Elven Greatsword has the correct Block Bash Impact Data Set.
* Bonemold, Chitin, and Stalhrim shield have the correct Bash Impact Data Set.
* Description of Scroll of Soul Gem Evocation ends with a period.

Reqtificator
------------

* Load order for GOG is automatically located. If both the Steam and GOG release are installed, GOG is prefered by default. This behavior can be overriden by the command line argument `--game=SkyrimSE` or `--game=SkyrimSEGog` respectively.

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* Existing tempering recipes from the Unofficial Patch are prefered over Requiem's where applicable.
* All shields have the armor type keyword assigned in the plugin.
* Enchanted steel weapons have a proper EditorID.
* Heavy Falmer armor does not have a redundant "Heavy" in its EditorID.
* Ingredients do not have the VendorItemAnimalPart keyword.
* Unused keyword REQ_Ench_RestoreHealthKeyword is deprecated.


Requiem 5.4.4 - "Towers and Shadows" Bugfix Pack #4
===================================================

Reqtificator
------------

* A ignorable warning is displayed if the settings file from Scrambled Bugs could not be verified.
* No warning is displayed if the "Soul Gems: Black" patch from Scrambled Bugs is disabled because it is not a significant patch.
* Load order and SKSE plugins are verified independently.
* The displayed link to Dual Casting Fix is correct.


Requiem 5.4.3 - "Towers and Shadows" Bugfix Pack #3
===================================================

New Features
------------

* The GOG release is supported.

Tweaks
------

* Angarvunde and Hillgrund's Tomb have more balanced loot. The stalhrim armors are moved to two more difficult locations instead.
* Training cost thresholds are at skill 25/50/75 (was unintentionally changed to 50/75/90, used to be 35/65/90). Due to an engine limitation Expert and Master trainers are both labeled "Rigorous" as a result.
* Crafting arrows and bolts requires firewood again. While many arrows appear to have a metal shaft, this raises serious questions regarding their functionality, and this is inconsistent with bolts which all have wooden shafts (except glass bolts). Therefore, it is more prudent to treat the models as a visual eccentricity and assume all shafts are made of wood.
* Dwarven Helmet of Arkngthamz does not grant Nightvision.
* Shock enchantments are consolidated into a single rank that deals triple damage to constructs.
* Damage of Elemental Fury is increased by 20%.
* Fire and frost enchantments apply damage spread over a second.

Bug Fixes
---------

* NPCs added by Requiem have a persistent location. Among other things, this should fix NPCs randomly disappearing until restarting the game.
* Synchronize with Unofficial Skyrim Special Edition Patch v4.2.9a.
* Scrambled Bugs is added as a highly recommended mod. Among other things, this fixes the following Requiem bugs:
    * Poison applied by weapon attacks can be blocked with a shield or ward.
    * Worn armor weight reduction only applies to the armor you are wearing instead of all similar armors.
    * Power attacks still deal full damage when stamina is low as a result of the current power attack.
* Fire/Frost/Shock Mastery affect Destruction spells instead of all other spells.
* Spells can paralyze again.
* Calm has non-zero magnitude.
* Fire breath is always resisted by fire resistance.
* Arcane Experimentation and Artificer's Insight requires at least three soul gems in your inventory.
* Creating enchantments through Arcane Experimentation and Artificer's Insight grants XP.
* Exploding stalhrim arrows can be crafted.
* Generic arrow leveled lists consistently have a 30% chance to spawn enchanted arrows.
* Haldyn's Quicksilver Helmet has the correct ground model.
* Blacksmiths do not sell chaos enchantments.
* Enchanted chitin shields do not have the armor type in their name.
* All enchantments are explicitly set to either increase in magnitude or duration.
* All detrimental magic effects are explicitly set to be resisted in either magnitude or duration.
* Frost enchantments always slow the player.
* The speed reduction from frost enchantments depends on the magnitude of the enchantment.
* Description of Banish Daedra and Turn Undead is rephrased to clarify the additional damage is reduced by magic resistance.
* The "Script initialization failed" error message mentions the possibility of outdated Requiem addons or patches.

Reqtificator
------------

* The masterlist is sorted with respect to the load order. Among other things, this fixes an infinite loading screen if the mod "Finding Helgi... and Laelette" is installed.
* Container ownership data is preserved. Among other things, this fixes a rare error with DynDOLOD.
* Merged leveled lists are truncated at 255 entries to avoid the "Arithmetic operation resulted in an overflow" error.
* No HITME records are generated.
* An explicit error message is displayed when the current working directory is incorrect.
* The "Too many masters" error message is rephrased to make its implications clearer, and all required masters are written to the log file.
* The "Could not find Requiem.esp" error message is rephrased to make its cause clearer.
* A warning is displayed if the following SKSE plugins are missing:
    * Dual Casting Fix
    * Scrambled Bugs
    * Script Effect Archetype Crash Fix (Scrambled Bugs)
    * Vendor Respawn Fix (Scrambled Bugs)
* A warning is displayed if the following patches from Scrambled Bugs are disabled:
    * Perk Entry Points: Apply Multiple Spells
    * Power Attack Stamina
    * Soul Gems: Black

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* Enchantments with multiple effects are refactored to support Scrambled Bugs.
* Apply spell perk entry points are refactored to take advantage of Scrambled Bugs.
* Fire enchantments apply lingering damage through tapering.
* Ring of the Erudite uses common magic effects.
* Various unused records are nullified.


Requiem 5.4.2 - "Towers and Shadows" Bugfix Pack #2
===================================================

Tweaks
------

* Quarterstaff Focus increases armor penetration by 12% per rank (was 8%).
* Strength of lockpicking enchantments is doubled.
* Dispelling active magic effects with Spellbreaking has a 10 second cooldown.
* Power attack stamina cost is increased by 25% of the weapon's weight to compensate for the previous removal of weapon weight from the Mass Effect.
* Alchemists sell potions of fortify health/magicka/stamina and fortify health/magicka/stamina regeneration.
* Quicksilver weapons have the same damage as Dwarven weapons.
* Stalhrim armors and weapons require steel as secondary component.
* Transmute Corpus can kill targets with more than 1200 health.
* Telekinetic Execution can kill Dwarven Centurions.
* Transcendence has a finite duration.
* Dispelling Candlelight costs no magicka.
* Illumination lasts for 5 minutes (was 10).
* Synchronize with Even Better Quest Objectives 1.9.2.

Bug Fixes
---------

* A New Order does not fail to start if too many locations have been cleared. (This is an old Skyrim bug and was not caused by Requiem.)
* Essential and protected NPCs are excluded from the yield system to work around an engine limitation that could automatically restore them to full health.
* Ancient Knowledge grants enough XP to reach Smithing skill 14 to work around an engine limitation that disables tempering with Smithing below 14.
* Cold effects modify lockpick durability instead of lockpicking expertise. This fix is not retroactive unless you disable Survival Mode _before_ updating.
* Treasure Hunter does not require Sophisticated Lockpicking.
* Khajiit caravans, Enthir and Mallus still sell their normal wares after they become fences.
* Dispel Soul Gems works are advertised.
* Mage Armor on Self (Rank V) increases armor rating by the intended amount.
* Spectral Arrow is irresistible.
* Dispel on Target does not dispel active magic effects from the caster.
* Absorb spells grant XP.
* Transmute Muscles grants XP.
* Dunmer receive 3% more Alteration, Evasion, Illusion, and Sneak XP instead of Destruction and One-Handed a second time.
* Horse saddles provide the expected armor rating.
* Health bar of werewolves correctly updates when they lose health.
* Invisible foxes in Labyrinthian are restored as skeletons.
* The hood of skeleton mages does not stretch upon death.
* The original template structure of random dragon priests is restored due to unexpected interactions with other mods.
* Ore veins in Redbelly Mine and Northwind Mine are reverted to their original type. The ore sample from the related quest is updated accordingly.
* Spriggans drop both Taproot and Spriggan Sap at the same time.
* Silver battleaxe, dagger, mace, quarterstaff, shortsword, war axe, and warhammer can be crafted.
* Akaviri-styled weapons can be crafted at the Atronach Forge.
* NPCs who spawn with tempered armors have the tempering bonuses recalculated on every cell load to account for outfit changes.
* Agent of Dibella only affects people.
* Elemental silver arrows use the correct model.
* Various typos in magic effect descriptions are fixed.
* Elsi the Spiker has Quarterstaff Focus instead Warhammer Focus.
* Stalhrim bow and arrow have proper stats.
* Quicksilver quarterstaff and shortsword are sold by blacksmiths.
* Forsworn and hagravens have a chance to drop black soul gems regardless of the player's level.
* Ranged Combat Training and Marksman's Focus do not reference weight penalty reduction (no such feature exists anymore).
* Arcane Ward spells do not reference spell reflection (no such feature exists).
* Dark Vision can be cast with one hand.
* Damage from firewalls on the ground is applied each second.
* Mistress of the Dark can be resisted.
* Duration of Sunfire Cloak is increased by Restoration perks.
* Dualcasting Detect Aura outside increases duration.
* Dualcasting Featherfalling increases duration.
* Imperial Luck is properly disabled.
* Trainers are correctly labelled (Common, Expert, Master).
* Dualcasting Arcane Resonance increases duration instead of magnitude because this actually works.
* Cremation does not affect dragons.
* General Tullius' Sword states the correct magnitude.
* Hidden Illusion spells cannot be absorbed.
* Description of Mark & Recall is rephrased to make its behavior clearer.
* Description of Fireshock and Iceshock is rephrased to clarify the spell applies instantaneously without firing a projectile.
* Description of Soulreaping is rephrased to clarify that the enchantment only works when wielded by a denizen of the Soul Cairn.
* Leadership fortifies Illusion instead of Conjuration twice.
* Hibernation Spray can be resisted.
* Dremora can be disarmed by the voice.
* Bandits are neutral towards prey.
* Forsworn are friendly towards chickens.
* Charmed Necklace has a less immersive but correctly spelled description.
* Dagger Focus I states the correct sneak attack bonus.
* Mistress of the Dark and Shadow Simulacrum do not increase health and Illusion of nearby summons.
* Inoperative Stamina Drain Reset is removed from the MCM.
* Rapidly activating shrines does not grant the blessing multiple times.
* Perfected Art has no effect when wearing heavy armor.
* Pickpocket and Sneak perks that have no effect when wearing heavy armor include heavy shields.
* Power attack stamina cost penalty when wearing heavy armor without Combat Training includes heavy shields.
* Sanguine's Rose has no effect when holding a shield.
* Nightingale Sentinels drop ectoplasm instead of arrows.
* Froki's Bow has proper stats.
* Firiniel's End has proper stats.
* A leveled list related to bandits does not unexpectedly spawn Nordic arrows.
* Destruction Mastery perks only affect Destruction spells.
* Dremora Warlocks do not known Summon Dremora Archer because they cannot cast the spell.
* Summon Skeletal Hero can be selected when taking Apprentice Conjuration.
* Summon Spectral Warrior can be selected when taking Adept Conjuration.
* Leftover faction edit from Minor Arcana to Valdr is removed.
* Leftover navmesh edit from Minor Arcana in Volunruud is removed.
* The easter egg in Helgen has the correct inventory model.
* All notes have no weight and price.
* All notes use a handwritten font.
* Disarm shout of dragons can be resisted.
* Pickpocketing Brand-Shei during A Chance Arrangement uses the standard pickpocketing formula.
* The merchant chest in Understone Keep does not block a bed roll.
* Bolgeir Bearclaw does not carry a duplicate sword.
* Vigilant Tyranus does not carry a duplicate amulet.
* Ulfric always carries his shield.
* Rockjoint reduces damage instead of armor penetration.
* Vampire armor cannot be sold as clothing.
* Targe of the Blooded cannot be enchanted.
* Targe of the Blooded has a more logical description.
* Aetherial Crown is considered jewelry.
* Frost breath used by high-level dragons slows the player by the intended amount.
* Telekinetic Execution cannot kill ghosts and the skeletal dragon.
* Telekinetic Execution, Teleport Vitals, Phantasmal Killer, and feeding as a vampire cannot kill essential actors if the MCM option to make Essential Actors invincible is enabled.
* Transmute Corpus can kill unique or essentials actors.
* Transmute Corpus does not damage immune targets.
* Summons are immune to Transmute Corpus.
* Ghosts are immune to Teleport Vitals.
* Clairvoyance (Rank II) is renamed to Vision of the Tenth Eye because the journal refers to it as such.
* Level 10 axe-wielding bandits do not have Experienced Blocking because it exceeds both their perk count and Block skill.
* Level 12 and higher mace-wielding bandits have Powerful Bashes.
* Soul Trap has no rank.
* The random encounter where a thief attempts to pick a locked door and is discovered by guards is not tied to the player's level and Thieves Guild membership.
* Mzulft Storeroom Key unlocks the gate in the Dwarven Storeroom.
* Keys have no weight and price.
* Keys have pick up and put down sounds.
* Keys have proper names.
* Duplicate key to House Battle-Born is removed.
* Gallow's Rock Key, Key to Shriekwind Bastion's Safe, and Labyrinthian Chasm Key use a standard model.
* Armored trolls have the same stamina as unarmored trolls.
* Frost trolls have correctly flagged power attacks.
* Spectral draugr have correctly flagged power attacks.
* Trolls have damage multiplier 1.5 for power attacks (was 1.0).
* Elder vampires have damage multiplier 2.0 for power attacks (was 2.5 or 1.0).
* Elder vampires cause stagger with all power attacks.
* Elder vampires have attack chance 0.2 for attackPowerStartBackward (was 0.1).
* The Afflicted have damage multiplier 2.0 for power attacks (was 1.5).
* Bretons and Imperial vampires have stagger 1.0 for attackPowerStartDualWield (was 0).
* Dunmer, Dunmer vampires, Bosmer vampires, and the Afflicted have strike angle 50 for attackStart (was 35).
* Bosmer have stagger 1.0 for attackPowerStartForwardLeftHand (was 0.5).
* Bosmer, Breton vampires, and Bosmer vampires have attack chance 0.2 for attackPowerStartInPlace (was 0.5).
* Bosmer have stagger 1.0 for attackPowerStart_SprintLeftHand (was 0).
* Nord vampires have stagger 0.5 and no recovery time for attackPowerStartForwardH2HLeftHand (was 0.2)
* Argonian vampires have damage multiplier 2 for attackPowerStartForwardH2HRightHand (was 5).
* Dunmer vampires and the Afflicted have stagger 1.0 for attackPowerStartForwardLeftHand (was 2.0).
* Imperial vampires have no recovery time for attackPowerStartDualWield.
* Bosmer vampires have stagger 1.0 for attackPowerStartInPlaceLeftHand, attackPowerStartLeftLeftHand, and attackPowerStartRightLeftHand (was 0.5).
* Bosmer vampires have strike angle 35 for attackPowerStartLeft (was 50).
* Astrid has stagger 1.0 for attackPowerStartBackLeftHand (was 0).
* Astrid has damage multiplier 2.0 and stagger 1.0 for attackPowerStartForwardLeftHand (was 1.0 and 0.0)
* Astrid, elders, elder vampires, and the Afflicted have damage multiplier 1.5, stagger 1.0, and stamina multiplier 0.5 for attackPowerStartDualWield (was 1.0, 0.0, and 1.0).
* Astrid, elders, elder vampires, and the Afflicted do not cause stagger with bashes.
* Astrid, elders, elder vampires, and the Afflicted can make use of power bash perks.
* Rusty Mace retains its original damage to ensure the associated scene plays out as expected.
* Draugr do not carry duplicate shields.
* Kyne's Peace is not affected by Improved Healing.
* Duplicate loading screen regarding heavy armor is disabled.
* Enchanted items of Fire Abatement increase fire resistance by 40% (was 45%).
* The Longhammer cannot be enchanted.
* Auriel's Bow and Shield are not considered Daedric artifacts.
* Auriel's Bow and Shield can be sold to merchants who deal in weapons and armors.
* Giant Clubs are marked as warhammer instead of swords.
* Nahkriin's Staff has a unique name.
* Blade of Markarth is renamed to Blade of the Reach.
* Notched Pickaxe is not a war axe.
* Talsgar the Wanderer carries a quicksilver crossbow instead of an improved Dawnguard crossbow.
* Alchemical Intellect does not affect spells that are not supposed to have their magnitude or duration increased.
* Spells with a constant duration are not affected by perks that increase duration.
* Duration of Summon Swarm is not increased by Conjuration perks.
* Duration of Summon Spectral Arrow is not increased by Conjuration perks.
* Duration of Teleport Vitals is not increased by Conjuration perks.
* Dualcasting Circle of Protection does not increase magnitude.
* Price of gourd is not auto-calculated.
* Wuuthrad displays the correct damage when used by an elf.
* Spectral summons have the same audio template as spectral draugr.
* Spectral Warrior has auto-calculated stats.
* Boethiah's Embrace states the correct magnitude.
* Magelight is not disabled by Stability.
* Soulreaping only deals magic damage.
* The effects of Fortitude are not displayed in the active magic effects menu.
* Soul gem fragments cannot be sold as gems.
* Falion and Enthir (prior to becoming a fence) do not have a black soul gem in their merchant inventory because they cannot sell it.
* Lucan does not deal in black market goods.
* Restore missing property in QF_DA16_000242AF.
* Mistress of the Dark has a functional casting animation.
* Mistress of the Dark is not considered a spirit summon.
* Magical Absorption is not affected by perks that increase the magnitude of Alteration spells.
* Elgrim's Elixirs stocks the normal amount of potions and poisons.
* The Aetherium Forge does not grant Smithing XP.
* Lunar weapons cannot be disenchanted.
* Troublesome Papyrus script swapping outfits of Imperial guards is removed.
* Herbalist's Guide to Skyrim is revised to take the removal of Fortify Enchanting into account.
* Bonemold Cuirass of the Squire is renamed to Bonemold Cuirass of the Knight to match its armor type.
* Spriggan Matron has more magicka to account for her much higher spell cost.
* Spriggan Earth Mother has better stats than the Spriggan Matron.
* Ammo projectiles have the same name as the ammo.
* Description of enchanted ammo states the magnitude without appending "points of damage".
* Silver arrows do not have a special description that points out that they are made of silver.
* Silver ammo has its weight explicitly defined.
* Aela does not fire exploding arrows at training dummies.
* J'zargo's Flame Cloak has the same behavior as a flame cloak except for its twist.
* NPC-exclusive wards have the correct name.
* Summon Flaming Familiar can be dualcast.
* Spells that would not benefit from dualcasting cannot be dualcasted.
* Secondary magic effects are assigned their respective school of magic.
* All spells are explicitly set to either increase in magnitude or duration when dualcast.

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* Magic effects used by standard spells have improved names and EditorIDs.
* Spells have improved EditorIDs.
* Notes have improved EditorIDs.
* Ammo projectiles have improved EditorIDs.
* Potions and poison do not have a redundant 0 in their EditorID.
* Several hidden ITMs are cleaned.


Requiem 5.4.1 - "Towers and Shadows" Bugfix Pack #1
===================================================

Tweaks
------

* Spirit Steed does not wear a saddle.

Bug Fixes
---------

* Helgen horses move at a slower pace so that they arrive in Helgen at the expected point in time.
* NULL references in the Creation Club patch are resolved.
* Quicksilver bow and arrow have proper stats.
* Exploding quicksilver and skyforge steel arrows can be crafted.
* Skyforge steel arrows are correctly named.


Requiem 5.4.0 - "Towers and Shadows"
====================================

New Features
------------

* Quicksilver crossbow is available using assets from [Carved Nordic Crossbow](https://www.nexusmods.com/skyrim/mods/48847). It can be crafted with the respective material perk, and it is integrated into the world through loot and merchants.
* Skyforge Steel Mace, Warhammer, Bow, and Arrow are available. They are integrated in the same manner as the other Skyforge Steel weapons.
* All crossbows use the models from Elite Crossbows and Expanded Crossbow Pack DLCs if the Creation Club patch is installed.
* The Horse Armor DLCs are integrated.
    * Elven horse armor has 400 armor rating, weighs 20 units, and increases health by 400.
    * Steel horse armor has 800 armor rating, weighs 40 units, and increases health by 800.
    * Horse armor does not make the horse essential.

Tweaks
------

* Lockpicking Expertise is rescaled to range from 0 to 10.
    * The odd values 1/3/5/7/9 unlock Novice/Apprentice/Adept/Expert/Master locks.
    * The even values 2/4/6/8/10 make the correct spot of Novice/Apprentice/Adept/Expert/Master locks 8 times larger. This bonus stacks additively, e.g., 4 Lockpicking Expertise makes the correct spot of Novice locks 16 times larger.
* Locksmithing Lore is split into two new perks unlocked at skill 30 and 60 respectively.
* Lockpicking enchantments increase lockpick durability instead of Lockpicking Expertise.
* Lockpicking skill does not increase the sweetspot or lockpick durability. All skill buffs are removed.
* Khajiit can pick Novice locks without perks, but they do not start with additional Lockpicking Expertise.
* Khajiit caravans and Thieves Guild fences always stock a decent supply of thief potions.
* Lockpicking expertise is explained in the help menu.
* All edits to horse NPCs including the armors are removed, leaving this area of the game to other mods. Movement speed changes are reimplemented as movement types.
* Most text improvements from [Requiem - Alternative Descriptions](https://www.nexusmods.com/skyrimspecialedition/mods/33663) are forwarded (but not the consolidated descriptions).
* Descriptions of food, potions, enchantments, and abilities are reworded to full sentences that provide a bit more variance and flavor then "Increases A by X".
* Wooden Crossbow is renamed to Imperial Crossbow.

Bug Fixes
---------

* Horses have manes again.
* Ataxia contracted from traps has the same effect as normal Ataxia.
* The Tower Stone does not allow you to pick Apprentice locks unnoticed in plain sight if you do not have the required Lockpicking Expertise to unlock Apprentice locks.
* Papyrus error when picking a lock without followers is fixed.


Requiem 5.3.1 - "Around the Fire" Bugfix Pack #1
==================================================

Tweaks
------

* Unique rings are removed from (one-off) fishing junk drops and placed in different locations guarded by an appropriately challenging encounter. The vacant drops are replaced with other interesting items.
* Ring of the Khajiit increases movement speed by 15% while sneaking (was 10% at all times).
* Ring of the Wind increases movement speed by 10% (was 15%).
* Quarterstaff Focus increases armor penetration by 8% per rank (was 7%).
* Damage of quarterstaffs is increased by 3 and stagger by 0.15.
* Weight of battlestaffs is decreased by 5.

Bugfixes
--------

* Ulfric's armor has its enchantments back.
* The abilities distributed by the Reqtificator to all NPCs are not dispeled on death. Among other things, this fixes reanimated NPCs sometimes having very low attack speed.
* War Axe Focus 1 and Battleaxe Focus 1 increase damage by 10% (was 5%) to match the next two ranks.
* Greatsword Focus increases attack speed by 8% per rank (was 10%) to match its one-handed counterpart.
* Devastating Strike increases power attack damage by 20% (was 25%) to match its one-handed counterpart.
* Sword Focus 2 does not increase armor penetration by more than intended.
* Quarterstaffs are not affected by Warhammer Focus.
* Battlestaffs are affected by Quarterstaff Focus instead of Warhammer Focus.
* Tantos have the same weight as daggers.
* All weapons have a critical damage multiplier of 1.
* Telekinetic Nova always knocks down vulnerable targets.
* Strong boxes respawn when the cell resets.
* Halvar's Boots have the correct model.
* Skaal armor is considered warm by Surival Mode.
* Ancient Nord Armor of the Tongue cannot be disenchanted.
* Material bonus of chainmail armor is correctly named "Slash Protection".
* Enchanted Imperial armors have proper names.
* Power of the Combatant matches the perk description.


Requiem 5.3.0 - "Around the Fire"
=================================

New Features
------------

* An optional patch that integrates the following Creation Club DLCs is added. The vanilla integration quests are fully disabled.
    * Alternative Armors - Daedric Mail
    * Alternative Armors - Daedric Plate
    * Alternative Armors - Dragon Plate
    * Alternative Armors - Dragonscale
    * Alternative Armors - Dwarven Mail
    * Alternative Armors - Dwarven Plate
    * Alternative Armors - Ebony Plate
    * Alternative Armors - Elven Hunter
    * Alternative Armors - Iron
    * Alternative Armors - Leather
    * Alternative Armors - Orcish Plate
    * Alternative Armors - Orcish Scaled
    * Alternative Armors - Silver
    * Alternative Armors - Stalhrim Fur
    * Alternative Armors - Steel Soldier
    * Netch Leather Armor
    * Redguard Elite Armaments
    * Spell Knight Armor
    * Vigil Enforcer Armor Set
* Basic support for Survival Mode is added. Food poisoning, advanced diseases, and the initial health and carry weight penalties are disabled in favor of Requiem's existing systems. All other Survival Mode features work as designed. However, Survival Mode has not been rebalanced and should only be enabled at the user's discretion.
* Fishing is integrated.
* All items from Rare Curios are removed.
* New weapon types are available using assets from [Heavy Armory](https://www.nexusmods.com/skyrimspecialedition/mods/6308) and [Katana Crafting](https://www.nexusmods.com/skyrimspecialedition/mods/5306). They can be crafted with the respective material perk, and they are integrated into the world through loot and merchants.
    * Tanto
    * Wakizashi
    * Katana
    * Dai-Katana
    * Shortsword
    * Quarterstaff
    * Club (Wood, Iron, Steel)
* New weapons are available using assets from [Heavy Armory](https://www.nexusmods.com/skyrimspecialedition/mods/6308). They are integrated in the same manner as the other weapons from the same set.
    * Silver: Dagger, War Axe, Mace, Battleaxe, Warhammer
    * Dawnguard: Mace, Battleaxe, Quarterstaff
    * Forsworn: Dagger
* Nordic weapons (from the Dragonborn DLC) can be crafted with Advanced Blacksmithing, and they are integrated into the mainland through loot and merchants.
* New armor sets are available using assets from the Creation Club and [Chainmail Armor](https://www.nexusmods.com/skyrimspecialedition/mods/27340). They can be crafted with the respective material perk, and they are integrated into the world through loot and merchants. If you do not use the Creation Club patch, the armors will still be present but share their appearance with a similar vanilla model.
    * Chainmail
    * Dwarven Light
    * Orcish Light
    * Netch Leather
* Various NPCs have new armors using assets from the Creation Club. If you do not use the Creation Club patch, the armors will still be present but share their appearance with a similar vanilla model.
    * Aicantar
    * Alain Dufont
    * Galmar Stone-Fist
    * Haldyn
    * Irileth
    * Jarl Korir
    * Keeper Carcette
    * Kematu and his entourage
    * Orcish chieftains
    * The Blood Horkers
    * The Dremora Merchant
    * The Halvar brothers
    * The Silver Hand
    * The Summerset Shadows
    * The Vigilants of Stendarr (The previous Vigilant assets from [Mage's Plate Armor](https://www.nexusmods.com/skyrim/mods/92916) are removed)
    * Ulfric
    * Velehk Sain
    * Vigilant Tyranus
* New perks are available.
    * Quarterstaff Focus 1: +10% attack speed, +7% armor penetration
    * Quarterstaff Focus 2: +20% attack speed, +14% armor penetration
    * Quarterstaff Focus 3: +30% attack speed, +21% armor penetration

Tweaks
------

* Armor material bonuses are rebalanced. (Magnitude is denoted per piece of light/heavy armor.)
    * Ancient Falmer, Daedric: +6/9% magic resistance
    * Chaurus Chitin: +10/15% poison resistance
    * Chitin: 6/9% less damage from ranged weapons
    * Corundum: 8/12% less damage from slashing weapons
    * Dawnguard: 14/21% less damage from Vampiric Drain, 7/10.5% less damage from physical attacks by vampires
    * Dragonscale, Dragonplate: 6/9% shorter shout cooldown, 6/9% less damage from the voice, immunity to Unrelenting Force at three pieces
    * Dwarven Metal: 4/6% less damage from blunt weapons
    * Glass, Ebony: 6/9% fire resistance
    * Imperial Legate: Nearby Imperial soldiers deal +4% damage
    * Netch Leather, Akaviri: +2/3% movement speed
    * Quicksilver: 2/3% more spellpower
    * Stalhrim: +6/9% frost resistance
    * Stormcloak Officer: Nearby Stormcloak soldiers deal +4% damage
    * Vigilant: 12% less damage from daedra
    * Volkihar Leather: +20% more damage from Vampiric Drain
* Aetherium armor is removed. In its place Dwarven armor with the same enchantments can be found.
* Redguard clothes are reverted to clothing.
* Ulfric's clothes are reverted to clothing.
* Reinforced chitin armor is reverted to heavy armor.
* Armor chest pieces are called cuirass instead of armor.
* Blades items are renamed to Akaviri.
* Nordic items (from the Dragonborn DLC) are renamed to Quicksilver.
* Weapon progression is rearranged so that Quicksilver < Dwarven < Orcish.
* Heavy armor progression is rearranged so that Dwarven < Orcish, but Orcish armor no longer provides a unique material bonus.
* Light armor progression is rearranged so that Scale < Elven.
* Penitus Oculatus armor has less inflated armor rating and weight but gains the same extra protection against ranged attacks as faction leader light armors.
* Morag Tong armor has the same armor rating and weight as chitin light armor.
* The steel scimitar has the same damage as a steel sword.
* Dawnguard weapons deal 25% more damage to vampires (on top of the extra damage from silver) but they have the same base damage and weight as normal silver weapons.
* Kematu's scimitar increases one-handed armor penetration by 10.
* The reward for retrieving Hrolfdir's Shield is an enchanted set of Dwarven armor.
* Vigilants of Stendarr use iron weapons and shields to match their new iron armor.
* Several encounters have proper stats, perks, and equipment.
    * Blood Horkers
    * Butcher
    * Champion of Boethiah
    * Ghunzul
    * Haldyn
    * Linwe
    * The band of Thirsk Hall
    * Unmid Snow-Shod
    * Velehk Sain
* The armory chest in Sky Haven Temple contains three sets of unenchanted Akaviri armor and weapons and no further loot.
* The enchanted set of Akaviri armor in Sky Haven Temple is enabled only after completing The Throat of the World.
* Non-Akaviri armors and weapons are removed from Sky Haven Temple.
* Gunmar sells Dawnguard weapons.
* Torkild wears a full set of fur armor.
* Falmer only use Falmer arrows.
* Names of base enchantments are reverted to the more concise vanilla names.
* Rested bonuses display the actual effect.
* The summoned Dremora archer uses normal daedric arrows instead of a heavily nerfed variant.
* Artifact name changes:
    * Dawnguard Rune Axe -> Ancient Dawnguard War Axe
    * Dawnguard Rune Hammer -> Ancient Dawnguard Warhammer
    * Dawnguard Rune Shield -> Ancient Dawnguard Shield
    * Dragon Priest Staff -> Rahgot's Staff
    * The Gauldur Amulet -> Gauldur Amulet
    * The Rueful Axe -> Rueful Axe
* Artifact gold values are standardized according to the following formula.
    * Divine artifacts have a value of 80000.
    * Legendary historic artifacts have a value of 50000.
    * Mundane artifacts have a value of 5000 or less.
* All crafting recipes are standardized according to the following formula.
    * Gauntlets: 2 primary components, 1 secondary components, 2 leather strips
    * Boots: 3 primary components, 1 secondary components, 2 leather strips
    * Helmet: 3 primary components, 1 secondary components, 1 leather strip
    * Cuirass: 5 primary components, 2 secondary components, 4 leather strips
    * Shield: 4 primary components, 1 secondary components, 2 leather strips
    * Dagger: 1 primary components, 1 secondary components, 1 leather strip
    * Sword: 2 primary components, 1 secondary components, 1 leather strip
    * War Axe: 2 primary components, 1 secondary components, 2 leather strips
    * Mace: 3 primary components, 1 secondary components, 2 leather strips
    * Greatsword: 4 primary components, 1 secondary components, 2 leather strips
    * Battleaxe: 4 primary components, 1 secondary components, 3 leather strips
    * Warhammer: 4 primary components, 2 secondary components, 3 leather strips
    * Bow: 3 primary components, 1 secondary components, 2 leather strips
    * Crossbow: 3 primary components, 1 secondary components, 2 leather strips
    * Arrows (30): 1 primary components, 1 leather strip
* Crafting components changes:
    * Ancient Nord armor does not require leather.
    * Dragonscale armor requires iron instead of corundum.
    * Dwarven armor does not require steel.
    * Glass armor does not require leather.
    * Hide and Leather armor requires iron.
    * Imperial heavy armor requires iron instead of leather.
    * Improved crossbows require the same material as normal crossbows.
    * Nord hero weapons require corundum instead of an ancient Nord weapon.
    * Quicksilver items require quicksilver as primary component and iron as secondary component.
    * Scale armor does not require corundum.
    * Stalhrim heavy armor does not require quicksilver.
    * Stalhrim light armor does not require steel.
    * No items require firewood.
* Tempering components changes:
    * Ancient Nord armor requires steel instead of iron.
    * Blade of Woe require ebony instead of steel.
    * Dawnguard crossbow requires silver instead of steel.
    * Dragon Priest Dagger requires iron instead of Dwarven metal.
    * Dragonbane requires steel instead of quicksilver.
    * Forsworn weapons require leather instead of steel.
    * General Tullius' armor requires leather instead of steel.
    * Guard armor requires leather instead of iron.
    * Harkon's sword requires ebony instead of steel.
    * Iron armor with pauldrons requires iron instead of corrundum.
    * Ironhand Gauntlets requires steel instead of iron.
    * Kahvozein's Fang requires dragon bone instead of dwarven metal.
    * Nightingale armor requires leather instead of void salts.
    * Poacher's Axe requires requires iron instead of steel.
    * Rueful Axe requires silver instead of ebony.
    * Savior's Hide requires steel instead of leather.
    * Scale armor requires steel instead of corundum.
    * Stormcloak officer armor requires steel instead of leather.
    * Studded hide armor requires leather instead of iron.
    * Ulfric's armor requires steel instead of corundum.
* Tempering Nightingale armor and weapons and Harkon's sword requires Legendary Blacksmithing.
* Tempering dragonplate armor requires Ebony Smithing and tempering dragonscale armor requires Glass Smithing to match the crafting requirements.
* Tempering Thieves Guild and Dark Brotherhood armors does not require joining the guilds.
* Crafting and tempering bonemold, chitin, and netch leather armor requires Craftsmanship and visiting Solstheim after being attacked by Miraak's cultists.
* Battlestaves have a consistent stats progression.
* Weapon prices are always rounded to the nearest multiple of 5.
* Armor weight is not rounded.
* Textures are distributed in a compressed BSA to reduce storage size.

Bugfixes
--------

* Critical weapon damage is always half the base damage.
* Maces wielded by the Vigilants of Stendarr have the intended tempering bonus.
* Enchanted weapons have the correct price.
* Aela's Shield has the same weight as a hide shield.
* Blade of Woe, Ebony Blade, Keening, and the Rueful Axe have the correct stagger value.
* Eduj and Okin have the same weight and price as a Nord Hero weapon.
* Kahvozein's Fang has the same damage and weight as a dragonbone dagger.
* Red Eagle's Fury has the same stats except lower damage as Red Eagle's Bane.
* The longhammer has the same damage and price as an Orcish warhammer.
* Wuuthrad has the same weight as an ebony battleaxe.
* Duplicate tempering recipe for Headman's Axe and Pickaxe is removed.
* Forsworn do not wield Orcish war axes.
* Various fixes to leveled lists containing enchanted weapons.
* A rarely used leveled list containing Dwarven armors is deleveled.
* Leveled lists containing common weapons and armors are consistently weighted.
* A missing look template of Thalmor archers is restored.

Reqtificator
------------

* Critical damage is increased by the same factor as weapon damage. Consequently, the critical damage bonus from Powerful Charge and Devastating Charge is removed.
* Visual automerging takes template flags into account and correctly copies them into the generated patch.


Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* Ammo crafting recipes use vanilla records where they already exist.
* Ammo, animal hides, crafting recipes, enchanted weapons, first person weapon models, and weapon enchantments have a proper EditorID.
* Standard weapons have a common prefix "Weapon" in their EditorID.
* Weapon artifacts do not have the weapon type in their EditorID.
* EditorID of ancient Nord weapons matches their name.
* Various unused records are nullified.
* Duplicate Orc stronghold outfits are nullified.
* REQ_ArmorUnion_Chitin is deprecated. Morag Tong armor shares its armor set keyword with chitin light armor.
* Enchanted Dawnguard crossbows do not use the improved crossbow as base weapon. (The enchanted improved crossbows are currently unused.)
* Thalmor archers inherit their inventory from the template.
* Redundant edits to Skyhaven Temple are removed.
* LootDwarvenWeapon15 does not contain enchanted weapons.
* LItemDawnguardWarAxe is reverted to its vanilla functionality.
* csDraugrMissile is removed from the plugin because it's effectively an ITM.
* Imperial weapon meshes are moved from `Requiem\weapons\imperial\` to `PrvtI\imperial\`.


Requiem 5.2.3 - "The Gathering Storm" Bugfix Pack #3
====================================================

Tweaks
------

* Fortify Magicka II reduces damage from all absorb spells by 35% but no longer negates magicka loss on hit.
* The Gauldur Amulet negates magicka loss on hit.
* Stendarr's blessing reduces damage from all absorb spells by 75% (was 100%).
* Sunfire Cloak reduces damage from Vampiric Drain by 50%.
* The spectral weapons from Labyrinthian are playable.
* Falmer melee weapons do not have their reach adjusted by the Reqtificator because it interferes with their AI. As a result, Falmer weapons have a niche as the longest one-handed weapons.

Bugfixes
--------

* Material bonus of dragonplate and dragonscale armor does not reduce magnitude of self-targeted shouts.
* Material bonus of Dawnguard armor affects all variants of Vampiric Drain.
* The invulnerable wisps in the Soul Cairn are not attacked by other encounters.
* Skill-ups from skill books, quests, and others grant a full skill-up in most cases. The underlying cause of this bug remains unfixable.
* Destruction cloak spells are correctly resisted when using Scrambled Bugs. It is still recommended to disable the "Magic Effect Flags" fix from Scrambled Bugs because it reinterprets the meaning of magic effect flags in a manner that is not supported by Requiem (or even vanilla Skyrim).
* Loading screens and tooltips do not use an unsupported ellipsis character.
* The new game check is more robust in large load orders.
* All absorb effects are affected by absorb resistance.
* Undead are not immune to some of Harkon's absorb spells.
* Dwarven automatons are immune to all absorb effects.

Reqtificator
------------

* Weight, height, and death item changes do not suffice to classify a record as a visual template during visual automerging.

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* `REQ_SunDamage` is assigned to all sun magic effects.
* `REQ_NoLifeDrainAllowed` is only assigned to magic effects.
* All deprecated record have the prefix DEPRECATED instead of LEGACY in their EditorID.
* Several unused absorb effects are nullified.
* Unused entries are removed from `Reqtificator.conf`.


Requiem 5.2.2 - "The Gathering Storm" Bugfix Pack #2
====================================================

Bugfixes
--------

* Armor weight penalty reduction from perks does not reduce damage taken.
* Synchronize with Unofficial Skyrim Special Edition Patch v4.2.7. Earlier versions of the Unofficial Patch should remain compatible and the free Creation Club DLCs were not added as a dependency to `Requiem.esp`.


Requiem 5.2.1 - "The Gathering Storm" Bugfix Pack #1
====================================================

Bugfixes
--------

* Farkas doesn't drop his weapon in Dustman's Cairn.
* Werewolf abilities in human form don't apply in beast form.
* Companions quests forward the improved quest descriptions from Even Better Quest Objectives.
* Powerful Healing Aura doesn't have a persistent visual effect.
* Enchanted variants of Reinforced Chitin Bracers are renamed to Reinforced Chitin Gauntlets.


Requiem 5.2.0 - "The Gathering Storm"
=====================================

New Features
------------

* Companions radiant quests are restricted to bandit camps until the player has reached a sufficient level.
* All potential follower Companions have perks and equipment according to their roles, their dialogue and Requiem changes.
* The Vigilants of Stendarr are real daedra hunters instead of being daedra food. Using actor variations, tempered equipment and a perk-set that is well focused on their specialization, they can sometimes beat even the toughest daedra. They are not too effective against vampires though for lore-based compatibility with Dawnguard.
* Blocking and wards prevent poison from applying on touch.
* Daedric arrows never break.

Tweaks
------

* Running reduces magicka and stamina regeneration by 100%, scaling with the armor weight penalty. Then, the stamina drain is applied if and only if your stamina regeneration was reduced to 0%. The regeneration penalty doesn't apply to vampires, werewolves, and those under the Sign of the Lady. However, the following effects no longer outright block the penalty:
    * Fortify Magicka (Rank I)
    * Focused Mind
    * Fortify Stamina Regeneration
    * Windrunner (whose name is reverted to Wind Walker)
* Only the cuirass, helmet, gauntlets, boots, and shield are included in the Mass Effect.
* The first heavy armor perk reduces the worn armor weight penalty by 35% and the next four perks by 10% each.
* Casting spells in heavy armor no longer applies an additional unremovable penalty.
* The penalty for untrained spellcasting in armor scales with spell tier instead of effective mass.
    * Casting Novice/Apprentice/Adept/Expert/Master spells in light armor increases spell cost by 10/20/30/40/50% without the perk.
    * Casting Novice/Apprentice/Adept/Expert/Master spells in heavy armor increases spell cost by 20/40/60/80/100% without the perk.
* Wind Walker increases movement speed by up to +10% when wearing no heavy armor: head, chest, hands, feet (was +15% when wearing no heavy armor at all).
* Agile Spellcasting requires Agility and skill 30 (was Dexterity and skill 50).
* Wooden bows break only by weapon power attacks or creatures with mass at least 2.
* Health, stamina, and carry weight bonus of werewolves in human form is cut in half.
* Werewolves in human form have -50% poison resistance and 15% higher spell cost.
* Werewolves in beast form have -100% poison resistance.
* Fortified Muscles increases health and stamina by 50 (was 100).
* Alchemical Intellect increases magicka by 50 (was 150) and restores 1 magicka per second (was 3).
* Fortify stamina regeneration is moved from Fortified Muscles to Regeneration and the perk properly affects vampires.
* Spell absorption on NPCs is replaced by equivalent magic resistance because the random nature of spell absorption and its apparently broken interaction with multiple magic effects lead to frustrating encounters.
* Fortify Health II grants immunity to paralysis (was immunity to absorb effects).
* Fortify Stamina II grants immunity to slow effects from frost spells (was immunity to paralysis).
* Resistance thresholds of slow effects from frost spells are removed.
* Dragons have less polarizing resistances:
    * 75% resistance to their element (was 90%).
    * No resistance or weakness to the opposite element (was -75%).
    * 50% resistance to shock (was 0%).
* Dwarven automatons have no resistance or weakness to frost (was 80%).
* Ancient Knowledge allows tempering dwarven armors and weapons without the smithing perk and adds 150% tempering health to dwarven armors and 75% to dwarven weapons. All other effects are removed.
* Astrid and her disguised assassins use poisons of paralysis instead of irresistible knockdown.
* Giant slaughterfishes have a more believable size and it's possible to survive their bite.
* Enchantments are 25% weaker when Bend the Law of Firsts is active (was 45%).
* Mage Armor V has base magnitude 240 (was 0) and reduces all incoming physical damage by 50% (was 98%).
* Ancestor Guardian reduces all incoming physical damage by 90% (was 98%).
* Protection from Poison has base magnitude 15 (was 20) and cost 200 (was 150).
* Transmute Muscles has base magnitude 30 (was 20), but dualcasting doesn't affect magnitude. This was the only spell in the game that scaled both in magnitude and duration when dualcast.
* Extra damage of banish enchantments is doubled.
* Powerful Healing Aura doesn't grant immunity to absorb effects.
* Healing aura spells don't have a persistent visual effect.
* Building a Hearthfire house doesn't pass time.
* No message box is displayed when attempting to pick a hard lock without followers. Locks are considered "hard" if they can be picked with your current lockpicking expertise but would have a larger sweetspot if your expertise was a level higher.

Bugfixes
--------

* The Mass Effect is reimplemented to be robust to improperly timed equip events sent by the engine. It should no longer be possible to arrive in an inconsistent state under any circumstances.
* Exploding ammo stacks with enchantments and poisons.
* Vigilants of Stendarr don't carry 256 silver bolts.
* Ancient Knowledge affects aetherium armor.
* Mage Armor V isn't suppressed by lower ranks.
* Alchemical Intellect doesn't appear in the active magic effects tab.

Installation
------------

* Release archive uses proper path separators to ensure compatibility with the latest version of 7zip.
* Reqtificator warns if Bug Fixes SSE is not installed.

Compatibility and Modding Support
---------------------------------

* The Mass Effect no longer conflicts with other mods like Unlocked Grip or Equipping Overhaul.

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* Armor weight penalty reduction is stored in the actorvalues `heavyarmormod` and `lightarmormod`.
* All Mass Effect calculations are performed inside the script `REQ_MassEffectArmor` attached to `xx82CC14 <REQ_Ability_MassEffect>`.
* The workaround to force movement speed updates by modifying inventory weight is removed in favor of Bug Fixes SSE.
* A hidden, constant effect, self-targeted value modifier MGEF is offered for every (relevant) actorvalue, as well as a variant with a visible description. These are generic magic effects for use by abilities.
* Astrid has the Mutagen perks instead of the abilities. 


Requiem 5.1.1 - "From Past to Present" Bugfix Pack #1
=====================================================

Tweaks
------

* Light armor tweaks:
    * Chitin: -5 weight.
    * Falmer: Same armor rating and weight as chitin.
    * Glass: -50 armor rating, -15 weight.
    * Hide: Same armor rating, weight, and price as leather.
    * Penitus Oculatus: +3 weight.
    * Stalhrim: -50 armor rating, -15 weight.
* Falmer armor increases poison resistance as material bonus.
* Falmer armor includes the armor type in its name.
* Falmer hardened armor is removed because it's identical to heavy falmer armor in all aspects but name.
* Enchanted ancient nord and Blades armor doesn't replace the base items.
* Enchanted ancient nord armor is restricted to Kvenel the Tongue.
* Dawnguard shield has no enchantment but can be enchanted by the player.
* Help menu entry on ranged resistances makes no reference to "ranks".
* Contradictory description of stalhrim armor bonus is rephrased.
* Description of perks that remove the spell cost penalty for casting spells in armor is rephrased.
* Description of Windrunner is rephrased.

Bugfixes
--------

* Steel plate armor bonus displays the correct magnitude.
* Strong Grip displays the correct magnitude.


Requiem 5.1.0 - "From Past to Present"
======================================

New Features
------------

* Extra armor rating against slash, blunt, and pierce attacks is removed. In its place high-quality armor materials provide unique bonuses. The first Evasion or Heavy Armor perk is required to unlock the bonuses. Each piece of armor grants the following effect, stacking additively up to four times. Shields do not count towards the four items. You can check your current armor material bonus in your active magic effects screen.
    * Aetherium: 8% less damage from blunt weapons.
    * Ancient Falmer: +4% magic resistance.
    * Chitin: 6% less damage from ranged weapons.
    * Daedric: +6% magic resistance.
    * Dawnguard Heavy: 21% less damage from vampiric drain.
    * Dawnguard Light: 14% less damage from vampiric drain.
    * Dragonplate: 9% less damage from the voice, immunity to Unrelenting Force at four pieces.
    * Dragonscale: 6% less damage from the voice, immunity to Unrelenting Force at four pieces.
    * Dwarven: 5% less damage from blunt weapons.
    * Ebony: +6% fire resistance.
    * Elven: Spells are 2% more effective.
    * Falmer: 5% less damage from ranged weapons.
    * Glass: +4% fire resistance.
    * Imperial Legate: Nearby Imperial soldiers deal +4% damage.
    * Nordic Carved: Spells are 3% more effective.
    * Orcish: Power attacks cost 6% less stamina.
    * Stalhrim Heavy: +6% frost resistance.
    * Stalhrim Light: +4% frost resistance.
    * Steel Plate: 8% less damage from slashing weapons.
    * Stormcloak Officer: Nearby Stormcloak soldiers deal+4% damage.
* Each rank of ranged protection provided by armors grants 50 armor rating (was 60). The ranks are no longer listed in the active magic effects screen. To this end, the ranks are reassigned as follows to provide a more intuitive progression:
    * Rank 1 are low-level light armors: Alik'r, Blackguard, Forsworn, Fur, Guard, Hide, Imperial light, Leather, Linwe's, Shrouded, Skaal, Stormcloak, Thieves Guild, and Vampire armor.
    * Rank 2 are mid-level light armors: Chitin, Dawnguard Light, Elven, Falmer, Morag Tong, Reinforced Chitin, Penitus Oculatus, and Scaled armor.
    * Rank 3 are high-level light armors: Ancient Falmer, Dragonscale, Glass, and Stalhrim Light armor.
    * Rank 4 are faction leader light armors: Ancient Shrouded, General Tullius', Nightingale, Thieves Guild Master, Ulfric's, and Vampire Royal armor.
    * Rank 5 are all heavy armors.
* Integration of the armors from the Dragonborn DLC into the mainland continues:
    * Bonemold armor has the same armor rating as steel armor but the same weight as iron armor. It can be bought from Brand-Shei and Revyn Sadri. Tempering requires the Craftsmanship perk.
    * Skaal armor has the same stats as leather armor. It can be bought from Birna. Tempering requires the Craftsmanship perk.
    * Stalhrim armor has the same stats as ebony and glass armor. Tempering requires Ebony smithing and obtaining the necessary knowledge in Solstheim.
    * Two sets of stalhrim armor are found in draugr crypts where an associated quest suggests the presence of a treasure.
    * Enchanted stalhrim armor is sometimes found in draugr boss chests with the Treasure Hunter perk.
* Perk descriptions of Block, Evasion, and Heavy Armor expose the gameplay effects. They use the following convention to denote how percentages stack. This is extended to Marksmanship, One-Handed, and Two-Handed perks too.
    * "X% less/more" means this effect stacks multiplicatively with other effects.
    * "+X% less/more" means this effect stacks additively with identical effects and is then applied as a multiplicative effect.
* Imperial Legates have a unique armor. Legate Rikke wears an enchanted set. Already spawned NPCs may continue using their old outfit.

Tweaks
------

* Armor rating of many mid and high-level armors is increased by 100 to counterbalance the loss of additional damage protections.
* Imperial, Stormcloak, and guard armors have +12/30/9/9/15 AR (head/body/hands/feet/shield) when worn by a faction member (including the player) ~to counterbalance the loss of additional damage protections~ because the officers have been drilling the new recruits.
* Hidden damage reduction is removed from Evasion perks.
* Weight of several light armors is increased to provide a more logical progression and account for the new material bonuses.
    * Chitin: +10
    * Dawnguard: +15
    * Dragonscale: +10
    * Glass: +25
    * Scaled: +5
* Slight armor rating and weight adjustments to have a more unified ruleset.
    * Armor rating of heavy armors is always a multiple of 100.
    * Armor rating of light armor is always a multiple of 50.
    * Armor weight of heavy armors is always a multiple of 10.
    * Armor rating is rounded in favor of boots and at the expense of gauntlets because as a result of the above changes the armor rating of a helmet, cuirass, or shield is always an integer.
    * Magnitude of Fortify Armor Rating enchantments increases in steps of 20.
* Ancient Nord armor has the same armor rating and weight as steel armor (was similar to iron) and is enchanted to reduce shout cooldown.
* Blades armor is enchanted.
    * Helmet increases ranged damage by 25%.
    * Armor increases armor rating by 130.
    * Gauntlets increase one-handed damage by 25%.
    * Boots increase fire resistance by 45%.
    * Shield increases magic resistance by 18%.
* Executioner armor is a light armor.
* Falmer armor is light armor.
* Ulfric's armor is light armor.
* Heavy chitin armor is light armor and is now known as reinforced chitin armor.
* Banded Iron Shield is repurposed as Ancient Nord Shield. Draugr use this shield instead of the iron shield.
* Stormcloak armor always has sleeves.
* Summerset Shadows armor can be looted.
* Apparel name changes:
    * Nordic armor -> Nordic Carved armor
    * Scaled armor -> Scale armor
    * Dawnguard and Imperial armor include the armor type in their name.
    * Names don't use a possessive apostrophe unless they belong to a specific character.
    * Chitin, Imperial Heavy, Morag Tong, Stalhrim, Stormcloak Officer, Ulfric's: Bracers -> Gauntlets
    * Aetherium, Alain's, Ancient Falmer, Stormcloak: Cuirass -> Armor
    * Studded Imperial Armor -> Imperial Studded Armor
    * Gold Jeweled Necklace -> Gold Amethyst Necklace
    * Silver Jeweled Necklace -> Silver Garnet Necklace
    * Shoes -> Chef's Shoes
    * Shoes -> Child's Shoes
    * Robes -> Plain Robes
    * Hooded Monk Robes -> Hooded Plain Robes
    * Monk Boots -> Plain Boots
* Armor variants with small aesthetic differences have the same stats and name.
* Steel Shin Boots and Steel Imperial Gauntlets are removed because they are identical to standard Imperial armor in all aspects but name.
* Elven light armor is removed because it is identical to standard elven armor in all aspects but name.
* Price of Wolf armor is increased to 5000.
* Weight of glass weapons is increased by 2.
* Vanilla Dawnguard set bonus is removed.
* Crafting elven equipment requires quicksilver instead of iron.
* Help menu entries are updated to reflect the changes to the armor system.
* Probability to find "special" loot (i.e. anything affected by the Treasure Hunter perk) is reduced to zero unless you have the Treasure Hunter perk.
* All NPCs wearing mid and high-level armors should have at least the first armor perk.

Bugfixes
--------

* Armored skeletons can equip iron shields.
* Disarming Bash has success probability exactly 25% and 5% respectively.

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* `000AD241 <ArmorImperialHeavyOutfitOfficer>` is exclusively used by Legate Rikke because during A Jagged Crown she is scripted to change into this outfit.
* General Tullius' Armor and Vampire Royal Armor have resistance keywords assigned in the plugin.
* Ancient Falmer armor is refered to as such in `ArmorKeywordAssignments_Requiem.esp.conf`.


Requiem 5.0.3 - "From the Ashes" Bugfix Pack #3
===============================================

Bugfixes
--------

* Avoid division by zero when disabling the innate running speed penalty. This resolves the camera spinning around very quickly when using the mod "True Directional Movement" and similar unexpected bugs.
* Examine doesn't stagger wounded dragons.

Installation
------------

* The Reqtificator produces a helpful error message when a template referenced by an actor does not exist in the load order.
* The Reqtificator detects circular dependencies in actor inheritance trees and emits a useful error message instead of deadlocking.
* The Reqtificator will emit a useful error message when a plugin is listed in the load order file but doesn't exist on disk.
* Missing master errors are more detailed and specify whether the missing master is completely missing, disabled or loaded out of order.
* The "too many masters" error that can occur during the patch export explains the problem in more detail.
* Remove "Hide Activate Button" option from the FOMOD installer because it doesn't work in SSE.

Compatibility and Modding Support
---------------------------------

* Bashed Patch detection is more reliable. In particular, it no longer incorrectly triggers if the mod Glamoril is installed.
* Leveled List merger ignores records if a later overwrite has the preceding override mod as master.
* Carrying Dawnbreaker and Meridia's Beacon hurts all undead actors instead of only vampires.
* Remove unnecessary changes to vampire NPCs that interfered with automatically merging actor visuals.
* The threshold (sum of starting attributes) above which a playable race is skipped by the custom race logic of the Reqtificator is raised to 200 to support custom races with slightly higher starting attributes.


Requiem 5.0.2 - "From the Ashes" Bugfix Pack #2
===============================================

Tweaks
------

* Disable innate running speed penalty from the game in favor of Requiem's mass effect implementation.

Installation
------------

* The old manual is marked as outdated in its file name to avoid confusion.
* The minimum SKSE version check checks for the minimum version of the scripts required by Requiem instead of the latest SKSE version.
* The Reqtificator will no longer freeze when saving the patch in some scenarios, but instead fail with an error message and useful logs.
* The root cause for the ingame error "`Requiem for the Indifferent.esp` is missing" is fixed. The Reqtificator checks the load order for missing masters before patching instead of generating a `Requiem for the Indifferent.esp` with missing masters.

Compatibility and Modding Support
---------------------------------

* The Reqtificator correctly supports plugins that define more than two REQ-Tags in their plugin description.


Requiem 5.0.1 - "From the Ashes" Bugfix Pack #1
===============================================

Installation
------------

* The "cannot locate game folder" issue is resolved.
* The Reqtificator's error reporting while patching records has been improved. Error reports will now include the record that was patched when the failure occured and the last mod changing the record. 
* The Reqtificator provides more accurate information when finding an invalid reference inside a formlist.


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
* The Reqtificator no longer allows priority-overrides in leveled list merges. The corresponding formlists `RFTI_List_LLMerge_HighPriority` and `RFTI_List_LLMerge_MediumPriority` have been deleted.
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


Requiem 4.0.2 - "Threshold" Bugfix Pack #2
==========================================

Installation
------------

* Reqtificator doesn't display a warning if a Bashed Patch is detected.
* Ingame Bashed Patch check detects if the Leveled List option has been used.

Compatibility and Modding Support
---------------------------------

* Updated Bash tags are added to `Requiem.esp`.
* Templates are removed from all unique items defined in the base game. New tempering recipes are added where necessary.


Requiem 4.0.1 - "Threshold" Bugfix Pack #1
==========================================

Bugfixes
--------

* The armor rating scaling factor is corrected, each point of armor rating provides 0.1% damage reduction.
* Armor piercing scaling factors are corrected. Each point of armor penetration provides 1% damage reduction bypass for power and ranged attacks. Standard melee attacks have half as much armor penetration.
* The perk description for Advanced Blacksmithing is updated and no longer mentions crafting exotic blades.
* "Craftsmanship" is spelled correctly in the perk "Arcane Craftsmanship" and the smithing tomes for Elven and Daedric materials.


Requiem 4.0.0 - "Threshold"
===========================

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
* `REQ_AlreadyReqtified` is replaced by new keywords that provide the same features, but with more fine-grained control and clearly communicated effects.
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
* KW prefix is removed from all keywords.
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


Requiem 3.4.0 - "The Shadow Theory"
===================================

New Features
------------

* Invisible Entities have been reworked completely and are now known as "The Slighted". Slighted are Dremora that suffer from a rather special condition. They will now become partially visible after attacking or getting hit. The various encounters are linked via a common background story explained in several letters. One additional encounter with the Slighted has been added to the world and the existing encounters have been reworked. The Slighted will spawn even if the Invisible Entities in that location have been killed already.
* Healing while sleeping has been reverted to its old behavior. You will fully recover your health when you have an active healing effect while going to sleep. You'll also heal naturally if your health is above 90% when sleeping.
* Requiem no longer blocks the legendary skills feature. It is still not supported by Requiem in any way and you'll be warned that this might break some Requiem perks if you try to use it anyway. (We removed the SKSE plugin which blocked this feature to reduce our maintenance overhead.)
* Scrolls of Detect Daedra can be found for sale and as random loot. These work like Detect Life scrolls, but they'll reveal deadric beings instead. (Daedra are not revealed by normal detect life effects.)

Tweaks
------

* Magic resistance no longer protects against deep freeze paralysis effects, frost resistance works as before. Stamina Rank II enchantments protect against the deep freeze paralysis effects as well.
* Stamina Rank II enchantments negate the paralysis effect from Stunning Precision.
* Pickpocket potions and enchantments are changed to multiplicative modifiers.
* Spell Tome: Summon Invisible Stalker is removed. Mages who already know the spell can still use it. (The spell will be removed competely in 4.0.0)
* Summoning a mystic bound bow grants mystic bound arrows, which have much higher armor penetration power than ordinary bound arrows. (same strength as ebony arrows)
* Unarmed combat only receives the flat damage bonus from the derived attributes system. Stamina and health now contribute equally to it and the magnitude of the bonus has been increased to compensate the removal of the percentual damage bonus.
* Damage of iron arrows and bolts is increased.
* Bandits without the Ranger perk don't use light weapons.
* Bandits don't use dwarven and orcish crossbows.
* Hadvar and Ralof wear their original light armors in the opening scene.
* Mace and Warhammer Focus perks increase power attack damage.
* Blunt resistance of Dwarven Centurions and Enchanted Spheres is decreased.
* Pierce resistance of Draugr and Dragon Priests is decreased.
* Ancano is a powerful Electromancer.
* Detect Life spells and scrolls explicitly state that they will not detect soulless beings like Daedra.
* Dremora can be paralyzed.
* Dremora are completely immune to Unrelenting Force and similar effects.
* Magic resistance of Dremora is reduced to 50%.
* Illusion resistance of Dremora is reduced.
* Unarmed damage of Dremora is reduced by 50.
* Knock spells have been renamed to "Knock (Rank X)" to improve sorting in the spell menu.
* The master conjuration robe has been removed from Fellglow Keep. One of the two handplaced master destruction robes has been replaced with the master conjuration robe instead.
* Merchants are more likely to sell Adept, Expert and Master scrolls.

Bugfixes
--------

* Daggers receive the same damage bonus from the derived attributes system as other one-handed weapons. The derived atrributes system no longer modifies your overall damage multiplier.
* All experience modifiers are disabled in training mode. Skills will correctly advance during training.
* Various incorrect attack settings of Dremora are fixed. In particular they no longer use ineffective sprinting attacks that are not supposed to be used by NPCs.
* Priority of Pickpocket perks is adjusted so that Pickpocket potions and enchantments never reduce the effective success chance.
* NPCs can enter and leave the Atronach Forge in Fellglow Keep.
* Silver Tongue affects both buying and selling prices instead of boosting selling prices twice.

Typos and Grammar
-----------------

* Several minor typos have been fixed.

Installation
------------

* `Reqtificator.bat` only exists in the top-level folder to avoid possible confusion.
* A rare bug where the Reqtificator gets stuck before showing the main menu has been fixed.
* Bundled JVM upgraded to OpenJDK 13.
* Reqtificator provides specific error messages when encountering a problem with tempered items.

Compatibility and Modding Support
---------------------------------

* Formlists used by the perk respec feature from the Dragonborn DLC are populated with Requiem's perks.

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* The Skyrim Knights assets used by Hadvar's and Ralof's former armors are removed.
* Hadvar's and Ralof's artificial immunity to spider poison is removed.
* Unused references in Fellglow Keep are deleted.
* Paralysis immunity conditions have been streamlined: the keyword `REQ_KW_ProtectionFromParalysis` is only used by magic effects, `ImmuneParalysis` is only used by races and actors.
* Paralysis immunity of Spriggan encounters has been moved to their races.


Requiem 3.3.0 - "The Quantum Enigma"
====================================

New Features
------------

* Nordic armor and shield from the Dragonborn DLC is integrated into the world. Bandits sometimes wear parts of the armor. Blacksmiths have a chance to sell the armor and it can be crafted with the Advanced Blacksmithing perk.

Tweaks
------

* Dragons, Dragon Priests, Dwarven Centurions and Enchanted Spheres have increased protection.
* Health of Forgemaster is doubled and damage, mass and illusion resistance is increased to match Dwarven Centurions.
* The Spellbreaking enchantment is applied with each hit and active magic effects are correctly dispelled from the target.
* Spawn chances of heavy armor sold by blacksmiths are adjusted to be more consistent.

Bugfixes
--------

* Poisons from Chaurus, Frostbite Spider and Spriggan are affected by Cure Posion.
* Summons no longer flee from dragons.
* Kvenel the Tongue no longer drops an overpowered version of Ancient Nord Armor.

Installation
------------

* The Reqtificator ships as a self-contained application. You thus no longer need to install any system-wide Java runtime to set up Requiem. Instead, the Reqtificator ships with its own, stripped down JVM version as part of the Requiem download.


Requiem 3.2.0 - "Design Your Universe"
======================================

New Features
------------

* The total weight of the ammunition you carry is now shown by the misc item "quiver" in your inventory. A help topic about ammunition weight has been added as well.
* The total weight of your gold coins is now shown by the misc item "coin purse" in your inventory. Help topics about gold weight and the examine power have been added too.

Tweaks
------

* Gold weight has been reduced by 75% and quest rewards yielding a mix of gold and gems have been reverted to pure gold rewards.
* Consistent formulae for damage, weight, price, speed and reach of melee weapons are restored, with the following major changes deserving special mention:
    * Damage of elven and dwarven weapons is swapped.
    * Dragonbone weapons have reduced damage so that they are no longer strictly better than daedric weapons.
    * Bound battleaxe has increased damage to be on par with the bound sword.
    * Daedric and forsworn weapons have reduced weight.
    * Silver weapons have the same weight as steel weapons.
    * Honed draugr weapons have the same weight as standard draugr weapons.
    * Honed falmer weapons have increased damage to match other honed weapons.
    * Sabers and scimitars are no longer strictly better than swords.
    * Longswords are alternative swords instead of sharing their stats with war axes.
    * Ghostblade has increased damage and no weight.
    * Rueful Axe has the same damage as a daedric battleaxe, but reduced weight because it's made of silver.
    * Mace of Molag Bal has the same damage as a daedric mace, but increased weight to match its size.
    * Volendrung has the same damage as a daedric warhammer, but increased weight to match its size.
* Giant bounties pay out 1000 gold.
* A note is posted on the entrance door of the Abandoned House to warn about the dangers ahead.
* Galmar Stone-Fist wears an enchanted Nordic Armor.
* Vigiliant Tyranus wears an ordinary Vigilant Plate Armor.
* Legendary steel armor can no longer be crafted and the related "WarChief" assets have been removed from the distribution. (Previously acquired legendary steel armors have the stats and look of the Nordic Armor.)
* Legendary steel plate armor can no longer be crafted and the related "SPOA Silver Knight" assets have been removed from the distribution. (Previously acquired legendary steel armors have the stats and look of the Vigilant Plate Armor.)
* Windrunner no longer increases stamina regeneration. Instead it increases movement speed by 15%.
* Potion of Cure Disease is restored and sold by all alchemists at an affordable price. All instances of Potion of Cleansing are replaced by Potion of Cure Disease and ingredients use Cure Disease instead of Cleansing.
* Potion of Cure Poison is restored and sold by alchemists at a fair price. Some ingredients now have Cure Poison as effect.
* Potion of Cleansing is sometimes sold by alchemists at a high price. Ingredients no longer have this effect.
* Ebony armor has increased weight.
* Dwarven armor has increased armor rating and weight.
* Dwarven armor is resistant to blunt attacks in accordance with the lore, but it no longer has special resistance to ranged and pierce attacks.
* Steel plate armor is more resistant to ranged attacks, but weaker to slash attacks.
* Orcish armor is more resistant to slash attacks, but it longer has resistance to blunt attacks.
* Nordic armor is resistant to blunt attacks, but weaker to slash attacks than other heavy armors.

Bugfixes
--------

* The ranged damage resistance of ghosts and spirits can be bypassed by silver and daedric ammunition.
* Alcohol consumption sound attenuates with distance.
* Dagger Focus states in its description that the sneak attack damage bonus is irresistable and affects creatures that are usually immune to sneak attacks.

Compatibility and Modding Support
---------------------------------

* The Reqtificator has a new config file which allow mod authors to customize the attribute offsets and removed starting spells for the player record.
* Severel assets unrelated to Requiem's core business have been removed: burned book retexture, lockpick mesh, lockpick menu retexture and dual wield sprint animation.
* Script initialization is delayed by 5 seconds to reduce the chance of other mods interfering.

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* Weapons have more useful and consistent EditorIDs.


Requiem 3.1.1 - "Steel meets Steel" Bugfix Pack #1
==================================================

Tweaks
------

* Restore alphabetical sorting of potions from weakest to strongest by renaming Feeble to Diluted.
* The reward for "A Lovely Letter" is reverted to its original value of 25 gold.

Bugfixes
--------

* Buying lumber from sawmill owners costs you 400 gold as the dialogue option states.
* Becoming Thane of the Rift grants the correct version of the Blade of the Rift.

Installation
------------

* The Reqtificator will not abort when encountering an actor with inheritance flags set but no defined template. Such actors are handled as if they have no inheritance settings defined at all.

Compatibility and Modding Support
---------------------------------

* A warning will be written to the Reqtificator log files if an actor has inheritance flags set but no template defined.
* Failures to find the record for a given FormId result in human-readable error messages. (This happens e.g. if an actor inherits from a record that has been deleted.)
* Edits to the script of the quest "A lovely Letter" reverted to improve compatibility with e.g. The Choice Is Yours.
* Reward edits for "Before the Storm" moved to the leveled lists and all edits to the quest itself are removed.


Requiem 3.1.0 - "Steel meets Steel"
===================================

New Features
------------

* Armor and Armor Penetration tweaks
    * Every point of expertise grants 0.25% armor penetration on melee power and ranged attacks.
    * Standard melee attacks have half the armor penetration of power attacks.
    * Armors can have resistances against some damage types, granting additional armor rating against such attacks.
        * All damage resistances have 5 tiers, each giving a stacking +50 armor rating.
        * Extra armor rating from damage resistances is affected by armor penetration.
    * most armors got rebalanced in terms of weight, base armor rating and additional damage resistances, with the following major changes deserving special mention:
        * Daedric, iron, orcish, scaled and steel armors are significantly lighter.
        * Dragonscale, dragonplate and daedric armors have considerably reduced base armor rating.
        * Ebony and ancient falmer armors have considerably increased base armor rating.
        * Guard armor protection has been improved considerably (chainmail below the padding), but so has its weight compared to other light armors.
        * Hammerfell armor is a stronger version of the guard chainmail armors (more weight and damage protection).
        * Stormcloak armor is a light armor with same stats as guard armor.
        * Stormcloak officer armor is a heavy armor on par with steel armor.
        * Wolf armor is a light-weight variant of steelplate armor.
        * Blades, Dawnguard and Imperial heavy armors are on par with steel armor.
        * Thieves Guild, Nightingale and Dark Brotherhood armors have their weight adjusted to be on par with leather armor.
* Creature damage resistances have been adjusted to the new system.
    * Like armors they can have resistances against some damage types, granting additional armor rating against such attacks.
    * Some supernatural creatures also have additional damage reductions which cannot be bypassed by armor penetration, e.g. a frost atronach is a massive chunk of ice that won't care too much about your arrows or sword slashes.
    * Centurions are much more resilient again.
* Expertise bonuses for higher-quality ammunition have been increased considerably and the gap between arrows and bolts has been reduced.

Tweaks
------

* Equipped gear can be changed freely in combat.
* Enemies following you through load orders into new cells during combat can be disabled in the Reqtificator options.
* Chitin armor from the Dragonborn DLC can be bought from Brand-Shei and Revyn Sadri. It can be tempered with the Craftsmanship perk.
* Bandit boss marksmen no longer use ammunition above steel/silver quality.
* Skill points have been redistributed for bandit boss marksmen, removing their access to top-tier marksman perks.
* Stamina threshold below which physical attacks no longer stagger opponents is raised to 10 to match other exhaustion penalties. This feature now applies to NPCs too.
* Various unused or rarely used third party assets have been removed to make the distribution leaner (all changes are backwards compatible, i.e. you will not suddenly be naked after updating).
    * All unused assets from Immersive Armors
    * Vagabond Armor from Immersive Armors - Alain Dufont wears an enchanted scaled armor instead of a unique Vagabond armor.
    * Stormlord Armor - Ulfric got back his original, unique gear from the base game as heavy armor set and some additional heavy armor perks.
    * Ebony Plate armor with hold insigina emblazoned tunic - Irileth wears an enchanted set of ebony armor instead of an ebony plate armor.
    * Orichalcum Plate armor with hold insigina emblazoned tunic - Commander Caius wears a standard Whiterun guard plate armor instead of an Orichalcum plate armor.
    * Golden Elven Armor and Weapons - Elenwen has been reverted to hear Vanilla gear, a Thalmor robe and an elven dagger.
    
Bugfixes
--------

* The "New game required" bug in the Vanilla start is fixed.
* Improved Dawnguard and Ancient Dwemer crossbows are flagged as light crossbows and can be used with the Ranger perk.
* Poison Resist spells do stack with poison resistance potions.
* Silver weapons display the correct damage if wielded by a vampire.

Modding Support
---------------

* The Reqtificator can assign spells and perks to NPCs via automated rules. This works similar to the keyword distribution for armors and weapons. Rules can use conditions based on race and keywords. (For templated actors the conditions must be true for all possible templates.)
* Distribution of gamemechanics perks via the Formlist has been deprecated, use the new rule system instead.

Installation and Compatibility
------------------------------

* REQ-Tag prefixes have been removed. You no longer need to specify a prefix in your REQ-Tags in the plugin description, only the features you want to use. The required EditorID pattern themselves remain unchanged. This change allows you to easily create merged plugins from Requiem-dependent plugins that use REQ-Tags with different prefixes.
* Uploaded a smash setting (used by Mator Smash) specifically for Requiem to the compatibility advice.

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* Removed meshes and textures for various unused or no longer used items:
    * Orichalcum and Ebony Plate armors
    * Stormlord Armor
    * Vagabond Armor
    * Golden Elven Weapons and Armors
    * All unused assets from Immersive Armors
    * Split up general player perk into several smaller perks to accommodate the rule of one. Perk entry priorities are updated accordingly. Conditions have been improved.
* Non-functional perk entry that supposedly tried to make the player immune to stagger from Unrelenting Force is removed.
* Improved EditorIDs of perks.


Requiem 3.0.2 - "Consign To Oblivion" Bugfix Pack #2
====================================================

Tweaks
------

* The Atronach stone does not block the damage and block bonuses from the Green Pact racial ability when Bosmer eat Strange Meat.
* Dawnguard and Dwemer Crossbows have damage values of heavy crossbows, but are classified as light crossbows and thus benefit from faster reload speed and can be used with the Ranger perk.
* Hide Armor has increased armor rating.
* 36 Lessons of Vivec Sermon 27, The Origin of the Mages Guild and The Cantatas Of Vivec have unique book covers.
* Frost always flees combat.
* Combat Reflexes has its duration extended considerably to be more useful.
* Potency of self-crafted spell cost reduction is reduced by 33%.

Bugfixes
--------

* damage bonuses from the derived attributes system for One-handed and Two-handed weapons are displayed correctly in the Skills MCM.
* Dragon priests have natural armor an penetration resistances.
* The Only Cure cannot be started before level 20.
* Edits that are already present in the master files are removed.
* Redguard Hood cannot be worn with a circlet.
* Fur Armor cannot be sold to hunters.
* Activating a business ledger outside of The Number Jobs displays a more accurate notification.
* The "Steal" label of the items in The Sweep Job and The Burglary Job appears red like other criminal actions.
* The "Forge Numbers" label of a business ledger appears red like other criminal actions.
* The boss at Traitor's Post inherits from a proper Requiem template.
* Bandit no longer use the Improved Dawnguard Crossbow.
* A rarely used bandit template always has a crossbow.
* Bandits wear both versions of the steel armor.
* Delevel enchanted imperial swords.
* Duplicate poison resistance is removed from Chaurus.
* Combat Reflexes will not drain any stamina if you don't have the required amount of stamina to activate the power.
* Ghosts and undead characters will not drop any blood for vampire characters when they are from a race that normally does so.
* Shadow Stride has sound and a visual effects.
* Veil of Silence has a visual effect.
* Shadow Sanctuary has sound.
* Attempting to cast and then canceling Shadow Simulacrum doesn't leave a lingering visual effect.

Typos and Grammar
-----------------

* fixed a variety of minor text issues in the Skills MCM

Installation and Compatibility
------------------------------

* The Reqtificator now picks up additions to `xx8F57EA <REQ_Skyproc_ReqtificatorPerks>` from all loaded plugins and thus allows multiple mods to add new game-mechanics perks that should be distributed globally by the Reqtificator.
* Trainer NPCs from third-party mods now work out of the box with Requiem's rescaled skill experience rates.
* Failed setup checks like missing Requiem for the Indifferent trigger only once and will not show the message again in the main menu.
* Radiant Raiment uses the `00106662 <LItemClothesAll>` leveled list.
* Links in the Reqtificator message have been updated to point to the latest Requiem Documentation.
* Lightning source edits are removed.

Internal Quality Improvements (only relevant for modders)
---------------------------------------------------------

* Removed AlreadyReqtified usage for any records that are neither ammunition nor ranged weapons
* Removed several unused scripts
* Removed nullified perks from NPCs.


Requiem 3.0.1 - "Consign To Oblivion" Bugfix Pack #1
====================================================

Tweaks
------

* The new ingame help menu topics are shown when you play the game with an Xbox controller.

Bugfixes
--------

* The derived attributes system no longer interferes with your expertise values.
* Guard steel plate armors have the correct armor rating.
* The duration of the save block needed for testing the setup does not increase after each reload.
* The perk "Eagle Eye" now has "Ranged Combat Training" as a prerequisite as implied by the perk tree.


Requiem 3.0.0 - "Consign To Oblivion"
=====================================

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

* All hidden armor rating is removed; instead cuirasses get +55 armor rating and all other armor items get +15 armor rating. Note to mod authors: Armors with the `REQ_KW_AlreadyReqtified` keyword need to be manually updated to this change.
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


Requiem 2.0.2 - "The Phantom Agony" Bugfix Pack #2
==================================================

Crucial changes to cumbersome coins and contracts, cloying construction, clandestine cups and other complications!

Other Tweaks
------------

* The FOMOD installer now allows you to select an optional plugin which will re-enable access to the unreqtified Dragonborn DLC. This plugin simply re-enables the cultist attack, the travel to Solstheim dialogue, the Ebony Warrior encounter and some crafting recipes from the Dragonborn DLC. 
* A large part of the reward for bounty quests is now paid in gems. Contracts on giants also pay more than they used to. Various other quests that used to give large amounts of gold as reward have a large share of their reward paid out in gems, including "Hail Sithis!", "Tending the Flames", "Aftershock", "Rise in the East" and "The Wolf Queen Awakened".
* You can now see and hear NPCs when they drink alcohol. (Bandits like to use the fortify health effect from alcohol as a health potion substitute which seemed like sudden unjustified health regeneration before.)
* The outdated Vanilla Shrines option is removed from the FOMOD installer. If you have used this option in the past you might want to use [Requiem - Blessings Ignore Crimes](https://www.nexusmods.com/skyrim/mods/50251) instead.
* Talos' greater blessing now gives 15% bonus to melee damage instead of adding a flat 20 points.
* Blacksmiths and fletchers have more common bolts for sale. Fletchers and caravaneers also have limited stocks of explosive ammunition in store. 
* You will be notified when you try to open a lock which qualifies as challenging with your current lockpicking expertise level. This also allows you to delegate the task to your follower if they have the necessary skills. 
* Tiny spiders no longer paralyze or use spit attacks. 
* The lunge attack of large and giant spiders paralyzes instead of knocking down. 
* Updated to USLEEP 3.0.13a. 
* The quest components for "On Hogithum" have been moved to more obvious places to make it easier to start and complete the quest. 
* The yield of stone quarries is doubled to 32 and each Hearthfire house has at least three stone quarries in close proximity. General merchants in major cities sell clay and quarried stone. 

Bugfixes
--------

* The quest "The Whispering Door" now proceeds normally after you acquired the key. (If you already opened the door and picked up the item behind it, the update will progress the quest to the correct state to recover from the bug.)
* The system penalizing excessive use of wait and sleep with reduced regeneration rates has been retracted. It turned out that this feature causes incompatibilities with other mods which let time progress, like Time Flies.
* The technical aspects of the new lockpicking system have been reworked. You can no longer be locked into an NPC's house. You will be notified if a lock is considered challenging for you. Compatibility with mods that use scripted activation-blocks has also been improved.
* The potency of self-made lockpicking enchantments now scales properly with your enchanting perks.
* Followers need the correct lockpicking expertise to pick master level locks.
* Evasion training works as intended.
* Isran once again trains heavy armor instead of evasion.
* All racial skill rate bonuses are also applied to their vampire variants.
* Transforming into a vampire lord while having Dawnbreaker or the beacon doesn't save your damned existence from Meridia's Malediction.
* The Oghma Infinium cannot be read multiple times in quick succession to get the reward more than once.
* Daedra Banishing spells, staffs and scrolls do not scare atronachs permanently if the banishment fails.
* A light armored version of the Ancient Nord Armor which had very high armor rating can no longer be obtained by the player. Any armor pieces in the player's inventory will be removed.
* Stamina buffs from Ring of Namira no longer stack.
* The Indomitable Force perk no longer affects self-targeted shouts.
* Jyrik Gauldurson is no longer immediately aggressive during "Forbidden Legend".
* Legendary Elven Shield has block and bash data of a light shield.
* The master locked chest in Yngvild can be opened with the key that is looted from the boss.
* The LItemArmorHelmetLightBest leveled list contains Imperial Light Helmet instead of Imperial Helmet.
* Gorak the Trollslayer is immune to paralysis.
* A special Giant Slaughterfish ignores 75% of armor like all other Giant Slaughterfishes.
* Small and big variants of the sabre cats can respawn.
* Hitting Alduin with a warhammer makes a sound.
* Deleveled some items from "Missing in Action" that were still leveled.
* Fixed various flags on notes and books added by Requiem.
* The USLEEP-added cooked boar meat recipe is disabled.
* Fixed an incorrect spawn chance for an expert alteration tomes leveled item.
* Savos Aren should no longer show up naked.

Reqtificator and Installation changes
-------------------------------------

* Races not satisfying the requirements for Requiem are reported as intended by the Reqtificator with a detailed report about which requirements failed.
* The Reqtificator lists the mods you registered as visual template providers in the logfiles.

Typos and Grammar fixes
-----------------------

* spell learning text
* skills MCM description for poison resistance
* descriptions of invisibility spells
* various perk descriptions


Requiem 2.0.1 - "The Phantom Agony" Bugfix Pack #1
==================================================

Audacious adventurers, eager to explore, came upon some unexpected obstacles. From too-tough terrors and tepid tucker to taciturn tools and tiny typos, this bugfix pack will ease your torments.

Other Tweaks
------------

* Alduin has no magic resistance and no more fire dragon traits, but 33% resistance against all elements
* Alduin no longer has a special health regeneration in Sovngarde while not under the effect of Dragonrend
* Centurions and Alduin have the intended armor rating of 1200 points
* Daedra hearts have their effect strenghts reduced to 10% of the old values and the duration increased fourfold
* Several additional merchants are willing to trade gems (Urag, Babette, Elrindir, Fihada, Taarie/Endarie) and jewellery (Urag, Elrindir, Fihada)
* Glass longswords and scimitars weights reduced
* Vegetable soup is as powerful as cabbage potato soup
* Meridia's malediction now causes 20 damage per second
* updated to USLEEP 3.0.12

Bugfixes
--------

* Reloading Wait Autosaves doesn't reduce your magicka and health regeneration rates
* Meridia's Malediction does not prevent vampires from waiting as long as they don't have Dawnbreaker or the beacon
* Incorrectly assigned NPC-variants of the Unrelenting Force shout are removed from the player and the correct shout added if necessary
* Dragonrend now bypasses magic resistance and ignores reflect/absorb effects
* Imperials don't glow green when sprinting
* Copper and Sapphire circlets don't occupy the feet slot
* Healing Ray's stamina regeneration effect from Respite has the correct type
* Silver Tanto keywords fixed
* Necromantic Empowerment tomes have conjuration tome models in the inventory
* Fixed leveled content remaining on afflicted gear lists

Reqtificator and Installation changes
-------------------------------------

* Failures to acquire enough memory in the Reqtificator don't fail silently
* Failures to detect the installation language of Skyrim don't fail silently
* Version numbers in plugin descriptions have been updated
* New Actors created for the ActorVariations feature inherit their display name from the skill template instead of getting a technical identifier of the merged templates

Typos and Grammar fixes
-----------------------

* Powder of Storms 
* Hogithum Beer
* Torturer's Guide
* Craftsman's Manual


Requiem 2.0.0 - "The Phantom Agony"
===================================

Our favourite overhaul has been overhauled! With USKP ousted we're now only usable with USLEEP, so dependent on all DLCs (though with Dragonborn disabled). With awesome archery, ferocious fighting, luscious lockpicking, hearthfire happiness, sumptuous snacks, lighter looting, banished bugs, gripping games, and one curious quest.

Highlights
----------

**Awesome Archery:** The Marksman perk tree has been rearranged and archery bugs have been fixed. The Ranger perk (previously Fast Movement) only works with light bows and crossbows... but it only takes a little skill to acquire, and sneaking no longer breaks it. Heavy bow and crossbow users will find their weapons less draining. Even novices can send arrows winging their way over a distance, unless you're trying to be sneaky. Different parts of armor provide different arrow protection, with a simple keyword-based approach that modders can easily follow. Boots and gauntlets will no longer protect your innards! Perks can help you to overcome such arrow protection and skilled marksman might even find some weakspots in the armor plating of automatons. And silver arrows, advanced bolts and explosive bolts have all been given a make-over and are now distinct and beautiful.

**Ferocious Fighting:** Evasion perks grant a reduction to physical damage and an increase in evasion xp gain... but only if you wear no heavy armor. Bashing perks work with both shields and weapon bashes. Fortify armor still further with enchantments. But it's not all in your favour: effects which were formerly the exclusive privilege of players are now distributed to NPCs, who can use daedric weapons, warhammers and maces to pierce armor just as you do, and some bosses are even more dangerous... even when newly risen from their sarcophagi.

**Luscious Lockpicking:** With a more gradual difficulty curve, various chests are no longer out of your reach until you suddenly find inspiration. While some chests may still defy your talents, others will require true dexterity, and several lockpicks. Friends and hirelings are restricted by their own skills... and their morals. Lockpicking bonuses from different sources such as potions and enchantments can be combined and some unique items and effects will also help you to coax open more challenging locks. Mages no longer have to deal with quicksilver ore if they want to use arcane arts to unlock a lock, but the new "Knock" spells have their own twists...

**Hearthfire Happiness:** Hearthfire is now fully integrated. The three Hearthfire houses are available at low levels... though you still have to do some favours for the Jarls first. They'll grant you enough material to build a bed for the night, after which you might have to do some searching; stone and clay of suitable quality can be hard to find! And building the houses will actually take time...

**Sumptuous Snacks:** The cuisine of Skyrim has been refined for the most discerning palate. Stews remain stupendous, alcohol amazing, Hearthfire's ovens are outstanding and getting drunk is just delightful. Strong Nords won't be double-counting dragons after only a single ale, and a gulp of good booze might give you a useful edge. But beware; too much alcohol might have unexpected interactions with your breakfast stew!

**Lighter Looting:** Gold can weigh down the body as well as the soul. Gems and jewellery are more commonly found and often more valuable, and more merchants are willing to buy them for a fair price. Armor is easier to carry when you wear it, and arrows weigh much less than they did.

**Banished Bugs:** From horses that sound like catapults to mountain flower trickery, from misbehaving rune spells to overpowered vegetables, a multitude of bugs and unbalancing tweaks including those reported have been fixed.

**Gripping Games:** The language and wording of Requiem-specific messages and descriptions has been reworked for easier, more immersive reading, from the Reqtificator to initialization, to picking your character and playing the game. Mage difficulty tags are reverted to vanilla for consistency, summon spell names have also been made consistent and more immersive, and vampires will no longer drop dead immediately just for touching shiny things. With a meaner, leaner MCM, updated descriptions that you can actually read, and several other requested changes included, Requiem games will be smoother and more believable than ever before.

**A Curious Quest:** While adventuring you find a strange piece of paper. It looks like a fragment of something eerily familiar, but where are the other pieces? Help the Bard's College reassemble the lost work... and find out what really happened on Hogithum.

**Legendarily Lazy:** We no longer support USKP, and as such require all DLCs. Note that Dragonborn is disabled; the quest will not run and no radiant quests will send you to Solstheim without a suitable patch.

Other Tweaks
------------

* Various Easter Eggs
* Dwarven automatons have huge armor rating instead of flat damage reduction
* A book that explains the Standing Stone
* Recharging enchanted items requires the first rank of the basic enchanting perks and recharge scrolls are considerably cheaper
* Enchanting-affine races can recharge items without perks but need the first perk to enchant new items
* dual-wield sneak attacks are allowed, but they only deal half the sneak bonus damage per blow
* Katanas and Tantos require advanced smithing perk
* Blacksmiths offer more variety in common crafting materials
* Sleeping allows minor wounds to heal, but healing effects will wear off while you sleep
* Transmute: Muscles has less magnitude and duration but dualcasting increases both
* Potions and enchantments of Stamina Regeneration negate stamina drain from running
* Forsworn fierce enough to have half a chance of escaping Cidhna Mine
* Divines no longer hate you just for thinking about joining the Thieves' Guild
* "Search" becomes "Examine", both searching as before and also providing info on your target's health (e.g.: your horse)
* NPCs will slowly recover health when the player isn't around
* Various weak NPC bosses are more dangerous
* Falion is actually a Master conjurer
* Soul tomatoes have been rebalanced
* Ebony Vampires have randomized loot
* Daedric, Undead and Nature spirit summons from Conjuration magic are more distinguished
* New perk for Spirit summons
* Julianos' additional magicka bonus becomes active when your base magicka is larger than 250
* Necromantic Empowerment (Rank II) is a concentration spell with reduced strength
* All master spells are offered when taking the master perk
* Daedric bows and crossbows no longer have innate armor piercing
* Missing Daedric items have been hidden across Tamriel
* Lockpicks are crafted at the forge and not at the smelter. The recipe creates 10 lockpicks instead of 3
* Dispel, Healing Aura, Necromantic Empowerment, Mage Armor, Mage Shield, Protection from Poison, Transcendance and Transmute Muscles are renamed to "on Target" or "on Self" instead of "Rank (x)"
* Broken (cross)bows no longer magically turn into ingots and firewood
* All absorb spells will require a momentary focus on the target before they start to apply
* Alchemical Intellect is boosted
* Added Shalidor's Mirror spell as reward for a certain quest
* Pickaxe and Woodcutter's Axe are no longer considered war axe but still benefit from generic one-handed perks
* "House of Horrors" starts at level 20
* Some valuable loot in Nightcaller Temple has been relocated
* The fire/frost ball shout from dragons deals 120 damage per second for 5 seconds instead of 600 instant damage
* The Way of the Voice perks no longer require meditating on word of power but become available as you progress the main quest
* Phantasmal killer is revised to properly apply the suicide animation, and the empowered version will work against most living beings. A scary audio effect (the Phantasmal Killer!) will play on the target upon successful cast
* Sunfire Cloak deals 40 damage per second instead of 15
* Added a lore book about vampires (original text contributed by Isphus)
* Meridia's Beacon and Dawnbreaker don't kill vampires instantly and the beacon can be dropped
* Removed MCM options that are no longer needed or considerably changed the intended balance
* Added Dwarven longswords
* Alain's gear includes a helmet
* It's no longer possible to make skills legendary
* Icewind's base magnitude is reduced from 20 to 17
* Smithing enchantments and potions directly increase the smithing skill
* Destroying the Skull of Corruption will make you eligible for Mara's greater blessing
* Completing "Elisif's Tribute" will make you eligible for Talos' greater blessing
* Vampires have more savage looking unarmed attacks
* Bag of Holding increases carry weight by 25 points instead of 100
* One more Bag of Holding can be found in Tamriel
* Daedric melee weapons penetrate armor by 30% instead of 50%
* A more reasonable amount of dwarven scrap can be found in chests
* More appropriate reward for completing "Missing in Action"
* Falmers rarely carry gold anymore
* Most miscellaneous items have a weight and value
* Khajiits no longer have a penalty to spell cost
* Powder of the Void is Powder of Storms
* Powders' have lower crafting requirements
* Slow Time has tripled recovery time
* Bound arrows are treated like silver arrows and have limited armor piercing power
* You will be pointed towards the local inkeeper instead of the Thieves Guild during "A Cornered Rat" to prevent characters from being forced into the Thieves Guild
* Ice Nova is renamed Snow Spume
* lower skill rate MCM option can now be changed in 5% steps

Bugfixes
--------

* Illusion spells no longer grant experience when used on dead targets
* Vampiric sight works for vampire lords and all levels of hunger
* "New Allegiances" can be completed as intended
* Melee attacks in human form no longer increase Werewolf experience
* Requiem no longer kills players on initialization
* Dragons will eat NPCs as intended in cutscenes (already part of the 1.9.4.1 hotfix)
* Absorb spells no longer bypass ward
* Absorb spells no longer absorb attributes once your victims have no more left
* Required ingredients for alchemy perks are consumed
* Mark and Recall introduced in place of Teleport I to help avoid game-breaking situations
* Werewolves and vampire lords get dressed and undressed appropriately
* Dead soldiers in the "fake soldier" encounter stay dead
* Dual-casting bonus effects are applied correctly
* Reanimated corpses are properly undead
* Reanimated corpses can go through loading screens
* Bound Sword no longer causes CTDs when used by NPCs
* Power Shot stagger no longer causes arrows to miss
* Flame/Frost/Lightning/Sunfire Cloak empowers correctly with USLEEP 3.0.2 and later
* Fundamental Destruction scales correctly
* Random comments about the player having joined the College of Winterhold trigger after completing "First Lessons"
* Rune Mastery allows three simultaneous runes
* Trampling horses and bull-rushing humanoids no longer sound like catapults
* NPCs can use reflective wards properly
* Hammerfell Coif, Hammerfell Chainmail and Alain's armor can be sold to vendors
* The Axe of Eastmarch has enchantment charges
* Dawnbreaker and Ebony Blade no longer display enchantment charges because they have infinite charges
* Vale Sabrecats have proper loot
* fixed typos in various loading screens
* Wooden chests in giant camps can be bashed
* Several perks from Smithing, Alchemy and Speech that require items are no longer appear greyed out after the player has taken them
* Added tempering recipes for all unique weapons added by Requiem
* Description of explosive ammunition is now correct
* Chief Yamarz has heavy armor perks
* The Way of the Voice perks actually work
* The White Phial has the same value when empty and filled
* All magic effects have the appropriate magic skill assigned and are correctly boosted by other effects
* Recharging weapons with scrolls no longers levels enchanting
* The daedric weapons looted from the Dremora at Mehrunes Dagon's Shrine have the Curse of Agony
* Reverted model of Rueful Axe and the Axe of Whiterun due to the lore
* During "First Lessons" the player receives Arcane Ward (Rank I) instead of Arcane Ward (Rank II)
* Fences now buy black market goods and general merchants no longer accept them
* Elemental Fury grants the correct base enchantment
* Elemental Fury enchantments created by the player scale correctly
* Removed various inefficient conditions
* The Greybeard's Robes can no longer be disenchanted
* The Ring of Erudite and Ring of The Beast work according to their descriptions
* Vampiric Talons is no longer active before taking the perk
* All dragon priests correctly inherit their perks from the template
* "Weapon XYZ Specializations" perks are renamed to "Weapon XYZ Focus" to fit within the vanilla UI
* Fast-travel is allowed while riding a dragon
* It's possible to recast Protection from Poison (Rank I)
* Dispel Soul Gems can be equipped in the left hand
* Guard Helmet is properly affected by smithing perks
* All intended racial abilities are active. Most notably Chaurus, Frostbite Spiders, Giants and Mammoths have 60% poison resistance
* Vampires benefit from racial skill rates
* Redguard Vampires benefit from reduced power attack cost
* Imperial vampires benefit from the racial stamina cost reduction for sprinting
* Recruited Blades are no longer afraid of dragons
* Concentrated Poisons and Catalysis check the correct condition
* Akaviri swords no longer have the model of Dragonbane
* Bandit bosses no longer have axes that are tempered too much
* The Curse of Agony can no longer be cheated by equipping several cursed items and then unequipping only one
* Ebony and Glass staffs can be crafted after taking the respective perk
* Spells that are added by items when equipped no longer cause animation issues if the item is unequipped when the spell is still equipped
* Mara's no longer blesses you if you killed her priest during a related quest
* It's no longer possible to equip a circlet under the Hedge Knight Helmet
* The Altmer racial ability no longer boosts standing stones, vampire abilities and other effects it's not supposed to increase
* Vampires no longer have significantly lower unarmed damage than their mortal counterpart
* High Elf vampires have the same unarmed reach than all other races
* The poison spit attack of Frozen Chaurus is stronger
* Tongue's Trance always reduces shout cooldown to zero
* Targeted healing spells check if the caster has the Respite perk not the player
* Heal Self (Rank I), Healing Hands (Rank II) and Healing Ray restore the same amount of stamina as health
* Healing Hands (Rank III) no longer additionally levels Restoration if the player has the Respite perk
* The NPC-exclusive version of Healing Hands (Rank III) now has an area effect
* The NPC-exclusive version of Heal Self (Rank II) only heals the caster
* The NPC-exclusive version of Heal Self (Rank III) cures diseases
* The health and carry weight effects of Transmute Muscles stack with potions
* Absorb Health, Absorb Stamina and Transmute Muscles on Target are correctly displayed as Adept spells in the spell menu
* Absorb Magicka is correctly displayed as Expert spell in the spell menu
* Mistress of the Dark, Phantasmal Miasma, Shadow Sanctuary, Veil of Shadows, Absorb Essence and Summon Unbound Dremora are correctly displayed as Master spells in the spell menu
* Vampiric Drain no longer incorrectly states that it absorbs magicka and stamina. It does absorb magicka and stamina but only if you have the Blood of the Ancients ability and the ability already mentions how the effect works
* Spell tome leveled lists no longer contain duplicate or misplaced spell tomes and missing spell tomes were added
* The enchantments of Wuuthrad, Targe of the Blooded, Mace of Molag Bal, Valdr's Dagger, Windshear and Kyne's Token now work on NPCs
* The Attack Speeed Dummy option in the MCM now works
* All NPC-exclusive spells have a half-cost perk assigned which means they can benefit from the main magic perks
* Empowering Blur, Shadow Shield and Veil of Silence no longer increases magnitude and upkeep cost to insane values
* The casting animation of Mistress of the Dark ends even when the Master Illusion perk is not taken
* A single cast Muffling sphere is correctly dispelled when running
* The Spectral Draugr in Labyrinthian now have all undead traits and drop ectoplasm
* Curing Vampirism now works correctly
* Respite affects Healing Aura
* Unused vanilla spell tomes no longer appear for sale after completing the Illusion ritual quest
* Removed sneak attack extra damage from spells because the feature was incomplete and bugged
* Become Etheral, Clear Skies, Dragonrend, Predator's Might and Slow Time are affected by shout perks
* Guard armor is now named after the hold
* The spell tome for Necromantic Empowerment has a conjuration cover
* Drain Vitality no longer incorrectly states that it "takes away the very lifeforce of your enemies **and gives it to you**.". Drain Vitality simply damages attributes but doesn't absorb them
* It's no longer possible to lure the invisible entity from the Pelagius Wing into the Blue Palace
* It's no longer possible to reanimate invisible entities
* Movement speed modifications take effect immediately
* Oblivion Lore is renamed to Cognitive Flexibility and affects all summons including illusion simulacra
* Mass Effect updates immediately after taking a related perk
* Necromancy now increases the duration of undead summons by 50% instead of 1000%
* The following weapons are no longer missing from blacksmith stores: Long Bow, Iron Dagger, Dwarven Bow, Elven War Axe, Elven Mace, Elven Greatsword, Elven Warhammer, Elven Bow, Orcish Battleaxe and Orcish Warhammer
* The spawn rate of rare (above steel) weapons at blacksmiths is no longer tied to weapon type
* Dualcasting Lightning Speed no longer causes reversed effect
* Ebony Blade can be tempered
* fixed false positives with imbalanced race warning
* Two unused and corrupted meshes have been removed
* Elemental Fury shout affects weapon speed in both hands
* Unequipping armor with a "Shield" enchantment removes the armor's natural armor rating
* Dark Vision is equipped in both hands to prevent the spell from becoming active without upkeep cost
* Stunning Precision works with explosive ammunition

Reqtificator and Installation changes
-------------------------------------

* New functionality: Distribute keywords to armors based on keywords they already have to support more complex condition checks on armors, can be easily extended by 3rd party mods to add their own new features
* Fixed the Reqtificator's handling of certain flags in visual merging
* Racial heights in visual merging are taken from the gameplay data template because height directly affects movementspeed
* Eyebrows correctly merged in visual templates
* Records flagged as "already reqtified" will no longer appear in the SkyProc patch
* Added warning if Crash Fixes or Bug Fixes missing
* Logfile format and location changed with improved pinpointing of troublesome records
* ReqtificatorVault.esp merged into main plugin
* SkyProc's internal consistency file is used instead of ActorVariations.txt
* Unbalanced races are detected by the Reqtificator, which will tell you which checks failed, as well as in-game
* ingame setup checks and installation procedure has been improved
* Smarter detection of Requiem folder
* Semantic versioning prevents save-game updates that won't work
* LOOT master list updated to work better with Requiem
* Correctly detects load orders wth more plugins than Skyrim can handle
* The Bethesda logo is no longer skipped

Compatibility Changes
---------------------

* EditorIDs of many records have been updated to provide a consistent navigation in the plugin
* Tempered items have been removed from outfits because the actual tempering is done via script
* Shock damage deals increased damage against Dwarven Automatons added by other mods as long as they use the proper keyword
* Included Modern Brawl Bug Fix
* Talos blessing is compatible with Paarthurnax Bypass/Dilemma
* Decreased Combat Hit Spell priority
* Script cleanup to simplify navigation for modders
* Update to USLEEP 3.0.11
* Deletion of unused legacy records to make Requiem.esp less cluttered
* A compatibility patch for Even Better Quest Objectives is no longer needed. The improved quest descriptions are carried over to conflicting records
