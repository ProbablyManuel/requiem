package skyrim.requiem

data class VersionStamp(
    val major: Int,
    val minor: Int,
    val bugFix: Int
) {
    init {
        require(major >= 0, { "invalid major version" })
        require(minor >= 0, { "invalid minor version" })
        require(bugFix >= 0, { "invalid bugfix version" })
    }

    override fun toString(): String = String.format("%s.%s.%s", major, minor, bugFix)

    companion object {

        operator fun invoke(version: GlobalVariable): VersionStamp {
            return invoke((version.value.toInt()))
        }

        operator fun invoke(version: Int): VersionStamp {
            val major = version / 10000
            val (minor, bugFix) = if (major >= 2) {
                Pair(version % 10000 / 100, version % 10000 % 100)
            } else {
                Pair(version % 10000 / 1000, version % 10000 % 1000 / 100)
            }
            return VersionStamp(major, minor, bugFix)
        }
    }
}