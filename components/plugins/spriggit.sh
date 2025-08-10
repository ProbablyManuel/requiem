#/bin/bash

set -euo pipefail

tmp_dir="$(mktemp -d)"
mkdir -p "${tmp_dir}"
cp "$1" "${tmp_dir}/$1"
Spriggit.CLI sort-randomized-fields --InputPath "${tmp_dir}/$1" --OutputPath "${tmp_dir}/$1" --GameRelease SkyrimSE
Spriggit.CLI convert-from-plugin --InputPath "${tmp_dir}/$1" --OutputPath "components/plugins/$1"
rm -rf "${tmp_dir}"
