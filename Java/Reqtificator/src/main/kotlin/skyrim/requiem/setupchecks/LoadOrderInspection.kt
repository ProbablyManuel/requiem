package skyrim.requiem.setupchecks

import skyproc.ModListing
import skyrim.requiem.Done
import skyrim.requiem.configuration.IgnorableWarning
import skyrim.requiem.exceptions.LoadOrderIssueDetectedException
import skyrim.requiem.fptools.Either
import skyrim.requiem.fptools.Left
import skyrim.requiem.fptools.Right
import skyrim.requiem.localization.UrlReference
import java.nio.file.Path
import java.nio.file.Paths

sealed class LoadOrderInspection {
    abstract fun checkSetup(context: TestContext): Either<LoadOrderIssueDetectedException, Done>
    abstract val logMessage: String
    abstract val configKey: IgnorableWarning

    companion object {
        data class TestContext(
            val loadOrder: List<ModListing>,
            val sksePlugins: List<Path>
        )
    }
}

object EquippingOverhaulPresent : LoadOrderInspection() {
    override fun checkSetup(context: Companion.TestContext): Either<LoadOrderIssueDetectedException, Done> {
        return if (context.loadOrder.contains(ModListing("Equipping Overhaul.esp"))) {
            Left(LoadOrderIssueDetectedException("load_order_checks.equipping_overhaul"))
        } else {
            Right(Done)
        }
    }

    override val logMessage: String = "equipping overhaul found"
    override val configKey: IgnorableWarning = IgnorableWarning.EquippingOverhaulInstalled
}

object RecommendedModLimitForNewcomers : LoadOrderInspection() {
    override fun checkSetup(context: Companion.TestContext): Either<LoadOrderIssueDetectedException, Done> {
        return if (context.loadOrder.size > 100) {
            Left(
                LoadOrderIssueDetectedException(
                    "load_order_checks.mod_count",
                    UrlReference("urls.installation"),
                    UrlReference("urls.compatibility")
                )
            )
        } else {
            Right(Done)
        }
    }

    override val logMessage: String = "recommended mod limit for newcomers exceeded"
    override val configKey: IgnorableWarning = IgnorableWarning.RecommendedModCountExceeded
}

object CrashFixesSksePluginInstalled : LoadOrderInspection() {
    override fun checkSetup(context: Companion.TestContext): Either<LoadOrderIssueDetectedException, Done> {
        return if (context.sksePlugins.contains(Paths.get("CrashFixPlugin.dll"))) {
            Right(Done)
        } else {
            Left(
                LoadOrderIssueDetectedException(
                    "load_order_checks.crash_fixes_missing",
                    UrlReference("urls.crash_fixes")
                )
            )
        }
    }

    override val logMessage: String = "Crash Fixes SKSE plugin not found"
    override val configKey: IgnorableWarning = IgnorableWarning.CrashFixesSksePluginMissing
}

object BugFixesSksePluginInstalled : LoadOrderInspection() {
    override fun checkSetup(context: Companion.TestContext): Either<LoadOrderIssueDetectedException, Done> {
        return if (context.sksePlugins.contains(Paths.get("BugFixPlugin.dll"))) {
            Right(Done)
        } else {
            Left(
                LoadOrderIssueDetectedException(
                    "load_order_checks.bug_fixes_missing",
                    UrlReference("urls.bug_fixes")
                )
            )
        }
    }

    override val logMessage: String = "Bug Fixes SKSE plugin not found"
    override val configKey: IgnorableWarning = IgnorableWarning.BugFixesSksePluginMissing
}

object CobbFixesSksePluginInstalled : LoadOrderInspection() {
    override fun checkSetup(context: Companion.TestContext): Either<LoadOrderIssueDetectedException, Done> {
        return if (context.sksePlugins.contains(Paths.get("CobbBugFixes.dll"))) {
            Right(Done)
        } else {
            Left(
                LoadOrderIssueDetectedException(
                    "load_order_checks.cobb_fixes_missing",
                    UrlReference("urls.cobb_fixes")
                )
            )
        }
    }

    override val logMessage: String = "Cobb Fixes SKSE plugin not found"
    override val configKey: IgnorableWarning = IgnorableWarning.CobbFixesSksePluginMissing
}