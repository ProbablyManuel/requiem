package skyrim.requiem.build

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileTree
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

abstract class CSharpSolutionTask : DefaultTask() {

    @Internal
    lateinit var solutionFolder: File

    // build outputs and IDE state (e.g. files locked by Visual Studio) must not be tracked as inputs
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val solutionSources: FileTree
        get() = project.fileTree(solutionFolder) { exclude("**/.vs/**", "**/bin/**", "**/obj/**") }
}

open class CompileCSharpTask : CSharpSolutionTask() {

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

open class PublishCSharpTask : CSharpSolutionTask() {

    @OutputDirectory
    lateinit var targetDirectory: File

    @Input
    lateinit var projectName: String

    @Input
    var warningsAsErrors: Boolean = true

    @TaskAction
    fun taskAction() {
        val args = (listOf("dotnet", "publish", projectName, "-r", "win-x64", "--self-contained", "-o", "$targetDirectory", "-c", "release")
            + if (warningsAsErrors) listOf("-warnaserror") else listOf())
        if (!runProcess(args, solutionFolder)) throw GradleException("C# project '$projectName' failed to publish!")
    }
}

open class TestCSharpTask : CSharpSolutionTask() {

    @Input
    var loglevel: String = "normal"

    @TaskAction
    fun taskAction() {
        val args = listOf("dotnet", "test", "--verbosity", loglevel, "--no-build")
        if (!runProcess(args, solutionFolder)) throw GradleException("Unit tests for C# project were not successful!")
    }
}

open class CheckFormatCSharpTask : CSharpSolutionTask() {

    @Input
    var loglevel: String = "normal"

    @TaskAction
    fun taskAction() {
        val args = listOf("dotnet", "format", "--verbosity", loglevel, "--verify-no-changes")
        if (!runProcess(args, solutionFolder)) throw GradleException("C# project does not obey format and analyzer rules!")
    }
}

open class FormatCSharpTask : CSharpSolutionTask() {

    @Input
    var loglevel: String = "normal"

    @TaskAction
    fun taskAction() {
        val args = listOf("dotnet", "format", "--verbosity", loglevel)
        if (!runProcess(args, solutionFolder)) throw GradleException("C# project does not obey format and analyzer rules!")
    }
}

open class RestoreDotnetToolsTask : DefaultTask() {

    @Internal
    lateinit var solutionFolder: File
    @InputFile
    lateinit var manifestFile: File

    @TaskAction
    fun taskAction() {
        val args = listOf("dotnet", "tool", "restore")
        if (!runProcess(args, solutionFolder)) throw GradleException("Failed to restore dotnet tools from manifest!")
    }
}