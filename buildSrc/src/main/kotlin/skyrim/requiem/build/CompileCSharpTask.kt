package skyrim.requiem.build

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.*
import java.io.File

open class CompileCSharpTask : DefaultTask() {

    @OutputFile
    lateinit var compilerLog: File
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
            .redirectOutput(ProcessBuilder.Redirect.to(compilerLog))
            .redirectErrorStream(true)
        val compilationResult = compileTask.start().waitFor() == 0
        if (!compilationResult) throw GradleException("C# project failed to build!")
    }
}