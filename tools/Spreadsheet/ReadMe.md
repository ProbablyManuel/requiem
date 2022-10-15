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
* _Secondary_ denotes the secondary ingredients of the crafting recipe. If unavailable, this set uses no secondary ingredients.
* _Leather_ denotes the leather ingredient of the crafting recipe. If unavailable, this set cannot be crafted.
* _Tempering_ denotes the ingredient of the tempering recipe. If unavailable, the primary ingredient is used as tempering ingredient.
* _Perk_ denotes the perk of the crafting and tempering recipe. If unavailable, the set is non-playable.

How the armor rating, weight, and price of a full set are distributed across the armor parts is defined in [armor_patcher.py](../xEdit%20Scripts/armor_patcher.py).

The coloring of some fields is a just visual cue to group armors with identical stats.

Extras
------

* The first column denotes the template (`<type>_<set>_<slot>`) and modifier of the extra armor item.
* _Armor Rating_ denotes the armor rating offset to the template.
* _Weight_ denotes the weight offset to the template.
* _Gold_ denotes the price offset to the template.

CraftingQuantities
------------------

* The first column denotes the armor part.
* _Primary_ denotes the number of primary ingredients in the crafting recipe of this armor part.
* _Secondary_ denotes the number of secondary ingredients in the crafting recipe of this armor part.
* _Leather_ denotes the number of leather ingredients in the crafting recipe of this armor part.

The tempering recipe always uses one ingredient.

Artifacts
---------

* The first column denotes the name of the artifact.
* _Base_ denotes the template (`<type>_<set>_<slot>`) of the artifact.
* _Armor Rating_ denotes the armor rating offset to the template.
* _Weight_ denotes the weight offset to the template.
* _Gold_ denotes the price of the artifact.
* _Divine_ denotes whether tempering the artifact requires Legendary Blacksmithing.

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
* _Secondary_ denotes the secondary ingredients of the crafting recipe. If unavailable, this set uses no secondary ingredients.
* _Tempering_ denotes the ingredient of the tempering recipe. If unavailable, the primary ingredient is used as tempering ingredient.
* _Perk_ denotes the perk of the crafting and tempering recipe. If unavailable, the set is non-playable.

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
* _Primary_ denotes the number of primary ingredients in the crafting recipe of this weapon type.
* _Secondary_ denotes the number of secondary ingredients in the crafting recipe of this weapon type.
* _Leather_ denotes the number of leather stripes in the crafting recipe of this weapon type.

The tempering recipe always uses one ingredient.

Artifacts
---------

* The first column denotes the name of the artifact.
* _Damage_ denotes the damage offset to the template.
* _Weight_ denotes the weight offset to the template.
* _Gold_ denotes the price of the artifact.
* _Speed_ denotes the speed offset to the template.
* _Reach_ denotes the reach offset to the template.
* _Stagger_ denotes the stagger offset to the template.
* _Divine_ denotes whether tempering the artifact requires Legendary Blacksmithing.
