plugins {
    base
}

val outputDir by project.extra(file("$buildDir/output"))
val releaseDocsDir by project.extra(file("$outputDir/release-docs"))

val copyDocsForDistribution by tasks.registering(Copy::class) {
    from("manual-and-changelogs")
    into(releaseDocsDir)
}

tasks.assemble {
    dependsOn(copyDocsForDistribution)
}