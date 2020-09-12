package skyrim.requiem.exceptions

import skyproc.FormID
import skyproc.GRUP_TYPE
import skyproc.MajorRecord
import skyrim.requiem.localization.TextReference
import skyrim.requiem.localization.UrlReference

abstract class ReqtificatorException : Exception() {
    abstract val messageTemplate: TextReference
}

data class SetupRequirementFailedException(
    override val messageTemplate: TextReference
) : ReqtificatorException() {

    constructor(messageTemplate: String, vararg arguments: Any) :
        this(TextReference(messageTemplate, arguments.toList()))
}

data class InvalidConfigurationException(
    override val messageTemplate: TextReference
) : ReqtificatorException() {

    constructor(messageTemplate: String, vararg arguments: Any) :
        this(TextReference(messageTemplate, arguments.toList()))
}

data class LoadOrderIssueDetectedException(
    override val messageTemplate: TextReference
) : ReqtificatorException() {

    constructor(messageTemplate: String, vararg arguments: Any) :
        this(TextReference(messageTemplate, arguments.toList()))
}

data class InvalidDataInLoadOrderException(
    override val messageTemplate: TextReference
) : ReqtificatorException() {

    constructor(messageTemplate: String, vararg arguments: Any) :
        this(TextReference(messageTemplate, arguments.toList()))
}

data class InvalidRecordException(
    val formID: FormID,
    val expectedTypes: Collection<GRUP_TYPE>
) : ReqtificatorException() {
    constructor(formID: FormID, vararg types: GRUP_TYPE) :
        this(formID, types.toList())

    override val message = "failed to resolve record $formID as types ${expectedTypes.joinToString(", ") { it.name }}"
    override val messageTemplate = TextReference(
        "error_handling.invalidFormReference",
        listOf(formID, expectedTypes.joinToString(", ") { it.name })
    )
}

/**
 * a wrapper with some extra context for any exception during patching that is not anticipated
 */
data class UnexpectedFailureException(
    val failedRecord: MajorRecord,
    override val cause: Throwable
) : ReqtificatorException() {

    override val message = "encountered an unexpected problem: ${cause.message}"
    override val messageTemplate = TextReference(
        "error_handling.unexpected_error",
        failedRecord,
        cause.localizedMessage,
        UrlReference("urls.service_desk")
    )
}