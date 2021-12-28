# Requiem - The Roleplaying Overhaul

This repository is the development version of the Skyrim Mod "Requiem - The Roleplaying Overhaul" for versions after
Requiem 4.0.0.
Older Requiem versions have been developed using the Mercurial version control system and the repository for these
versions can be found at https://bitbucket.org/ogerboss/requiem-1.9.4/.
(The history of the old versions has not been imported into the new repository to avoid the usage of sub-repositories.)

This mod is published on NexusMods:

* [Skyrim Legendary Edition](https://www.nexusmods.com/skyrim/mods/19281) (releases before 5.0.0)
* [Skyrim Special Edition](https://www.nexusmods.com/skyrimspecialedition/mods/60888) (future releases from 5.0.0 onwards)

## Requirements

* a Java 13 (or newer) JDK like the [OpenJDK](https://jdk.java.net/)
* .NET 5.0 SDK ([download here](https://dotnet.microsoft.com/download))
* Skyrim Special Edition
* Creation Kit (you're not required to use it for modding, but you need its Papyrus Script Compiler)
* [SSE CreationKit Fixes](https://www.nexusmods.com/skyrimspecialedition/mods/20061) (you're not required to use it for modding, but without it you cannot save a plugin that has a regular plugin as master file)
* [BSArch](https://www.nexusmods.com/newvegas/mods/64745)
* Mod Organizer 2.x

## Setup

1. Clone this repository into a new directory inside the `mods` folder of your Mod Organizer installation.
2. Create a new file `userSetup.gradle` the repository's root directory.
3. Copy the following content into the new file and adjust the parameters to your personal setup.

        ext {
            papyrusCompiler = file("S:\\SteamLibrary\\steamapps\\common\\Skyrim Special Edition\\Papyrus Compiler\\PapyrusCompiler.exe")
            papyrusCompilerFlags = file("S:\\SteamLibrary\\steamapps\\common\\Skyrim Special Edition\\Papyrus Compiler\\TESV_Papyrus_Flags.flg")
            papyrusIncludeFolders = files(
                    "S:\\MO-Skyrim\\mods\\USSEP\\Scripts\\Source",
                    "S:\\MO-Skyrim\\mods\\SkyUI5-SDK\\Scripts\\Source",
                    "S:\\MO-Skyrim\\mods\\SKSE64\\Scripts\\Source",
                    "S:\\MO-Skyrim\\mods\\SSE-scripts\\Source\\Scripts"
            )
            papyrusFailFast = false
            csharpWarningsAsErrors = true
            bsArch = file("S:\\bsarch.exe")
        }

    * `papyrusCompiler` - path to your Papyrus compiler
    * `papyrusCompilerFlags` - path to the Papyrus compiler flags file
        * You can find this file in the `Scripts.zip` archive that is part of the Creation Kit installation.
    * `papyrusIncludeFolders` - list of folders with Papyrus sources to include in the compilation process
        * The scripts from the include folders will not be compiled themselves, but used by the compiler to verify
        that the function calls and other external references in the scripts to be compiled are valid.
        * The scripts are taken from the first folder that contains the given file.
        The order is therefore important to select updated versions the unofficial patch over the vanilla scripts.
        * Script folders should point to the .psc script source files. They are:
            1. USSEP base-scripts, from the `Scripts\Source` directory in the download
            (Oldrim version requires USLEEP; use the `Scripts\Source` directory from the unpacked BSA)
            2. SKY-UI base scripts, from the `Scripts\Source` directory in the download
            3. SKSE scripts, from the `Data\Scripts\Source` directory in the download
            4. Skyrim base scripts, all from the `Source\Scripts` directory inside `Data\scripts.zip` which comes with Skyrim.
    * `papyrusFailFast` - failure handling mode for the Papyrus compilation step
        * If set to true, the compilation fails upon encountering the first error.
        * If set to false, the compilation continues after the first error to accumulate all error messages.
    * `csharpWarningsAsErrors` - handling of compile time warnings in the C# Reqtificator.
        * If set to true, code analysis warnings will cause the Reqtificator build task to fail
        * If set to false, code analysis warnings will be emitted on the console, but the build will still succeed.
        This setting only applies to the `assemble` task, `checkFormat` will always fail if a code-analyser warning is
        present.
    * `bsArch` - the full path to the executable from BSArch

4. Open a (power)shell, go to the repository's directory and execute `gradlew.bat installDevVersion`. This may take
some time when you execute it the first time because the wrapper needs to download the Gradle distribution and other
dependencies first.
5. Enable this repository as a mod in Mod Organizer and disable any other Requiem version you may have installed.
6. Follow the regular [installation instructions](https://requiem.atlassian.net/wiki/spaces/RS/pages/765394945) to set
up the Reqtificator in Mod Organizer.

## Important Gradle tasks

* `assemble` - build a playable version of Requiem in this repository
* `clean` - clean up all the build related files
* `packRelease` - assembles a release-ready 7z archive based on the current repository content
* `test` - run the Reqtificator's unit tests
* `checkFormat` - verify formatting and code analyzer rules compliance of the C# Reqtificator code base
* `format` - apply formatting rules and automated code-analyzer issue fixes for the C# Reqtificator code base