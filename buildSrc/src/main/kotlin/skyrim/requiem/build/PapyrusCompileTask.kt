package skyrim.requiem.build

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import java.io.File

open class PapyrusCompileTask : DefaultTask() {


    @InputDirectory
    lateinit var sourceFolder: File
    @OutputDirectory
    lateinit var targetDir: File
    @InputFile
    lateinit var compiler: File
    @InputFile
    lateinit var compilerFlags: File
    @InputFiles
    lateinit var includeFolders: FileCollection
    @OutputDirectory
    lateinit var compilerLogs: File
    @Internal
    var failFast: Boolean = false

    @TaskAction
    fun compileScripts() {
        val templateArgs = buildTemplateProcessArgs()
        val compilationResult = sourceFolder
            .walkTopDown()
            .filter { it.isDirectory }
            .fold(true) { state, folder ->
                if (state || !failFast) {
                    val compileResult = compileScriptsFolder(folder, templateArgs)
                    if (!compileResult) {
                        logger.error("failed compilation(s) in folder " +
                            "${folder.relativeTo(project.rootDir)}, see logs for details")
                    }
                    state && compileResult
                } else {
                    state
                }
            }
        if (!compilationResult) throw GradleException("Papyrus scripts failed to compile!")
    }

    private fun compileScriptsFolder(folder: File, template: List<String>): Boolean {
        val logFile = File(compilerLogs, folder.name + ".log")
        val myArgs = ArrayList(template)
        myArgs.add(1, folder.absolutePath)
        val compileTask = ProcessBuilder(myArgs)
            .redirectOutput(ProcessBuilder.Redirect.to(logFile))
            .redirectErrorStream(true)
        return compileTask.start().waitFor() == 0
    }

    private fun buildTemplateProcessArgs(): List<String> {
        val includes = sourceFolder.walkTopDown().filter { it.isDirectory } +
            includeFolders.filter { it.isDirectory }
        return listOf(
            compiler.absolutePath,
            "-a",
            "-f=${compilerFlags.absolutePath}",
            "-i=${includes.joinToString(";")}",
            "-o=${targetDir.absolutePath}"
        )
    }
}