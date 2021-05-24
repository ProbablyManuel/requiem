import skyrim.requiem.build.CompileCSharpTask

plugins {
    base
}

buildDir = file("./bin")
val projectDirs = file(".").listFiles().filter { it.isDirectory && it.listFiles().any { f -> f.extension == "csproj" } }
val objectDirs = files(projectDirs.map { "$it/obj" })
val compileDirs = files(projectDirs.map { "$it/bin" })

val outputDir by project.extra(file("$buildDir/Reqtificator-SSE"))


val compileCSharp by tasks.registering(CompileCSharpTask::class) {
    description = "Compiles the SSE Reqtificator based on Mutagen"
    group = "build"

    solutionFolder = file(".")
    projectName = "Reqtificator"
    targetDirectory = file("$outputDir/app")
}

tasks.assemble {
    dependsOn(compileCSharp)
}

tasks.clean {
    delete(compileDirs, objectDirs)
}