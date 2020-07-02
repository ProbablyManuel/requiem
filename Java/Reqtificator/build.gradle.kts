import skyrim.requiem.build.VersionFileTask

plugins {
    application
    java
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.beryx.jlink")
}

val reqtificatorDir = objects.directoryProperty()
reqtificatorDir.set(file("$rootDir/SkyProc Patchers/Requiem/app"))

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.typesafe:config:1.3.1")
    implementation("org.apache.logging.log4j:log4j-api:2.13.0")
    implementation("org.apache.logging.log4j:log4j-core:2.13.0")
    implementation(project(":Java:SkyProc"))
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("net.bytebuddy:byte-buddy:1.10.6")
}

val createVersionFile = tasks.register<VersionFileTask>("createVersionFile") {
    val mercurialRevision: String by rootProject.extra
    val mercurialBranch: String by rootProject.extra
    val mercurialBookmarks: String by rootProject.extra

    group = "build"
    description = "store Mercurial revision information in a properties file"

    revision = mercurialRevision
    branch = mercurialBranch
    tags = mercurialBookmarks
    versionFile = file("file:/$projectDir/src/main/resources/version.properties")
}

tasks.processResources {
    dependsOn(createVersionFile)
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to "Reqtificator - SkyProc Patcher for the Skyrim mod 'Requiem'",
                "Implementation-Version" to archiveVersion
            )
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val cleanReqtificator = tasks.register<Delete>("cleanReqtificator") {
    group = "build"
    description = "remove the deployed Reqtificator"

    delete(reqtificatorDir)
    delete(file("file:/$projectDir/src/main/resources/version.properties"))
}

tasks.clean {
    dependsOn(cleanReqtificator)
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
    destinationDir = tasks.compileJava.map { it.destinationDir }.get()
}
tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.test {
    useJUnitPlatform()
}

val moduleName by project.extra("skyrim.requiem")

tasks.compileJava {
    inputs.property("moduleName", moduleName)
    doFirst {
        options.compilerArgs = listOf("--module-path", classpath.asPath)
        classpath = files()
    }
}

jlink {
    setProperty("mainClass", "Reqtificator.Reqtificator")
    setProperty("options", listOf("--compress", "2", "--no-header-files", "--no-man-pages"))
    setProperty("imageDir", reqtificatorDir)
    forceMerge("log4j-api", "config")

    launcher {
        name = "launcher_template"
    }
}