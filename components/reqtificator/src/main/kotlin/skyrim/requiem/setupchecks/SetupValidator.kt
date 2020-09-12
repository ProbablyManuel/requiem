package skyrim.requiem.setupchecks

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import skyproc.ModListing
import skyrim.requiem.Done
import skyrim.requiem.VersionStamp
import skyrim.requiem.configuration.IgnorableWarning
import skyrim.requiem.exceptions.LoadOrderIssueDetectedException
import skyrim.requiem.exceptions.SetupRequirementFailedException
import skyrim.requiem.fptools.Either
import skyrim.requiem.fptools.Left
import skyrim.requiem.fptools.Right
import skyrim.requiem.fptools.flatMap
import skyrim.requiem.fptools.leftFlatMap
import skyrim.requiem.gui.PopupTools
import skyrim.requiem.localization.TextReference
import skyrim.requiem.localization.Translatable
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

class SetupValidator(private val popups: PopupTools) {
    private val logger = LogManager.getLogger()

    fun checkRequirements(
        loadOrder: List<ModListing>,
        pluginVersion: VersionStamp,
        patcherVersion: VersionStamp
    ): Either<SetupRequirementFailedException, Done> {
        ThreadContext.put("context", "Setup Requirements")
        val requirementsToCheck =
            listOf(
                PatcherAndPluginsVersionMatch,
                PluginCountSupportedBySkyrim,
                PcExclusiveAnimationsPatchNotInstalled
            )
        val context = LoadOrderValidation.Companion.TestContext(loadOrder, pluginVersion, patcherVersion)
        val initial: Either<SetupRequirementFailedException, Done> = Right(Done)
        val result = requirementsToCheck.fold(initial) { result, requirement ->
            result.flatMap { requirement.checkSetup(context) }
        }
        ThreadContext.remove("context")
        return result
    }

    fun validateLoadOrderAndOverrideWarnings(
        previouslyUserIgnoredWarnings: Set<IgnorableWarning>,
        modIgnoredWarnings: Set<IgnorableWarning>,
        loadOrder: List<ModListing>
    ): Either<LoadOrderIssueDetectedException, Set<IgnorableWarning>> {
        ThreadContext.put("context", "Setup Checks [recommended]")
        val pluginsLocation = Paths.get("..", "..", "SKSE", "Plugins")
        val loadedSksePlugins = if (pluginsLocation.toFile().exists()) {
            Files.list(pluginsLocation)
                .filter { it.fileName.toString().endsWith(".dll") }
                .map { it.fileName }
                .toList()
        } else {
            listOf()
        }

        val issuesToCheck =
            listOf(
                EquippingOverhaulPresent,
                RecommendedModLimitForNewcomers,
                CrashFixesSksePluginInstalled,
                BashedPatchUsed,
                BugFixesSksePluginInstalled,
                CobbFixesSksePluginInstalled
            )
        val initial: Either<LoadOrderIssueDetectedException, Set<IgnorableWarning>> =
            Right(previouslyUserIgnoredWarnings)
        val context = LoadOrderInspection.Companion.TestContext(loadOrder, loadedSksePlugins)

        val userIgnoreConfig = issuesToCheck.fold(initial) { userIgnoredWarnings, possibleIssue ->
            userIgnoredWarnings.flatMap { oldUserIgnoredWarnings ->
                possibleIssue.checkSetup(context)
                    .map { oldUserIgnoredWarnings - possibleIssue.configKey }
                    .leftFlatMap {
                        if (possibleIssue.configKey in modIgnoredWarnings) {
                            Right(oldUserIgnoredWarnings)
                        } else {
                            askUserHowToHandleWarning(it, possibleIssue, oldUserIgnoredWarnings)
                        }
                    }
            }
        }
        ThreadContext.remove("context")
        return userIgnoreConfig
    }

    private fun askUserHowToHandleWarning(
        warning: LoadOrderIssueDetectedException,
        inspection: LoadOrderInspection,
        ignoredWarnings: Set<IgnorableWarning>
    ): Either<LoadOrderIssueDetectedException, Set<IgnorableWarning>> {

        return if (inspection.configKey in ignoredWarnings) {
            Right(ignoredWarnings)
        } else {
            logger.warn("issue found in user setup: ${inspection.logMessage}")
            popups.showPopupQuestion(
                TextReference("load_order_checks.warning_title"),
                warning.messageTemplate,
                Choices.values(),
                Choices.Abort,
                PopupTools.Companion.PopupType.Warning
            ).map<Either<LoadOrderIssueDetectedException, Set<IgnorableWarning>>> {
                when (it) {
                    Choices.Abort -> Left(LoadOrderIssueDetectedException(inspection.logMessage))
                    Choices.IgnoreOnce -> Right(ignoredWarnings - inspection.configKey)
                    Choices.IgnoreAlways -> Right(ignoredWarnings + inspection.configKey)
                }
            }.getOrElse { Left(LoadOrderIssueDetectedException(inspection.logMessage)) }
        }
    }

    companion object {
        private enum class Choices(override val text: TextReference) : Translatable {
            Abort(TextReference("gui.warnings.abort")),
            IgnoreOnce(TextReference("gui.warnings.ignoreOnce")),
            IgnoreAlways(TextReference("gui.warnings.ignoreAlways")),
        }
    }
}