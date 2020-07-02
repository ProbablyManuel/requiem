package skyrim.requiem.setupchecks

import skyproc.ModListing
import skyrim.requiem.Done
import skyrim.requiem.fptools.Either
import skyrim.requiem.fptools.Left
import skyrim.requiem.fptools.Right
import skyrim.requiem.VersionStamp
import skyrim.requiem.exceptions.SetupRequirementFailedException

sealed class LoadOrderValidation {
    abstract fun checkSetup(context: TestContext): Either<SetupRequirementFailedException, Done>

    companion object {
        data class TestContext(
            val loadOrder: List<ModListing>,
            val pluginVersion: VersionStamp,
            val patcherVersion: VersionStamp
        )
    }
}

object PluginCountSupportedBySkyrim : LoadOrderValidation() {
    override fun checkSetup(context: Companion.TestContext): Either<SetupRequirementFailedException, Done> {
        // TODO: this does not count any mods loaded after the generated patch
        return if (context.loadOrder.size <= 254) {
            Right(Done)
        } else {
            Left(SetupRequirementFailedException("load_order_checks.too_many_mods", context.loadOrder.size.toString()))
        }
    }
}

object PcExclusiveAnimationsPatchNotInstalled : LoadOrderValidation() {
    override fun checkSetup(context: Companion.TestContext): Either<SetupRequirementFailedException, Done> {
        return if (context.loadOrder.contains(ModListing("Requiem plus PCEA 3-5.esp"))) {
            Left(SetupRequirementFailedException("load_order_checks.pcea_found"))
        } else {
            Right(Done)
        }
    }
}

object PatcherAndPluginsVersionMatch : LoadOrderValidation() {
    override fun checkSetup(context: Companion.TestContext): Either<SetupRequirementFailedException, Done> {
        return if (context.patcherVersion == context.pluginVersion) {
            Right(Done)
        } else {
            Left(SetupRequirementFailedException("setup.version_mismatch", context.patcherVersion, context.pluginVersion))
        }
    }
}