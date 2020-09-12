import skyrim.requiem.build.VersionFileTask

plugins {
    kotlin("jvm")
    application
    id("org.jlleitschuh.gradle.ktlint")
    id("org.beryx.jlink")
}

java {
    modularity.inferModulePath.set(true)
}

application {
    mainModule.set("skyrim.requiem")
    mainClass.set("skyrim.requiem.MainKt")
}

repositories {
    mavenCentral()
}

val reqtificatorBuildDir: File? by rootProject.extra

if (reqtificatorBuildDir != null) {
    buildDir = reqtificatorBuildDir!!.resolve(project.name)
}

val outputDir by project.extra(file("$buildDir/output"))

val generatedResources = file("$buildDir/generated-resources")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.typesafe:config:1.4.0")
    implementation("org.apache.logging.log4j:log4j-api:2.13.0")
    implementation("org.apache.logging.log4j:log4j-core:2.13.0")
    implementation(project(":components:skyproc"))
    testImplementation(kotlin("reflect"))
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("net.bytebuddy:byte-buddy:1.10.6")
}

val createVersionFile by tasks.registering(VersionFileTask::class) {
    val gitRevision: String by rootProject.extra
    val gitBranch: String by rootProject.extra

    group = "build"
    description = "store Mercurial revision information in a properties file"

    revision = gitRevision
    branch = gitBranch
    versionFile = file("$generatedResources/version.properties")
}

val copyExternalConfiguration by tasks.registering(Copy::class) {
    from("configuration")
    into(outputDir)
}

sourceSets {
    main {
        output.dir(generatedResources, "builtBy" to createVersionFile)
    }
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to "Reqtificator - SkyProc Patcher for the Skyrim mod 'Requiem'",
                "Implementation-Version" to archiveVersion,
                "provider" to "The Requiem Dungeon Masters"
            )
        )
    }
}

tasks.assemble {
    dependsOn(copyExternalConfiguration)
    dependsOn(tasks.jlink)
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    destinationDir = tasks.compileKotlin.map { it.destinationDir }.get()
}

jlink {
    setProperty("options", listOf("--compress", "2", "--no-header-files", "--no-man-pages"))
    setProperty("imageDir", file("$outputDir/app"))
    forceMerge("log4j-api", "kotlin") // kotlin-stdlib-common has no module-identifier and has split modules

    launcher {
        name = "launcher_template"
    }
}