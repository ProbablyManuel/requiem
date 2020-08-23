plugins {
    java
}

project("Reqtificator") {
    apply<JavaPlugin>()

    val reqtificatorBuildDir: File? by rootProject.extra

    if (reqtificatorBuildDir != null) {
        buildDir = reqtificatorBuildDir!!.resolve(project.name)
    }

    tasks.compileJava {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        testImplementation("org.mockito:mockito-core:2.+")
        testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    }

    tasks.jar {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
        manifest.attributes(
            mapOf(
                "provider" to "The Requiem Dungeon Masters"
            )
        )
    }

}
