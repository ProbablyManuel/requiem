package skyrim.requiem.build

import java.io.Serializable

data class RequiemVersion(
    val majorRelease: Int,
    val minorRelease: Int,
    val bugfixRelease: Int,
    val name: String
) : Serializable {
    init {
        require(majorRelease >= 0) { -> "majorRelease must be >= 0" }
        require(minorRelease >= 0 && minorRelease <= 99) { -> "minorRelease must be in the range 0 to 99" }
        require(bugfixRelease >= 0 && bugfixRelease <= 99) { -> "bugfixRelease must be in the range 0 to 99" }
    }

    override fun toString(): String = "$majorRelease.$minorRelease.$bugfixRelease"

    fun toPrettyString(): String = """Requiem $majorRelease.$minorRelease.$bugfixRelease - "$name""""

    fun toIntegerRepresenation(): Int = majorRelease * 10000 + minorRelease * 100 + bugfixRelease
}