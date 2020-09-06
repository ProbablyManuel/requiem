import skyrim.requiem.build.ArchiveSevenZTask
import skyrim.requiem.build.FomodInstallerInfoTask
import skyrim.requiem.build.McmTranslationsTask
import skyrim.requiem.build.RequiemVersion
import skyrim.requiem.build.PapyrusCompileTask
import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    kotlin("jvm") version "1.4.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.3.0"
    id("org.beryx.jlink") version "2.21.3"
    idea
}

if (JavaVersion.current() != JavaVersion.VERSION_13) {
    throw ProjectConfigurationException("Java 13 is required to build Requiem", listOf())
}

repositories {
    mavenCentral()
}

try {
    apply("userSetup.gradle")
} catch (ex: Exception) {
    logger.error("The config file 'userSetup.gradle' could not be found.")
    logger.quiet("This file specifies the settings specific to your setup.")
    logger.quiet("Please read the ReadMe file and follow its instructions to")
    logger.quiet("create your own config file with the appropriate paths.")
    throw ex
}

allprojects {
    version = RequiemVersion(4, 0, 1, "Threshold")
}

val gitRevision by extra {
    try {
        val command = ProcessBuilder(listOf("git", "rev-parse", "HEAD"))
        val process = command.start()
        BufferedReader(InputStreamReader(process.inputStream)).readLine()
    } catch (e: Exception) {
        "unknown"
    }
}

fun retrieveGitBranch(): String {
    return try {
        val command = ProcessBuilder(listOf("git", "symbolic-ref", "--short", "-q", "HEAD"))
        val process = command.start()
        BufferedReader(InputStreamReader(process.inputStream)).readLine()
    } catch (e: Exception) {
        "unknown"
    }
}

val gitBranch by extra { retrieveGitBranch() }

val papyrusCompiler: File by project.extra
val papyrusCompilerFlags: File by project.extra
val papyrusFailFast: Boolean by project.extra
val papyrusIncludeFolders: FileCollection by project.extra

val reqtificatorBuildDir: File by project.extra

val reqtificatorDir = file("SkyProc Patchers/Requiem")

val copyReqtificator by tasks.registering(Copy::class) {
    dependsOn("Java:Reqtificator:assemble")
    val outputDir: File by project("Java:Reqtificator").extra
    from (outputDir)
    into(reqtificatorDir)
}

val compilePapyrus = tasks.register<PapyrusCompileTask>("compilePapyrus") {
    description = "Compiles all Papyrus Scripts belonging to Requiem"
    group = "build"

    compiler = papyrusCompiler
    compilerFlags = papyrusCompilerFlags
    failFast = papyrusFailFast
    includeFolders = papyrusIncludeFolders
    sourceFolder = file("Papyrus\\Source\\Requiem")
    outputDir = file("Scripts")
    compilerLogs = file("PapyrusLogs")
}

val copyMcmTranslations = tasks.register<McmTranslationsTask>("copyMcmTranslations") {
    description = "Copy the English MCM strings to other languages to have always have MCM text"
    group = "build"

    templateFile = file("Interface/Translations/Requiem_TEMPLATE.txt")
    version = project.version as RequiemVersion
    placeholder = "{{version}}"
    outputFiles = listOf(
        file("Interface/Translations/Requiem_CZECH.txt"),
        file("Interface/Translations/Requiem_ENGLISH.txt"),
        file("Interface/Translations/Requiem_FRENCH.txt"),
        file("Interface/Translations/Requiem_GERMAN.txt"),
        file("Interface/Translations/Requiem_ITALIAN.txt"),
        file("Interface/Translations/Requiem_JAPANESE.txt"),
        file("Interface/Translations/Requiem_POLISH.txt"),
        file("Interface/Translations/Requiem_RUSSIAN.txt"),
        file("Interface/Translations/Requiem_SPANISH.txt")
    )
}

val fomodInstallerInfo = tasks.register<FomodInstallerInfoTask>("fomodInstallerInfo") {
    description = "set up the Fomod installer info file"
    group = "build"

    templateFile = file("fomod/info_TEMPLATE.xml")
    version = project.version as RequiemVersion
    placeholder = "{{version}}"
    outputFile = file("fomod/info.xml")
}

val cleanPapyrus = tasks.register<Delete>("cleanPapyrus") {
    description = "Delete all compiled Papyrus scripts"
    group = "build"

    delete = setOf("Scripts", "PapyrusLogs")
}

val cleanFomodInstallerInfo = tasks.register<Delete>("cleanFomodInstallerInfo") {
    description = "Delete all generated Fomod installer files"
    group = "build"

    delete = fomodInstallerInfo.map { setOf(it.outputFile) }.get()
}

val cleanMcmTranslations = tasks.register<Delete>("cleanMcmTranslations") {
    description = "delete all MCM text files except the English master version"
    group = "build"

    delete = copyMcmTranslations.map { it.outputFiles.toSet() }.get()
}

tasks.assemble {
    dependsOn(copyReqtificator)
}

tasks.clean {
    delete(reqtificatorDir)
}

//TODO: remove this task once all build aspects are converted to subprojects that allow simple folder-based cleanups
tasks.register("cleanDevVersion") {
    group = "build"
    description = "remove all build results"

    dependsOn(tasks.clean)
    dependsOn(cleanPapyrus)
    dependsOn("Java:Reqtificator:clean")
    dependsOn("Java:SkyProc:clean")
    dependsOn(cleanMcmTranslations)
    dependsOn(cleanFomodInstallerInfo)
}

//TODO: supersede with "assemble" to be more in line with Gradle conventions and have fewer custom tasks
val installDevVersion = tasks.register("installDevVersion") {
    description = "Prepare the Requiem Dev Build for ingame usage"
    group = "application"

    doLast {
        println("Setup ready, you can now start the game!")
    }
    dependsOn(tasks.assemble)
    dependsOn(compilePapyrus)
    dependsOn(copyMcmTranslations)
    dependsOn(fomodInstallerInfo)
}

tasks.register<ArchiveSevenZTask>("packRelease") {
    description = "Pack Requiem as a ready to ship 7z archive"
    group = "distribution"
    dependsOn(installDevVersion)

    archiveFile = file {
        var revision = gitRevision
        val branch = gitBranch

        var archiveName = "Requiem_${branch}_$revision"
        "distribution/$archiveName.7z"
    }

    baseDirectory = rootDir
    fileMapping = mapOf(
        // fomod installer stuff
        file("file:fomod") to file("file:fomod"),
        file("file:options") to file("file:options"),
        // individual files
        file("file:Requiem.esp") to file("file:plugin"),
        file("file:Reqtificator.bat") to file("file:core"),
        // folders
        file("file:documentation") to file("file:core/documentation"),
        file("file:Scripts") to file("file:core/Scripts"),
        file("file:Papyrus/Source") to file("file:core/Scripts/Source"),
        file("file:interface") to file("file:core/interface"),
        file("file:meshes") to file("file:core/meshes"),
        file("file:SkyProc Patchers/Requiem") to file("file:core/SkyProc Patchers/Requiem"),
        file("file:sound") to file("file:core/sound"),
        file("file:textures") to file("file:core/textures")
    )
    excludeFolders = files(
        "file:/Papyrus/Source/Requiem/debugtools",
        "file:/meshes/.hg",
        "file:/textures/.hg",
        "file:/documentation/confluence"
    )
    excludePatterns = listOf(
        "REQ_Debug.+\\.pex",
        "\\.hg.*",
        ".*TEMPLATE.*",
        ".*\\.xcf"
    )
}