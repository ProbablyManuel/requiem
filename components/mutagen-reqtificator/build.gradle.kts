import skyrim.requiem.build.*

plugins {
    base
}

buildDir = file("./bin")
val projectDirs = file(".").listFiles().filter { it.isDirectory && it.listFiles().any { f -> f.extension == "csproj" } }
val objectDirs = files(projectDirs.map { "$it/obj" })
val compileDirs = files(projectDirs.map { "$it/bin" })

val csharpWarningsAsErrors: Boolean = rootProject.findProperty("csharpWarningsAsErrors") as Boolean? ?: false

val outputDir by project.extra(file("$buildDir/Reqtificator-SSE"))


val compile by tasks.registering(CompileCSharpTask::class) {
    description = "Compiles the C# code for the SSE Reqtificator"
    group = "build"

    solutionFolder = file(".")
    projectName = "Reqtificator"

    dependsOn(restoreTools)
}

val testCompile by tasks.registering(CompileCSharpTask::class) {
    description = "Compiles the C# test code for the SSE Reqtificator"
    group = "build"

    solutionFolder = file(".")
    projectName = "ReqtificatorTest"

    dependsOn(restoreTools)
}

val publish by tasks.registering(PublishCSharpTask::class) {
    description = "Compiles the C# code for the SSE Reqtificator"
    group = "build"

    solutionFolder = file(".")
    projectName = "Reqtificator"
    targetDirectory = file("$outputDir/app")
    warningsAsErrors = csharpWarningsAsErrors

    dependsOn(restoreTools)
}

val test by tasks.registering(TestCSharpTask::class) {
    description = "Run C# unit tests for the SSE Reqtificator"
    group = "verification"

    solutionFolder = file(".")

    dependsOn(compile, testCompile, restoreTools)
}

val checkFormat by tasks.registering(CheckFormatCSharpTask::class) {
    description = "Run C# unit tests for the SSE Reqtificator"
    group = "verification"

    solutionFolder = file(".")

    dependsOn(restoreTools)
}

val format by tasks.registering(FormatCSharpTask::class) {
    description = "Fix formatting issues and analyzer complaints automatically when possible"
    group = "verification"

    solutionFolder = file(".")

    dependsOn(restoreTools)
}

val restoreTools by tasks.registering(RestoreDotnetToolsTask::class) {
    description = "Loads the desired version of Dotnet build tool extensions requried by other tasks"
    group = "build"

    solutionFolder = file(".")
    manifestFile = file(".config/dotnet-tools.json")
}

tasks.assemble {
    dependsOn(publish)
}

tasks.clean {
    delete(compileDirs, objectDirs)
}