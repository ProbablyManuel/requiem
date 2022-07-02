Requiem 3.1.0 - "Steel meets Steel"
===================================

The armor penetration system works more like the one from the 1.9.x versions, but keeps the new features like improved transparency and more distinct damage types for different weapons. Worn gear can now be swapped freely during combat. Various unused or rarely used assets have been removed from the distribution.

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
    * Vagabond Armor from Immersive Armors – Alain Dufont wears an enchanted scaled armor instead of a unique Vagabond armor.
    * Stormlord Armor – Ulfric got back his original, unique gear from the base game as heavy armor set and some additional heavy armor perks.
    * Ebony Plate armor with hold insigina emblazoned tunic – Irileth wears an enchanted set of ebony armor instead of an ebony plate armor.
    * Orichalcum Plate armor with hold insigina emblazoned tunic – Commander Caius wears a standard Whiterun guard plate armor instead of an Orichalcum plate armor.
    * Golden Elven Armor and Weapons – Elenwen has been reverted to hear Vanilla gear, a Thalmor robe and an elven dagger.
    
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
