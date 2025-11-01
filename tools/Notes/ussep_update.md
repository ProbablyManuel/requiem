# How to forward changes in the plugin

1. Load the new USSEP in xEdit.
1. Create a delta patch targeting the new USSEP.
1. Load the old USSEP, the delta patch, and Requiem in xEdit.
1. Make sure mod groups are disabled.
1. Apply the script "REQ_UssepDiff" to Requiem.
1. Now you have a nice view over the relevant records from the old and new USSEP.

# How to forward changes in the scripts

You can use [find_ussep_papyrus_script_diff.py](../Python/find_ussep_papryrus_script_diff.py) to identify scripts used by Requiem that have been changed between the old and new USSEP version.

```shell
python find_ussep_papyrus_script_diff.py "C:\Skyrim Tools\Mod Organizer SSE\mods\Unofficial Skyrim Special Edition Patch" "C:\Skyrim Tools\Mod Organizer SSE\mods\Unofficial Skyrim Special Edition Patch-266-4-3-2-1721451025" "C:\Skyrim Tools\Mod Organizer SSE\mods\requiem\components\papyrus-scripts\src"
```
