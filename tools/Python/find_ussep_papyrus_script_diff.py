import os
import filecmp
import sys
from pathlib import Path


ussep_new = Path(sys.argv[1])
ussep_old = Path(sys.argv[2])
requiem = Path(sys.argv[3])


def get_papyrus_scripts_in_directory(path):
    scripts = {}
    for root, dirs, files in os.walk(path):
        for file in files:
            if file.endswith(".psc"):
                scripts[file.lower()] = os.path.join(root, file)
    return scripts


ussep_old_scripts = get_papyrus_scripts_in_directory(ussep_old)
ussep_new_scripts = get_papyrus_scripts_in_directory(ussep_new)
requiem_scripts = get_papyrus_scripts_in_directory(requiem)

added_or_deleted = (set(ussep_old_scripts.keys()) ^ set(ussep_new_scripts.keys())) & set(requiem_scripts.keys())
maybe_modified = (set(ussep_old_scripts.keys()) & set(ussep_new_scripts.keys())) & set(requiem_scripts.keys())

for script in added_or_deleted:
    print(script)
for script in maybe_modified:
    if not filecmp.cmp(ussep_old_scripts[script], ussep_new_scripts[script], shallow=False):
        print(script)
