plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("org.apache.commons:commons-compress:1.28.0")
    implementation("org.tukaani:xz:1.12")
}

repositories {
    mavenCentral()
}