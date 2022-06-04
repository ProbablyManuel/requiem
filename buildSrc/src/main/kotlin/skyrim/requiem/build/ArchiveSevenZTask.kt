package skyrim.requiem.build

import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.util.function.Predicate
import java.util.regex.Pattern

open class ArchiveSevenZTask : DefaultTask() {

    @OutputFile
    lateinit var archiveFile: File
    @Input
    lateinit var fileMapping: Map<File, File>
    @Input
    lateinit var baseDirectory: File
    @Input
    lateinit var excludeFolders: FileCollection
    @Input
    lateinit var excludePatterns: List<String>

    @TaskAction
    fun buildArchive() {
        project.delete(archiveFile)
        logger.quiet("Now starting to pack release $archiveFile")
        val patterns = excludePatterns.map { Pattern.compile(it, Pattern.CASE_INSENSITIVE).asPredicate() }

        SevenZOutputFile(archiveFile).use { archive ->
            fileMapping.forEach { (source, target) ->
                val relativeTargetDir = target.relativeTo(baseDirectory)
                if (source.isDirectory) {
                    copyFileTree(source, relativeTargetDir, patterns, archive)
                } else {
                    copyFile(source, relativeTargetDir.resolve(source.name), patterns, archive)
                }
            }
        }
    }

    private fun copyFileTree(source: File,
                             relativeTargetDir: File,
                             patterns: List<Predicate<String>>,
                             archive: SevenZOutputFile) {
        source.walkTopDown()
            .onEnter {
                val shouldProcess = !excludeFolders.contains(it)
                if (shouldProcess) {
                    logger.quiet("now packing folder '${it.relativeTo(baseDirectory)}'")
                }
                shouldProcess
            }
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