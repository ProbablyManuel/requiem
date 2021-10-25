package skyrim.requiem.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class SkyProcVersionFileTask() : DefaultTask() {

    @Input
    lateinit var revision: String
    @Input
    lateinit var branch: String
    @Input
    val version: RequiemVersion = project.version as RequiemVersion
    @OutputFile
    lateinit var versionFile: File

    @TaskAction
    fun writeVersionInfo(): Unit {
        versionFile.writeText(
            """|revision=$revision
                |branch=$branch
                |version=${version.toIntegerRepresenation()}
            """.trimMargin("|")
        )
    }
}
