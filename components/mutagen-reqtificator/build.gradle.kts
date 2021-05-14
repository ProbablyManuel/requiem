import skyrim.requiem.build.CompileCSharpTask

plugins {
    base
}

buildDir = file("./bin")
//TODO: handle cleanup in a nicer way than hardcoding project paths
val projectDirs = files("./Reqtificator", "./ReqtificatorTest")
val objectDirs = files(projectDirs.map { "$it/obj" })
val compileDirs = files(projectDirs.map { "$it/bin" })


val outputDir by project.extra(file("$buildDir/Reqtificator-SSE"))


val compileCSharp by tasks.registering(CompileCSharpTask::class) {
    description = "Compiles the SSE Reqtificator based on Mutagen"
    group = "build"

    compilerLog = file("$buildDir/buildlog.txt")
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