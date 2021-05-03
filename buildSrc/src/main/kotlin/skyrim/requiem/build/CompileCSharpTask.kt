package skyrim.requiem.build

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CompileCSharpTask : DefaultTask() {

    @OutputFile
    lateinit var compilerLog: File
    @OutputDirectory
    lateinit var targetDirectory: File
    @InputDirectory
    lateinit var projectFolder: File

    @TaskAction
    fun buildCSharpProject() {
        val compileTask = ProcessBuilder(listOf("dotnet", "publish", "-r", "win-x64", "-o", "$targetDirectory"))
            .directory(projectFolder)
            .redirectOutput(ProcessBuilder.Redirect.to(compilerLog))
            .redirectErrorStream(true)
        val compilationResult = compileTask.start().waitFor() == 0
        if (!compilationResult) throw GradleException("C# project failed to build!")
    }
}