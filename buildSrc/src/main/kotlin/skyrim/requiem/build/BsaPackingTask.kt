package skyrim.requiem.build

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.*
import java.io.File

open class BsaPackingTask : DefaultTask() {
    @OutputFile
    lateinit var logFile: File
    @OutputFile
    lateinit var archiveFile: File
    @InputDirectory
    lateinit var folder: File
    @InputFile
    lateinit var archiveTool: File

    @TaskAction
    fun buildBsaArchive() {
        val myArgs = listOf(archiveTool.absolutePath, "pack", folder.absolutePath, archiveFile.absolutePath, "-sse")
        val archiveTask = ProcessBuilder(myArgs)
            .redirectOutput(ProcessBuilder.Redirect.to(logFile))
            .redirectErrorStream(true)
        if (archiveTask.start().waitFor() != 0) throw GradleException("BSA archive creation failed!")
    }
}