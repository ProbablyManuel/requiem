include(
    "Java:SkyProc",
    "Java:Reqtificator",
    "components:interface",
    "components:fomod-installer",
    "components:documentation"
)
rootProject.name = "Requiem-Development"
pluginManagement {
    plugins {
        kotlin("jvm") version "1.4.0"
        id("org.jlleitschuh.gradle.ktlint") version "9.3.0"
        id("org.beryx.jlink") version "2.21.3"
    }
}

