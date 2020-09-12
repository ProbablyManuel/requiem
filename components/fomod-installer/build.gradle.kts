import skyrim.requiem.build.FomodInstallerInfoTask
import skyrim.requiem.build.RequiemVersion

plugins {
    base
}

val outputDir by project.extra(file("$buildDir/output"))
val installerDir by project.extra(file("$outputDir/fomod"))

val generatedFiles = file("$buildDir/generated")

val generateFomodInstallerInfo by tasks.registering(FomodInstallerInfoTask::class) {
    description = "set up the Fomod installer info file"
    group = "build"

    templateFile = file("info_TEMPLATE.xml")
    version = project.version as RequiemVersion
    placeholder = "{{version}}"
    outputFile = file("$generatedFiles/info.xml")
}

val copyinstallerFiles by tasks.registering(Copy::class) {
    dependsOn(generateFomodInstallerInfo)

    from("files")
    from(generatedFiles)
    into(installerDir)
}

val copyinstallerImages by tasks.registering(Copy::class) {
    from("images")
    into("$installerDir/images")
}

val copyOptionFiles by tasks.registering(Copy::class) {
    from("options")
    into("$installerDir/options")
}

tasks.assemble {
    dependsOn(copyinstallerFiles)
    dependsOn(copyinstallerImages)
    dependsOn(copyOptionFiles)
}