Requiem 3.0.2 - "Consign To Oblivion" Bugfix Pack #2
====================================================

This update address a variety of bugs found in 3.0.0. Most importantly, it fixes the display of derived attributes features in the MCM menu and gives more meaningful protection to Dragonpriests.

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
