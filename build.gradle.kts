import skyrim.requiem.build.ArchiveSevenZTask
import skyrim.requiem.build.FomodInstallerInfoTask
import skyrim.requiem.build.RequiemVersion
import skyrim.requiem.build.PapyrusCompileTask
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

val skyProcDir = file("SkyProc Patchers")
val reqtificatorDir = file("$skyProcDir/Requiem")
val interfaceDir = file("Interface")
val scriptsDir = file("Scripts")

val copyReqtificator by tasks.registering(Copy::class) {
    dependsOn("Java:Reqtificator:assemble")
    val outputDir: File by project("Java:Reqtificator").extra
    from(outputDir)
    into(reqtificatorDir)
}

val copyScripts by tasks.registering(Copy::class) {
    dependsOn("components:papyrus-scripts:assemble")
    val outputDir: File by project("components:papyrus-scripts").extra
    from(outputDir)
    into(scriptsDir)
}

val copyInterfaceFiles by tasks.registering(Copy::class) {
    dependsOn("components:interface:assemble")
    val outputDir: File by project("components:interface").extra
    from(outputDir)
    into(interfaceDir)
}

tasks.assemble {
    dependsOn(copyReqtificator)
    dependsOn(copyInterfaceFiles)
    dependsOn(copyScripts)
}

tasks.clean {
    delete(reqtificatorDir)
    delete(interfaceDir)
    delete(scriptsDir)
    delete(skyProcDir)
}

//TODO: remove this task once all build aspects are converted to subprojects that allow simple folder-based cleanups
tasks.register("cleanDevVersion") {
    group = "build"
    description = "remove all build results"

    dependsOn(tasks.clean)
    dependsOn("components:papyrus-scripts:clean")
    dependsOn("Java:Reqtificator:clean")
    dependsOn("Java:SkyProc:clean")
    dependsOn("components:interface:clean")
    dependsOn("components:fomod-installer:clean")
    dependsOn("components:documentation:clean")
}

//TODO: supersede with "assemble" to be more in line with Gradle conventions and have fewer custom tasks
val installDevVersion = tasks.register("installDevVersion") {
    description = "Prepare the Requiem Dev Build for ingame usage"
    group = "application"

    doLast {
        println("Setup ready, you can now start the game!")
    }
    dependsOn(tasks.assemble)
}

tasks.register<ArchiveSevenZTask>("packRelease") {
    description = "Pack Requiem as a ready to ship 7z archive"
    group = "distribution"
    dependsOn(installDevVersion)
    dependsOn("components:fomod-installer:assemble")
    dependsOn("components:documentation:assemble")

    archiveFile = file {
        val revision = gitRevision
        val branch = gitBranch

        val archiveName = "Requiem_${branch}_$revision"
        "distribution/$archiveName.7z"
    }

    val installerDir: File by project("components:fomod-installer").extra
    val optionsDir: File by project("components:fomod-installer").extra
    val releaseDocsDir: File by project("components:documentation").extra

    baseDirectory = rootDir
    fileMapping = mapOf(
        // fomod installer stuff
        installerDir to file("file:fomod"),
        optionsDir to file("file:options"),
        // individual files
        file("file:Requiem.esp") to file("file:plugin"),
        file("file:Reqtificator.bat") to file("file:core"),
        // folders
        releaseDocsDir to file("file:core/documentation"),
        file("file:Scripts") to file("file:core/Scripts"),
        file("file:interface") to file("file:core/interface"),
        file("file:meshes") to file("file:core/meshes"),
        file("file:SkyProc Patchers/Requiem") to file("file:core/SkyProc Patchers/Requiem"),
        file("file:sound") to file("file:core/sound"),
        file("file:textures") to file("file:core/textures")
    )
    excludeFolders = files()
    excludePatterns = listOf("REQ_Debug.+\\.pex")
}