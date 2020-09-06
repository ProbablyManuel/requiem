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

val reqtificatorDir by extra(file("$rootDir/SkyProc Patchers/Requiem/app"))

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.typesafe:config:1.4.0")
    implementation("org.apache.logging.log4j:log4j-api:2.13.0")
    implementation("org.apache.logging.log4j:log4j-core:2.13.0")
    implementation(project(":Java:SkyProc"))
    testImplementation(kotlin("reflect"))
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("net.bytebuddy:byte-buddy:1.10.6")
}

val createVersionFile = tasks.register<VersionFileTask>("createVersionFile") {
    val gitRevision: String by rootProject.extra
    val gitBranch: String by rootProject.extra

    group = "build"
    description = "store Mercurial revision information in a properties file"

    revision = gitRevision
    branch = gitBranch
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
                "Implementation-Version" to archiveVersion,
                "provider" to "The Requiem Dungeon Masters"
            )
        )
    }
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

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    destinationDir = tasks.compileKotlin.map { it.destinationDir }.get()
}

jlink {
    setProperty("options", listOf("--compress", "2", "--no-header-files", "--no-man-pages"))
    setProperty("imageDir", reqtificatorDir)
    forceMerge("log4j-api", "kotlin") // kotlin-stdlib-common has no module-identifier and has split modules

    launcher {
        name = "launcher_template"
    }
}