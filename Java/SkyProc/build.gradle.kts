tasks.jar {
    manifest {
        attributes(
                mapOf(
                        "Implementation-Title" to "SkyProc Library (Requiem Version)",
                        "Implementation-Version" to archiveVersion
                )
        )
    }
}

val moduleName: String by project.extra("skyproc")

tasks.compileJava {
    inputs.property("moduleName", moduleName)
    doFirst {
        options.compilerArgs = listOf("--module-path", classpath.asPath)
        classpath = files()
    }
}