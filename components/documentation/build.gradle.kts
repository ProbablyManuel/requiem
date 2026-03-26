plugins {
    base
}

val outputDir by project.extra(layout.buildDirectory.dir("output").get().asFile)
val releaseDocsDir by project.extra(file("$outputDir/documentation"))

val copyDocsForDistribution by tasks.registering(Copy::class) {
    from("src")
    into(releaseDocsDir)
}

tasks.assemble {
    dependsOn(copyDocsForDistribution)
}