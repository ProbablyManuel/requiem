Requiem 3.4.0 - "The Shadow Theory"
===================================

Invisible Entities have been reworked from scratch and are now known as the _Slighted_. They feature proper visualization and also have a backstory that connects the various places where they appear.

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
