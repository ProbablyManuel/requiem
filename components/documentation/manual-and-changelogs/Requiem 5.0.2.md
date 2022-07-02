Requiem 5.0.2 - "From the Ashes" Bugfix Pack #2
===============================================

A small collection of bug fixes to address more common installation problems we have seen. Most notably, the Reqtificator now terminates with a proper error message instead of hanging on the save export if a problem is encountered.

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
