package skyrim.requiem.tests.setupchecks

import io.kotlintest.matchers.instanceOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import skyproc.ModListing
import skyrim.requiem.Done
import skyrim.requiem.exceptions.LoadOrderIssueDetectedException
import skyrim.requiem.setupchecks.*
import skyrim.requiem.tests.left
import skyrim.requiem.tests.right
import java.nio.file.Paths

class LoadOrderInspectionSpec : WordSpec() {
    init {

        val loadOrder = listOf(
            ModListing("Skyrim.esm"),
            ModListing("Dawnguard.esm"),
            ModListing("HearthFires.esm"),
            ModListing("Dragonborn.esm"),
            ModListing("Unofficial Skyrim Legendary Edition Patch.esp"),
            ModListing("Requiem.esp")
        )
        val plugins = listOf(
            Paths.get("CrashFixPlugin.dll"),
            Paths.get("BugFixPlugin.dll"),
            Paths.get("CobbBugFixes.dll")
        )
        val context = LoadOrderInspection.Companion.TestContext(loadOrder, plugins)

        "LoadOrder Inspections" When {

            "checking the presence of Equipping Overhaul" should {

                "report an issue if the EO plugin is found in the imported mods" {
                    val affectedLoadOrder = loadOrder + ModListing("Equipping Overhaul.esp")

                    EquippingOverhaulPresent.checkSetup(context.copy(loadOrder = affectedLoadOrder)).left() shouldBe
                        instanceOf(LoadOrderIssueDetectedException::class)
                }

                "report no issue if the EO plugin is not loaded" {
                    EquippingOverhaulPresent.checkSetup(context).right() shouldBe Done
                }
            }

            "checking the recommended mod limit for new comers" should {

                "report an issue if the load order contains 100 plugins or more" {
                    val affectedLoadOrder = 1.rangeTo(101).map { ModListing("plugin$it.esp") }

                    RecommendedModLimitForNewcomers.checkSetup(context.copy(loadOrder = affectedLoadOrder)).left() shouldBe
                        instanceOf(LoadOrderIssueDetectedException::class)
                }

                "report no issue if the load order contains less than 100 plugins" {
                    RecommendedModLimitForNewcomers.checkSetup(context).right() shouldBe Done
                }
            }

            "checking the presence of the Crash Fixes SKSE plugin" should {

                "report an issue if the SKSE plugin cannot be found" {
                    val missingPlugin = plugins - Paths.get("CrashFixPlugin.dll")

                    CrashFixesSksePluginInstalled.checkSetup(context.copy(sksePlugins = missingPlugin)).left() shouldBe
                        instanceOf(LoadOrderIssueDetectedException::class)
                }

                "report no issue if the SKSE plugin is found in the expected location" {
                    CrashFixesSksePluginInstalled.checkSetup(context).right() shouldBe Done
                }
            }

            "checking the presence of the Bug Fixes SKSE plugin" should {

                "report an issue if the SKSE plugin cannot be found" {
                    val missingPlugin = plugins - Paths.get("BugFixPlugin.dll")

                    BugFixesSksePluginInstalled.checkSetup(context.copy(sksePlugins = missingPlugin)).left() shouldBe
                        instanceOf(LoadOrderIssueDetectedException::class)
                }

                "report no issue if the SKSE plugin is found in the expected location" {
                    BugFixesSksePluginInstalled.checkSetup(context).right() shouldBe Done
                }
            }

            "checking the presence of the Cobb Fixes SKSE plugin" should {

                "report an issue if the SKSE plugin cannot be found" {
                    val missingPlugin = plugins - Paths.get("CobbBugFixes.dll")

                    CobbFixesSksePluginInstalled.checkSetup(context.copy(sksePlugins = missingPlugin)).left() shouldBe
                        instanceOf(LoadOrderIssueDetectedException::class)
                }

                "report no issue if the SKSE plugin is found in the expected location" {
                    CobbFixesSksePluginInstalled.checkSetup(context).right() shouldBe Done
                }
            }
        }
    }
}