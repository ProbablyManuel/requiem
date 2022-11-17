package skyrim.requiem.build

import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import java.io.File
import java.nio.file.Files
import java.util.function.Predicate
import java.util.regex.Pattern

open class ReleaseArchiveTask : DefaultTask() {

    @OutputFile
    lateinit var archiveFile: File
    @InputFiles
    lateinit var coreMod: FileCollection
    @InputDirectory
    lateinit var fomod: File
    @InputFile
    lateinit var plugin: File
    @InputFile
    lateinit var pluginCreationClub: File
    @Input
    lateinit var excludePatterns: List<String>

    @TaskAction
    fun buildArchive() {
        project.delete(archiveFile)
        val patterns = excludePatterns.map { Pattern.compile(it, Pattern.CASE_INSENSITIVE).asPredicate() }

        SevenZOutputFile(archiveFile).use { archive ->
            coreMod.forEach { entry ->
                if (entry.isDirectory) copyFileTree(entry, File("core").resolve(entry.name), patterns, archive)
                else copyFile(entry, File("core").resolve(entry.name), patterns, archive)
            }
            copyFile(plugin, File("plugin").resolve(plugin.name), patterns, archive)
            copyFile(pluginCreationClub, File("plugin").resolve(pluginCreationClub.name), patterns, archive)
            copyFileTree(fomod, File(fomod.name), patterns, archive)
        }
    }

    private fun copyFileTree(source: File,
                             relativeTargetDir: File,
                             patterns: List<Predicate<String>>,
                             archive: SevenZOutputFile) {
        source.walkTopDown()
            .onEnter { logger.quiet("now packing folder '${it.relativeTo(project.rootDir)}'"); true }
            .filter { it.isFile }
            .forEach {
                copyFile(it, relativeTargetDir.resolve(it.relativeTo(source)), patterns, archive)
            }
    }

    private fun copyFile(source: File,
                         pathInArchive: File,
                         patterns: List<Predicate<String>>,
                         archive: SevenZOutputFile) {
        if (patterns.any { it.test(source.toString()) }) {
            logger.quiet("skipping ignored file: $source")
        } else {
            logger.debug("archiving file: $source  -> $pathInArchive")
            val entry = archive.createArchiveEntry(source, pathInArchive.toString().replace("\\", "/"))
            archive.putArchiveEntry(entry)
            archive.write(Files.readAllBytes(source.toPath()))
            archive.closeArchiveEntry()
        }
    }
}