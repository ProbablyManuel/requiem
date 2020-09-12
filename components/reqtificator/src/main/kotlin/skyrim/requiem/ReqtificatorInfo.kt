package skyrim.requiem

import java.util.Properties

data class ReqtificatorInfo(
    val version: VersionStamp,
    val commitChecksum: String,
    val gitBranch: String
    ) {
    companion object {

        operator fun invoke(versionFile: Properties): ReqtificatorInfo {
            return ReqtificatorInfo(
                version = VersionStamp(versionFile.getProperty("version").toInt()),
                commitChecksum = versionFile.getProperty("revision"),
                gitBranch = versionFile.getProperty("branch")
            )
        }
    }
}