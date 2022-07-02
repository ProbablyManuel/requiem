Requiem 5.0.3 - "From the Ashes" Bugfix Pack #3
===============================================

This small release addresses more common installation problems and improves compatibility with some mods.

Bugfixes
--------

* Avoid division by zero when disabling the innate running speed penalty. This resolves the camera spinning around very quickly when using the mod True Directional Movement and similar unexpected bugs.
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
