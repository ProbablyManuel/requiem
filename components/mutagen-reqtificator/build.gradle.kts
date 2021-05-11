import skyrim.requiem.build.CompileCSharpTask

plugins {
    base
}

buildDir = file("./bin")
val compileDir = file("./obj")

val outputDir by project.extra(file("$buildDir/Reqtificator-SSE"))


val compileCSharp by tasks.registering(CompileCSharpTask::class) {
    description = "Compiles the SSE Reqtificator based on Mutagen"
    group = "build"

    compilerLog = file("$buildDir/buildlog.txt")
    projectFolder = file(".")
    targetDirectory = outputDir
}

tasks.assemble {
    dependsOn(compileCSharp)
}

tasks.clean {
    delete(compileDir)
}