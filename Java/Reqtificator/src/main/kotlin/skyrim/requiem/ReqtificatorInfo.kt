package skyrim.requiem

import java.util.Properties

data class ReqtificatorInfo(
    val version: VersionStamp,
    val commitChecksum: String,
    val mercurialBranch: String,
    val mercurialBookmarks: String
) {
    companion object {

        operator fun invoke(versionFile: Properties): ReqtificatorInfo {
            return ReqtificatorInfo(
                version = VersionStamp(versionFile.getProperty("version").toInt()),
                commitChecksum = versionFile.getProperty("revision"),
                mercurialBranch = versionFile.getProperty("branch"),
                mercurialBookmarks = versionFile.getProperty("bookmarks")
            )
        }
    }
}