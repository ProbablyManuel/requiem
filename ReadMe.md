# Requiem - The Roleplaying Overhaul

This repository is the development version of the Skyrim Mod "Requiem - The Roleplaying Overhaul" for versions after
Requiem 4.0.0.
Older Requiem versions have been developed using the Mercurial version control system and the repository for these
versions can be found at https://bitbucket.org/ogerboss/requiem-1.9.4/.
(The history of the old versions has not been imported into the new repository to avoid the usage of sub-repositories.)

This mod is published on NexusMods:
* [Skyrim Legendary Edition](https://www.nexusmods.com/skyrim/mods/19281) (releases before 5.0.0)
* Skyrim Special Edition (future releases from 5.0.0 onwards)

## Requirements

* a Java 13 (or newer) JDK like the [OpenJDK](https://jdk.java.net/)
* Skyrim
* Creation Kit (you're not required to use it for modding, but you need its Papyrus Script Compiler)
* Mod Organizer 2.x

## Setup

1. clone this repository into a new directory inside the `mods` folder of your Mod Organizer installation
2. copy the `userSetupSample.gradle` file in the repo's root directory to `userSetup.gradle`
3. update the copied file to reflect your personal setup
    ```
    ext {
        papyrusCompiler = file("S:\\SteamLibrary\\steamapps\\common\\Skyrim\\Papyrus Compiler\\PapyrusCompiler.exe")
        papyrusCompilerFlags = file("S:\\SteamLibrary\\steamapps\\common\\Skyrim\\Papyrus Compiler\\TESV_Papyrus_Flags.flg")
        papyrusIncludeFolders = files(
                "S:\\MO-Skyrim\\mods\\base-scripts-usleep",
                "S:\\MO-Skyrim\\mods\\SkyUI5-SDK\\scripts\\Source",
                "S:\\MO-Skyrim\\mods\\SKSE-Scripts\\scripts\\Source",
                "S:\\MO-Skyrim\\mods\\base-scripts-Dragonborn",
                "S:\\MO-Skyrim\\mods\\base-scripts-Hearthfire",
                "S:\\MO-Skyrim\\mods\\base-scripts-Dawnguard",
                "S:\\MO-Skyrim\\mods\\base-scripts-Skyrim"
        )
        papyrusFailFast = false
        reqtificatorBuildDir = file("S:\\MO-Skyrim\\mods\\SkyProcBuild")
    }
    ```
   * `papyrusCompiler` - path to your Papyrus compiler
   * `papyrusCompilerFlags` - path to the Papyrus compiler flags file
   * `papyrusIncludeFolders` - list of folders with Papyrus sources to include in the compilation process for
   referencing - scripts are taken from the first folder that contains the given file, the order is therefore important
   to take overwrites of Vanilla scripts by the DLCs or the unofficial patches into account
   * `papyrusFailFast` - if false, the Papyrus script compilation step will only fail at the end of the compilation
   process for all scripts to accumulate error messages
   * `reqtificatorBuildDir` - dedicated directory for the intermediate build files of the Reqtificator (some files can
   have long file names that cause Skyrim to crash on startup, therefore the build intermediates need to be placed
   somewhere where MO does not link them into Skyrim's data directory)
4. open a (power)shell, go to the repository's directory and execute `gradlew.bat installDevVersion` - this may take
some time when you execute it the first time because the wrapper needs to download the Gradle distribution and other
dependencies first
5. enable this repository as a mod in Mod Organizer (and disable any released Requiem version you have active)
6. follow the regular [installation instructions](https://requiem.atlassian.net/wiki/spaces/RS/pages/765394945)

## Important Gradle tasks

* `installDevVersion` - build a playable version of Requiem in this repository
* `cleanDevVersion` - clean up all the build related files
* `packRelease` - assembles a release-ready 7z archive based on the current repository content
* `test` - run the Reqtificator's unit tests
* `ktlintFormat` - enforce formatting for the Kotlin part of the Reqtificator's code base