Requiem 3.1.1 - "Steel meets Steel" Bugfix Pack #1
==================================================

The Reqtificator handles plugins with incorrect inheritance data for actors more gracefully. Potion names have been changed to restore the natural inventory order. A few minor edits to quests have been removed to improve compatibility with other mods.

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
