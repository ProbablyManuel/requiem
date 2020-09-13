package skyrim.requiem.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

open class McmTranslationsTask : DefaultTask() {

    @InputFile
    lateinit var templateFile: File
    @Input
    lateinit var placeholder: String
    @Input
    lateinit var languages: List<String>
    @Input
    lateinit var version: RequiemVersion
    @OutputDirectory
    lateinit var outputFolder: File

    @TaskAction
    fun createMcmFiles(): Unit {
        val content = templateFile.readText(Charsets.UTF_16LE).replace(placeholder, version.toPrettyString())
        languages.forEach { lang -> outputFolder.resolve("Requiem_$lang.txt").writeText(content, Charsets.UTF_16LE) }
    }
}