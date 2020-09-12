import skyrim.requiem.build.PapyrusCompileTask
import skyrim.requiem.build.RequiemVersion

plugins {
    base
}

val papyrusCompiler: File by rootProject.extra
val papyrusCompilerFlags: File by rootProject.extra
val papyrusFailFast: Boolean by rootProject.extra
val papyrusIncludeFolders: FileCollection by rootProject.extra

val outputDir by project.extra(file("$buildDir/output"))

val compilePapyrus by tasks.registering(PapyrusCompileTask::class) {
    description = "Compiles all Papyrus Scripts belonging to Requiem"
    group = "build"

    compiler = papyrusCompiler
    compilerFlags = papyrusCompilerFlags
    failFast = papyrusFailFast
    includeFolders = papyrusIncludeFolders
    sourceFolder = file("src/requiem")
    targetDir = file("$outputDir")
    compilerLogs = file("$buildDir/logs")
}

val copySourceFiles by tasks.registering(Copy::class) {
    from("src")
    exclude("**/debugtools/**")
    into("$outputDir/source")
}

tasks.assemble {
    dependsOn(compilePapyrus)
    dependsOn(copySourceFiles)
}