package skyrim.requiem.build

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.*
import java.io.File

open class CompileCSharpTask : DefaultTask() {

    @OutputDirectory
    lateinit var targetDirectory: File
    @InputDirectory
    lateinit var solutionFolder: File
    @Input
    lateinit var projectName: String

    @TaskAction
    fun buildCSharpProject() {
        val compileTask = ProcessBuilder(listOf("dotnet", "publish", projectName, "-r", "win-x64", "-o", "$targetDirectory", "-c", "release"))
            .directory(solutionFolder)
            .redirectErrorStream(true)
        val process = compileTask.start()
        val compilationResult = process.waitFor() == 0
        //TODO: might be causing issues if there's too much output, but works fine for now
        process.inputStream.reader().use { it.forEachLine { line -> println(line) }}
        if (!compilationResult) throw GradleException("C# project failed to build!")
    }
}