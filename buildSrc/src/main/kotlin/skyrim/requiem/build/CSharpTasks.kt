package skyrim.requiem.build

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.*
import java.io.File

private fun runProcess(args: List<String>, workDir: File): Boolean {
    val task = ProcessBuilder(args)
        .directory(workDir)
        .redirectErrorStream(true)
    val process = task.start()
    process.inputStream.reader().use { it.forEachLine { line -> println(line) } }
    return process.waitFor() == 0

}

//TODO: refine input parameters to avoid unnecessary re-executions after temp build subfolders are created
open class CompileCSharpTask : DefaultTask() {

    @InputDirectory
    lateinit var solutionFolder: File

    @Input
    lateinit var projectName: String

    @Input
    var warningsAsErrors: Boolean = true

    @TaskAction
    fun taskAction() {
        val args = listOf("dotnet", "build", projectName) + if (warningsAsErrors) listOf("-warnaserror") else listOf()
        if (!runProcess(args, solutionFolder)) throw GradleException("C# project '$projectName' failed to compile!")
    }
}

open class PublishCSharpTask : DefaultTask() {

    @OutputDirectory
    lateinit var targetDirectory: File

    @InputDirectory
    lateinit var solutionFolder: File

    @Input
    lateinit var projectName: String

    @Input
    var warningsAsErrors: Boolean = true

    @TaskAction
    fun taskAction() {
        val args = (listOf("dotnet", "publish", projectName, "-r", "win-x64", "-o", "$targetDirectory", "-c", "release")
            + if (warningsAsErrors) listOf("-warnaserror") else listOf())
        if (!runProcess(args, solutionFolder)) throw GradleException("C# project '$projectName' failed to publish!")
    }
}

open class TestCSharpTask : DefaultTask() {

    @Input
    var loglevel: String = "normal"
    @InputDirectory
    lateinit var solutionFolder: File

    @TaskAction
    fun taskAction() {
        val args = listOf("dotnet", "test", "--verbosity", loglevel, "--no-build")
        if (!runProcess(args, solutionFolder)) throw GradleException("Unit tests for C# project were not successful!")
    }
}
