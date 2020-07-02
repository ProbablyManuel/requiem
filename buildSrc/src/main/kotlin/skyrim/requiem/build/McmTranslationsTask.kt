package skyrim.requiem.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

open class McmTranslationsTask : DefaultTask() {

    @InputFile
    lateinit var templateFile: File
    @Input
    lateinit var placeholder: String
    @Input
    lateinit var version: RequiemVersion
    @OutputFiles
    lateinit var outputFiles: List<File>

    @TaskAction
    fun createMcmFiles(): Unit {
        project.delete(outputFiles)
        val content = templateFile.readText(Charsets.UTF_16LE).replace(placeholder, version.toPrettyString())
        outputFiles.forEach { file -> file.writeText(content, Charsets.UTF_16LE) }
    }
}