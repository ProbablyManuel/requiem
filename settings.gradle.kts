include(
    "components:skyproc",
    "components:reqtificator",
    "components:interface",
    "components:fomod-installer",
    "components:documentation",
    "components:papyrus-scripts",
    "components:mutagen-reqtificator"
)
rootProject.name = "Requiem-Development"
pluginManagement {
    plugins {
        kotlin("jvm") version "1.4.10"
        id("org.jlleitschuh.gradle.ktlint") version "9.3.0"
        id("org.beryx.jlink") version "2.21.3"
    }
}