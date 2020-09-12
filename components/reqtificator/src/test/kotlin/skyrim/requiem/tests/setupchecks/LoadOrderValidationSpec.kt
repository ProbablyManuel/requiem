package skyrim.requiem.tests.setupchecks

import io.kotlintest.matchers.instanceOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import skyproc.ModListing
import skyrim.requiem.Done
import skyrim.requiem.VersionStamp
import skyrim.requiem.exceptions.SetupRequirementFailedException
import skyrim.requiem.setupchecks.LoadOrderValidation
import skyrim.requiem.setupchecks.PluginCountSupportedBySkyrim
import skyrim.requiem.setupchecks.PatcherAndPluginsVersionMatch
import skyrim.requiem.setupchecks.PcExclusiveAnimationsPatchNotInstalled
import skyrim.requiem.tests.left
import skyrim.requiem.tests.right

class LoadOrderValidationSpec : WordSpec() {
    init {

        val context = LoadOrderValidation.Companion.TestContext(
            listOf(
                ModListing("Skyrim.esm"),
                ModListing("Dawnguard.esm"),
                ModListing("HearthFires.esm"),
                ModListing("Dragonborn.esm"),
                ModListing("Unofficial Skyrim Legendary Edition Patch.esp"),
                ModListing("Requiem.esp")
            ),
            VersionStamp(3, 4, 2),
            VersionStamp(3, 4, 2)
        )

        "LoadOrder Inspections" When {

            "verifying that the maximum supported plugin count has not been exceeded" should {

                "report an issue if too many plugins are loaded" {
                    val affectedLoadOrder = context.copy(
                        loadOrder = context.loadOrder + 1.rangeTo(249).map { ModListing("mod$it.esp") }
                    )

                    PluginCountSupportedBySkyrim.checkSetup(affectedLoadOrder).left() shouldBe
                        instanceOf(SetupRequirementFailedException::class)
                }

                "report no issue if the number of plugins is below the limit" {
                    PluginCountSupportedBySkyrim.checkSetup(context).right() shouldBe Done
                }
            }

            "verifying that the old PCEA patch that was a complete copy of Requiem is not loaded" should {

                "report an issue if the plugin is loaded" {
                    val affectedLoadOrder = context.copy(
                        loadOrder = context.loadOrder + ModListing("Requiem plus PCEA 3-5.esp")
                    )

                    PcExclusiveAnimationsPatchNotInstalled.checkSetup(affectedLoadOrder).left() shouldBe
                        instanceOf(SetupRequirementFailedException::class)
                }

                "report no issue if the plugin is not present" {
                    PcExclusiveAnimationsPatchNotInstalled.checkSetup(context).right() shouldBe Done
                }
            }

            "verifying that the version of the patcher matches the loaded plugin version" should {

                "report an issue if there is a mismatch" {
                    val affectedLoadOrder = context.copy(
                        pluginVersion = VersionStamp(42, 4, 666)
                    )

                    PatcherAndPluginsVersionMatch.checkSetup(affectedLoadOrder).left() shouldBe
                        instanceOf(SetupRequirementFailedException::class)
                }

                "report no issue if the versions are matching" {
                    PatcherAndPluginsVersionMatch.checkSetup(context).right() shouldBe Done
                }
            }
        }
    }
}