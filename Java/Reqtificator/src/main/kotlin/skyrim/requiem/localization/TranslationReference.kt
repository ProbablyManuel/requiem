package skyrim.requiem.localization

sealed class TranslationReference

data class TextReference(val path: String, val arguments: List<Any>) : TranslationReference() {
    constructor(path: String, vararg arguments: Any) : this(path, arguments.toList())
}

data class UrlReference(val path: String) : TranslationReference()