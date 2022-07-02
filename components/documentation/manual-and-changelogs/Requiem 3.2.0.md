Requiem 3.2.0 - "Design Your Universe"
======================================

The core concepts of gold and ammunition weight are now transparent in the inventory interface and explained in the help menu. Gold weight has been reduced considerably and quest rewards that previously yieled gold and gems have been reverted to pure gold rewards. Various smaller tweaks have been made to weapon stats to make their overall progression more consistent. Potions of Cure Disease and Cure Poison are now available in addition to the powerful Potion of Cleansing.

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
