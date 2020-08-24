plugins {
    `java-library`
}

java {
    modularity.inferModulePath.set(true)
}

tasks.compileJava {
    options.release.set(11)
}

tasks.jar {
    manifest {
        attributes(
                mapOf(
                        "Implementation-Title" to "SkyProc Library (Requiem Version)",
                        "Implementation-Version" to archiveVersion,
                        "provider" to "The Requiem Dungeon Masters"
                )
        )
    }
}