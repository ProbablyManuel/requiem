Requiem 5.2.0 - "The Gathering Storm"
=====================================

Numerous simple and effective gameplay tweaks to alleviate longstanding issues.

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
* Transmute Muscles has base magnitude 30 (was 20), but dual casting doesn't affect magnitude. This was the only spell in the game that scaled both in magnitude and duration when dual cast.
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
* All Mass Effect calculations are performed inside the script `REQ_MassEffectArmor` attached to `xx82CC14 <\REQ\_Ability\_MassEffect\>`.
* The workaround to force movement speed updates by modifying inventory weight is removed in favor of Bug Fixes SSE.
* A hidden, constant effect, self-targeted value modifier MGEF is offered for every (relevant) actorvalue, as well as a variant with a visible description. These are generic magic effects for use by abilities.
* Astrid has the Mutagen perks instead of the abilities. 
