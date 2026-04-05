Armor
=====

Each subsection details the content of a worksheet in `Armor.xlsx`.

Armors
------

* The first column denotes the armor type and set.
* _Armor Rating_ denotes the armor rating of a full set: Head, Body, Hands, Feet.
* _Weight_ denotes the weight of a full set: Head, Body, Hands, Feet.
* _Gold_ denotes the price of a full set: Head, Body, Hands, Feet.
* _Primary_ denotes the primary ingredient of the crafting recipe. If unavailable, this set cannot be crafted.
* _Secondary_ denotes the secondary ingredient of the crafting recipe. If unavailable, this set has no secondary ingredient.
* _Leather_ denotes the leather ingredient of the crafting recipe. If unavailable, this set cannot be crafted.
* _Temper_ denotes the ingredient of the tempering recipe. If unavailable, the primary ingredient is used as tempering ingredient.
* _Breakdown_ denotes the ingredient of the breakdown recipe. If unavailable, this set cannot be broken down.
* _Perk_ denotes the perk of the crafting and tempering recipe. If unavailable, the set cannot be crafted or tempered.

How the armor rating, weight, and price of a full set are distributed across the armor parts is defined in [armor_patcher.py](../armor_patcher.py).

The coloring of some fields is a just visual cue to group armors with identical stats.

Extras
------

"Extras" are armor items that slightly deviate from the standard edition, e.g. Imperial Studded Armor.

* The first column denotes the template (`<type>_<set>_<slot>`) and modifier of the extra.
* _Armor Rating_ denotes the armor rating offset to the template.
* _Weight_ denotes the weight offset to the template.
* _Gold_ denotes the price offset to the template.

CraftingQuantities
------------------

* The first column denotes the armor part.
* _Primary_ denotes the number of primary ingredients in a crafting recipe of this armor part.
* _Secondary_ denotes the number of secondary ingredients in a crafting recipe of this armor part.
* _Leather_ denotes the number of leather ingredients in a crafting recipe of this armor part.

The tempering recipe always uses one ingredient.

Artifacts
---------

* The first column denotes the name of the artifact.
* _Base_ denotes the template (`<type>_<set>_<slot>`) of the artifact.
* _Armor Rating_ denotes the armor rating offset to the template.
* _Weight_ denotes the weight offset to the template.
* _Gold_ denotes the price of the artifact.
* _Divine_ denotes whether tempering the artifact requires Legendary Blacksmithing.

Patches
-------

* The first column denotes the armor type and set of the third-party armor.
* _Base_ denotes the template (`<type>_<set>`) of the third-party armor.
* _Armor Rating_ denotes the armor rating offset to the template.
* _Weight_ denotes the weight offset to the template.
* _Gold_ denotes the price offset to the artifact.

Weapon
======

Each subsection details the content of a worksheet in `Weapon.xlsx`.

Weapons
-------

* The first column denotes the weapon set.
* _Damage_ denotes the damage offset to the reference value in the "Stats" worksheet.
* _Weight_ denotes the weight offset to the reference value in the "Stats" worksheet.
* _Gold_ denotes the price factor to the reference value in the "Stats" worksheet.
* _Primary_ denotes the primary ingredient of the crafting recipe. If unavailable, this set cannot be crafted.
* _Secondary_ denotes the secondary ingredient of the crafting recipe. If unavailable, this set has no secondary ingredient.
* _Temper_ denotes the ingredient of the tempering recipe. If unavailable, the primary ingredient is used as tempering ingredient.
* _Breakdown_ denotes the ingredient of the breakdown recipe. If unavailable, this set cannot be broken down.
* _Perk_ denotes the perk of the crafting and tempering recipe. If unavailable, the set cannot be crafted or tempered.

Stats
-----

* The first column denotes the weapon type.
* _Damage_ denotes the reference damage of this weapon type.
* _Weight_ denotes the reference weight of this weapon type.
* _Gold_ denotes the reference price of this weapon type.
* _Speed_ denotes the reference speed of this weapon type.
* _Reach_ denotes the reference reach of this weapon type.
* _Stagger_ denotes the reference stagger of this weapon type.

CraftingQuantities
------------------

* The first column denotes the weapon type.
* _Primary_ denotes the number of primary ingredients in a crafting recipe of this weapon type.
* _Secondary_ denotes the number of secondary ingredients in a crafting recipe of this weapon type.
* _Leather_ denotes the number of leather stripes in a crafting recipe of this weapon type.

The tempering recipe always uses one ingredient.

Artifacts
---------

* The first column denotes the name of the artifact.
* _Base_ denotes the template (`<set>_<type>`) of the artifact.
* _Damage_ denotes the damage offset to the template.
* _Weight_ denotes the weight offset to the template.
* _Gold_ denotes the price of the artifact.
* _Speed_ denotes the speed offset to the template.
* _Reach_ denotes the reach offset to the template.
* _Stagger_ denotes the stagger offset to the template.
* _Divine_ denotes whether tempering the artifact requires Legendary Blacksmithing.

Patches
-------

* The first column denotes the name of the third-party weapon.
* _Base_ denotes the template (`<set>_<type>`) of the third-party weapon.
* _Damage_ denotes the damage offset to the template.
* _Weight_ denotes the weight offset to the template.
* _Gold_ denotes the price multiplier to the template.
* _Speed_ denotes the speed offset to the template.
* _Reach_ denotes the reach offset to the template.
* _Stagger_ denotes the stagger offset to the template.

Leveled Lists
=============

Each object designates a leveled list pattern. The name denotes the EditorId of the leveled list where `{item_slot}` is a placeholder for the armor part or weapon type. The value is another object that maps an armor/weapon set to its number of entries in the leveled list.

Spell Scaling
=============

* The first column denotes the spell.
* _Power Affects Magnitude_ denotes the value of the eponymous magic effect flag.
* _Power Affects Duration_ denotes the value of the eponymous magic effect flag.
* _No Magnitude_ denotes the value of the eponymous magic effect flag.
* _No Duration_ denotes the value of the eponymous magic effect flag.
* _No Magnitude Scaling_ denotes the presence of the REQ_NoMagnitudeScaling keyword.
* _No Duration Scaling_ denotes the presence of the REQ_NoDurationScaling keyword.
* _No Dual Cast Modification_ denotes the value of the eponymous spell flag.

The other three columns are sanity checks.
