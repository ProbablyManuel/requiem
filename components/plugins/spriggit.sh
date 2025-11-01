#/bin/bash

set -euo pipefail

Spriggit.CLI convert-from-plugin --InputPath "$1" --OutputPath "components/plugins/$1"
