import skyrim.requiem.build.McmTranslationsTask
import skyrim.requiem.build.RequiemVersion

plugins {
    base
}

val outputDir by project.extra(file("$buildDir/output"))

val generateMcmTranslations by tasks.registering(McmTranslationsTask::class) {
    description = "generate MCM files for all languages supported by Skyrim"
    group = "build"

    templateFile = file("mcm-translations/Requiem_TEMPLATE.txt")
    version = project.version as RequiemVersion
    placeholder = "{{version}}"
    languages = listOf("CZECH", "ENGLISH", "FRENCH", "GERMAN", "ITALIAN", "JAPANESE", "POLISH", "RUSSIAN", "SPANISH")
    outputFolder = file("$outputDir/translations")
}

val copyMcmImages by tasks.registering(Copy::class) {
    from("mcm-images")
    include("*.dds")
    into("$outputDir/requiem")
}

tasks.assemble {
    dependsOn(generateMcmTranslations)
    dependsOn(copyMcmImages)
}