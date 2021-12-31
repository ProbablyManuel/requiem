import skyrim.requiem.build.ReleaseArchiveTask
import skyrim.requiem.build.BsaPackingTask
import skyrim.requiem.build.RequiemVersion
import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    base
}

if (JavaVersion.current() != JavaVersion.VERSION_13) {
    throw ProjectConfigurationException("Java 13 is required to build Requiem", listOf())
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
    version = RequiemVersion(5, 0, 1, "From the Ashes")
}

fun runCommand(command: List<String>): String = try {
    val process = ProcessBuilder(command).start()
    BufferedReader(InputStreamReader(process.inputStream)).readLine()
} catch (e: Exception) {
    "unknown"
}

val gitRevision by extra { runCommand(listOf("git", "rev-parse", "HEAD")) }
val gitBranch by extra { runCommand(listOf("git", "symbolic-ref", "--short", "-q", "HEAD")) }

val bsArch: File by extra

val interfaceDir = file("Interface")
val scriptsDir = file("Scripts")
val sourceDir = file("Source")
val reqtificatorDir = file("Reqtificator")
val scriptsSourcesDir = file(sourceDir.resolve("Scripts"))
val reqtificatorSourcesDir = file(sourceDir.resolve("Reqtificator"))
val bsaFilesDir = file("BsaFiles")
val bsaFile = file("Requiem.bsa")

val copyReqtificator by tasks.registering(Copy::class) {
    dependsOn("components:mutagen-reqtificator:assemble")
    val outputDir: File by project("components:mutagen-reqtificator").extra
    from(outputDir)
    into(reqtificatorDir)

    exclude("sources")
}

val copyScripts by tasks.registering(Copy::class) {
    dependsOn("components:papyrus-scripts:assemble")
    val outputDir: File by project("components:papyrus-scripts").extra
    from(outputDir)
    into(scriptsDir)
    exclude("**/*.psc")
}

val copyScriptSources by tasks.registering(Copy::class) {
    dependsOn("components:papyrus-scripts:assemble")

    val outputDir: File by project("components:papyrus-scripts").extra
    from(outputDir.resolve("source"))
    into(scriptsSourcesDir)
    exclude("**/*.pex")
    eachFile {
        path = name
    }
    includeEmptyDirs = false
}

val copyReqtificatorSources by tasks.registering(Copy::class) {
    description = "copy Reqtificator source code for inclusion in release archive"
    group = "build"
    dependsOn("components:mutagen-reqtificator:assemble")

    val outputDir: File by project("components:mutagen-reqtificator").extra
    from(outputDir.resolve("sources"))
    into(reqtificatorSourcesDir)
    includeEmptyDirs = false
}

val copyInterfaceFiles by tasks.registering(Copy::class) {
    dependsOn("components:interface:assemble")
    val outputDir: File by project("components:interface").extra
    from(outputDir)
    into(interfaceDir)
}

val copyBsaFiles by tasks.registering(Copy::class) {
    dependsOn("assemble")

    from(".")
    include("Interface/**", "meshes/**", "Sound/**", "textures/**", "Scripts/**")
    into(bsaFilesDir)
    exclude("**/REQ_Debug*.pex", "**/REQ_Debug*.psc")
    includeEmptyDirs = false
}

val createBsa by tasks.registering(BsaPackingTask::class) {
    description = "create a BSA archive for Requiem's core assets"
    group = "distribution"
    dependsOn(copyBsaFiles)

    folder = bsaFilesDir
    archiveFile = bsaFile
    logFile = file("distribution/bsaLog.txt")
    archiveTool = bsArch
}

tasks.assemble {
    dependsOn(copyReqtificator)
    dependsOn(copyInterfaceFiles)
    dependsOn(copyScripts)
    dependsOn(copyScriptSources)
    dependsOn(copyReqtificatorSources)
}

tasks.clean {
    delete(sourceDir)
    delete(reqtificatorDir)
    delete(interfaceDir)
    delete(scriptsDir)
    delete(bsaFilesDir)
    delete(bsaFile)
}

val packRelease by tasks.registering(ReleaseArchiveTask::class) {
    description = "Pack Requiem as a ready to ship 7z archive"
    group = "distribution"

    dependsOn(tasks.assemble)
    dependsOn(createBsa)
    dependsOn("components:fomod-installer:assemble")
    dependsOn("components:documentation:assemble")

    archiveFile = file ("distribution/Requiem_${gitBranch}_$gitRevision.7z")

    val installerDir: File by project("components:fomod-installer").extra
    val releaseDocsDir: File by project("components:documentation").extra

    fomod = installerDir
    plugin = file("Requiem.esp")
    coreMod = files(
        "Reqtificator.bat",
        "Requiem.modgroups",
        releaseDocsDir,
        "Requiem.bsa",
        "Source",
        "Reqtificator",
        "BashTags"
    )
    excludePatterns = listOf()
}