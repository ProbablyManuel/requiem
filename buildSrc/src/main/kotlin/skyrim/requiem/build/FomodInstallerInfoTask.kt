package skyrim.requiem.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

open class FomodInstallerInfoTask : DefaultTask() {

    @InputFile
    lateinit var templateFile: File
    @Input
    lateinit var placeholder: String
    @Input
    lateinit var version: RequiemVersion
    @OutputFile
    lateinit var outputFile: File

    @TaskAction
    fun createMcmFiles(): Unit {
        val content = templateFile.readText(Charsets.UTF_16).replace(placeholder, version.toString())
        outputFile.writeText(content, Charsets.UTF_16)
    }
}