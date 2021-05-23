import skyrim.requiem.build.CompileCSharpTask
import skyrim.requiem.build.TestCSharpTask
import skyrim.requiem.build.PublishCSharpTask

plugins {
    base
}

buildDir = file("./bin")
val projectDirs = file(".").listFiles().filter { it.isDirectory && it.listFiles().any { f -> f.extension == "csproj" } }
val objectDirs = files(projectDirs.map { "$it/obj" })
val compileDirs = files(projectDirs.map { "$it/bin" })

val outputDir by project.extra(file("$buildDir/Reqtificator-SSE"))


val compile by tasks.registering(CompileCSharpTask::class) {
    description = "Compiles the C# code for the SSE Reqtificator"
    group = "build"

    solutionFolder = file(".")
    projectName = "Reqtificator"
}

val testCompile by tasks.registering(CompileCSharpTask::class) {
    description = "Compiles the C# test code for the SSE Reqtificator"
    group = "build"

    solutionFolder = file(".")
    projectName = "ReqtificatorTest"
}

val publish by tasks.registering(PublishCSharpTask::class) {
    description = "Compiles the C# code for the SSE Reqtificator"
    group = "build"

    solutionFolder = file(".")
    projectName = "Reqtificator"
    targetDirectory = file("$outputDir/app")
    warningsAsErrors = true
}

val test by tasks.registering(TestCSharpTask::class) {
    description = "Run C# unit tests for the SSE Reqtificator"
    group = "verification"

    solutionFolder = file(".")

    dependsOn(compile, testCompile)
}

tasks.assemble {
    dependsOn(publish)
}

tasks.clean {
    delete(compileDirs, objectDirs)
}