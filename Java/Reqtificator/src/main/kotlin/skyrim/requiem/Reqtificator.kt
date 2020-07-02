package skyrim.requiem

import Reqtificator.ConsistencyManager
import Reqtificator.ExceptionManager
import Reqtificator.OldReqtificatorConfiguration
import Reqtificator.YourSaveFile
import Reqtificator.components.ActorVariations
import Reqtificator.components.Containers
import Reqtificator.components.Doors
import Reqtificator.components.EncounterZones
import Reqtificator.components.IngameManual
import Reqtificator.components.LeveledCharacters
import Reqtificator.components.LeveledItems
import Reqtificator.components.Races
import Reqtificator.enums.ConfigSections
import Reqtificator.logging_and_gui.OtherSettingsPanel
import Reqtificator.logging_and_gui.TextManager
import Reqtificator.logging_and_gui.WelcomePanel
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import lev.gui.LSaveFile
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import skyproc.GLOB
import skyproc.GRUP_TYPE
import skyproc.Mod
import skyproc.ModListing
import skyproc.SPDatabase
import skyproc.SPExeceptionHandler
import skyproc.SPGlobal
import skyproc.ScriptRef
import skyproc.gui.SPMainMenuPanel
import skyproc.gui.SUM
import skyrim.requiem.configuration.ConfigReader
import skyrim.requiem.configuration.IgnorableWarning
import skyrim.requiem.configuration.PlayerConfiguration
import skyrim.requiem.configuration.ReqtificatorConfig
import skyrim.requiem.exceptions.ReqtificatorException
import skyrim.requiem.exceptions.SetupRequirementFailedException
import skyrim.requiem.fptools.Either
import skyrim.requiem.fptools.Left
import skyrim.requiem.fptools.Right
import skyrim.requiem.fptools.flatMap
import skyrim.requiem.gui.PopupTools
import skyrim.requiem.gui.PopupTools.Companion.PopupType
import skyrim.requiem.localization.TextFormatter
import skyrim.requiem.localization.TextReference
import skyrim.requiem.localization.UrlReference
import skyrim.requiem.logic.AssignmentRule.Companion.fromActorConfig
import skyrim.requiem.logic.AssignmentRule.Companion.fromArmorConfig
import skyrim.requiem.logic.AssignmentRule.Companion.fromWeaponConfig
import skyrim.requiem.setupchecks.SetupValidator
import skyrim.requiem.transformations.RecordTransformationUtilities.actorTransformation
import skyrim.requiem.transformations.RecordTransformationUtilities.ammunitionTransformations
import skyrim.requiem.transformations.RecordTransformationUtilities.armorTransformations
import skyrim.requiem.transformations.RecordTransformationUtilities.commitRecordsToPatch
import skyrim.requiem.transformations.RecordTransformationUtilities.loadMechanicsPerksFromPlugins
import skyrim.requiem.transformations.RecordTransformationUtilities.weaponTransformations
import java.awt.Color
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale
import java.util.Properties
import javax.swing.JFrame
import kotlin.system.exitProcess

class Reqtificator(private val language: Locale) : SUM {

    // temporarily create old and new versions of some objects doing these tasks while migrating more code
    private val patcherMetadata: ReqtificatorInfo
    private val texts: TextManager = TextManager(
        "Reqtificator_EN.conf", language,
        Paths.get("translations", "Reqtificator_" + language.language + ".conf")
    )
    private val formatter: TextFormatter = TextFormatter(language)
    private val popupTools: PopupTools = PopupTools(formatter)
    private var consistency: ConsistencyManager = ConsistencyManager(texts, popupTools, "$name.esp_Consistency")
    private val save: YourSaveFile = YourSaveFile(texts)
    private val exceptionHandler: ExceptionManager = ExceptionManager(texts, formatter, popupTools)

    init {
        val versionInfo = Properties()
        versionInfo.load(this.javaClass.classLoader.getResourceAsStream("version.properties"))
        patcherMetadata = ReqtificatorInfo(versionInfo)
    }

    override fun getName(): String = "Requiem for the Indifferent"

    override fun dangerousRecordReport(): Array<GRUP_TYPE> = arrayOf()

    override fun importRequests(): Array<GRUP_TYPE> = arrayOf(
        GRUP_TYPE.LVLI,
        GRUP_TYPE.LVLN,
        GRUP_TYPE.ARMO,
        GRUP_TYPE.WEAP,
        GRUP_TYPE.AMMO,
        GRUP_TYPE.NPC_,
        GRUP_TYPE.PERK,
        GRUP_TYPE.FLST,
        GRUP_TYPE.GLOB,
        GRUP_TYPE.RACE,
        GRUP_TYPE.ECZN,
        GRUP_TYPE.CONT,
        GRUP_TYPE.DOOR,
        GRUP_TYPE.KYWD,
        GRUP_TYPE.SPEL
    )

    override fun importAtStart(): Boolean = false

    override fun hasStandardMenu(): Boolean = true

    override fun getStandardMenu(): SPMainMenuPanel {
        val settingsMenu = SPMainMenuPanel(headerColor)
        settingsMenu.setWelcomePanel(WelcomePanel(settingsMenu, texts))
        settingsMenu.addMenu(
            OtherSettingsPanel(settingsMenu, texts, save),
            false, save, YourSaveFile.Settings.OTHER_SETTINGS
        )
        return settingsMenu
    }

    override fun hasCustomMenu(): Boolean = false

    override fun openCustomMenu(): JFrame = throw UnsupportedOperationException("Not supported yet.")

    override fun hasLogo(): Boolean = false

    override fun getLogo(): URL = throw UnsupportedOperationException("Not supported yet.")

    override fun hasSave(): Boolean = true

    override fun getSave(): LSaveFile = save

    override fun getVersion(): String = patcherMetadata.version.toString()

    override fun getListing(): ModListing = ModListing(name, false)

    override fun getExportPatch(): Mod = Mod(listing)

    override fun getHeaderColor(): Color = Color(66, 181, 184)

    override fun needsPatching(): Boolean = true

    override fun hasExceptionManager(): Boolean = true

    override fun getExceptionManager(): SPExeceptionHandler = exceptionHandler

    override fun requiredMods(): ArrayList<ModListing> = arrayListOf(ModListing("Requiem.esp"))

    override fun description(): String = formatter.format(TextReference("patch.general.sum_description"))

    @Throws(Exception::class)
    override fun onStart() {
        logger.info("Reqtificator initialized")
        logger.info("Java version: ${System.getProperty("java.version")}")
        logger.info("operating system: ${System.getProperty("os.name")}")
        logger.info("patcher version: ${patcherMetadata.version}")
        logger.info("git revision: ${patcherMetadata.commitChecksum}")
        logger.info("git branch: ${patcherMetadata.gitBranch}")
        logger.info("working directory: ${Paths.get(".").toAbsolutePath().normalize()}")
        logger.info("language from Skyrim.ini: $language")
        logger.info("execution date and time: ${Calendar.getInstance().time}")

        // exceptions thrown here go straight to the Skyproc error handling
        if (!Paths.get("..", "..", "Skyrim.esm").toFile().exists()) {
            throw SetupRequirementFailedException("setup.skyrim_esm_missing")
        }
        if (!Paths.get("..", "..", "textures", "clutter", "books", "REQ_BestiaryOfSkyrim.dds").toFile().exists()) {
            throw SetupRequirementFailedException("setup.missing_textures", UrlReference("setup.urls.installation"))
        }
        consistency.checkConsistencyFile()
    }

    @Throws(Exception::class)
    override fun onExit(patchWasGenerated: Boolean) {
        if (patchWasGenerated) {
            logger.info("finished export of generated patch")
            consistency.backupConsistencyData()
            popupTools.showPopupMessage(
                TextReference("patch.general.patch_created_title"),
                TextReference("patch.general.patch_created"),
                PopupType.PatchSuccess
            )
        }
        SPGlobal.closeDebug()
        logger.info("closing program")
        exitProcess(0)
    }

    @Throws(Exception::class)
    override fun runChangesToPatch() {
        // FIXME: this is a quick hack to not having to deal with the LSaveFile more than necessary
        save.saveToFile()

        val importedMods = SPDatabase.getImportedMods()
        val merger = Mod(name + "Merger", false)
        merger.addAsOverrides(importedMods)
        val context = LoadOrderContent(importedMods, merger)
        val patch = SPGlobal.getGlobalPatch()
        SPGlobal.setAllModsAsMasters(save.getBool(YourSaveFile.Settings.ADDALLMASTERS))

        // load configs
        val oldConfiguration = OldReqtificatorConfiguration("Reqtificator.ini", texts)
        val rawConfig = loadMergedConfiguration(Paths.get("Reqtificator.conf"), context, true)
        val reqtificatorConfig = when (val config = ReqtificatorConfig(rawConfig)) {
            is Left -> throw config.left
            is Right -> config.right
        }

        // logging and setup checks
        if (save.getBool(YourSaveFile.Settings.LOG_DEBUG)) {
            setDebugLogLevel()
        }
        writeLoadOrderToLog(context)
        val versionStampPlugin = merger.getMajor(StaticReferences.GlobalVariables.versionPlugin) as GLOB
        when (val userIgnoredWarnings = testSetup(versionStampPlugin, reqtificatorConfig.ignoredWarnings, context)) {
            is Left -> throw userIgnoredWarnings.left
            is Right -> IgnorableWarning.writeIgnoredWarningsToFile(
                userIgnoredWarnings.right,
                ConfigReader.userConfigPath.toFile()
            )
        }

        // apply changes to records
        EncounterZones(texts).reqtifyZones(merger, patch, save.getBool(YourSaveFile.Settings.OpenEncounterZones))
        commitRecordsToPatch(transformActors(reqtificatorConfig.playerConfig, context, merger, oldConfiguration), patch)
        Races(texts, oldConfiguration.settings[ConfigSections.RaceVisuals]!!).reqtify_races(merger, patch)
        commitRecordsToPatch(transformArmors(reqtificatorConfig, context), patch)
        commitRecordsToPatch(transformWeapons(context), patch)
        commitRecordsToPatch(transformAmmunition(context), patch)
        Containers(texts).reqtifyContainers(merger, patch)
        Doors(texts).reqtifyDoors(merger, patch)
        IngameManual.patchIngameManual(merger, patch)

        val lItemTrafo = LeveledItems(
            texts,
            oldConfiguration.modsWithCompactLists,
            oldConfiguration.modsWithTemperedItems,
            merger
        )
        lItemTrafo.reqtifyLeveledItems(merger, patch, save.getBool(YourSaveFile.Settings.LITEM_MERGE))
        val lCharTrafo = LeveledCharacters(
            texts,
            oldConfiguration.modsWithCompactLists,
            oldConfiguration.modsWithActorVariations,
            merger
        )
        val variations = lCharTrafo.reqtifyLeveledChars(merger, patch, save.getBool(YourSaveFile.Settings.LCHAR_MERGE))
        ActorVariations(texts).reqtifyActorVariations(merger, patch, variations)

        // update ingame version stamp and plugin information of the generated patch
        val versionStampPatch = merger.getMajor(StaticReferences.GlobalVariables.versionPatch) as GLOB
        versionStampPatch.value = versionStampPlugin.value
        patch.addRecord(versionStampPatch)
        patch.description = texts.format("patch.general.description")
        patch.author = texts.format("patch.general.authors")

        logger.info("started export of generated patch")
    }

    private fun transformAmmunition(context: LoadOrderContent): Set<Ammunition> {
        val ammoTransformer = ammunitionTransformations()
        return ammoTransformer.transformRecords(
            context.lastOverwrites.ammo.records,
            context,
            "Ammunition",
            texts.format("patch.gui_titles.ammunition")
        )
    }

    private fun transformArmors(config: ReqtificatorConfig, context: LoadOrderContent): Set<Armor> {
        val rulesConfig = loadMergedRuleConfigs("ArmorKeywordAssignments", context, true)
        val armorTransformer = armorTransformations(config.armors, fromArmorConfig(rulesConfig, context.lastOverwrites))
        return armorTransformer.transformRecords(
            context.lastOverwrites.armors.records,
            context,
            "Armors",
            texts.format("patch.gui_titles.armors")
        )
    }

    private fun transformWeapons(context: LoadOrderContent): Set<Weapon> {
        val rulesConfig = loadMergedRuleConfigs("WeaponKeywordAssignments", context, true)
        val weaponTransformer = weaponTransformations(fromWeaponConfig(rulesConfig, context.lastOverwrites))
        return weaponTransformer.transformRecords(
            context.lastOverwrites.weapons.records,
            context,
            "Weapons",
            texts.format("patch.gui_titles.weapons")
        )
    }

    private fun transformActors(
        playerConfig: PlayerConfiguration,
        context: LoadOrderContent,
        merger: Mod,
        oldConfiguration: OldReqtificatorConfiguration
    ): Set<Actor> {
        val rulesConfig = loadMergedRuleConfigs("ActorAssignmentRules", context, true)
        val actorTransformer = actorTransformation(
            playerConfig = playerConfig,
            assignmentRules = fromActorConfig(rulesConfig, context.lastOverwrites),
            globalPerks = loadMechanicsPerksFromPlugins(merger),
            globalScripts = setOf(ScriptRef("REQ_NPCData")),
            visualTemplateProviders = oldConfiguration.settings[ConfigSections.NPCVisuals]!!
        )
        return actorTransformer.transformRecords(
            context.lastOverwrites.npCs.records,
            context,
            "Actors",
            texts.format("patch.gui_titles.actors")
        )
    }

    private fun loadMergedRuleConfigs(
        baseName: String,
        context: LoadOrderContent,
        dumpMergedConfigToFile: Boolean
    ): Config {
        val configFiles = ConfigReader.findRuleFiles(Paths.get(".", "Data"), baseName, context.importHistory)
        val mergedConfig = ConfigReader.loadAndMergeConfigFiles(configFiles)
        if (dumpMergedConfigToFile) ConfigReader.dumpConfigurationToFile(mergedConfig, baseName + "_merged.conf")
        return mergedConfig
    }

    private fun loadMergedConfiguration(
        fileName: Path,
        context: LoadOrderContent,
        dumpMergedConfigToFile: Boolean
    ): Config {
        val configBaseDir = Paths.get(".", "config")
        val configFiles = ConfigReader.findConfigurationFiles(configBaseDir, fileName, context.importHistory)
        val mergedConfig = ConfigReader.loadAndMergeConfigFiles(configFiles)
        if (dumpMergedConfigToFile) ConfigReader.dumpConfigurationToFile(mergedConfig, "merged_$fileName")
        return mergedConfig
    }

    private fun testSetup(
        pluginVersionVar: GLOB,
        warningsIgnoredByMods: Set<IgnorableWarning>,
        loadOrder: LoadOrderContent
    ): Either<ReqtificatorException, Set<IgnorableWarning>> {
        val pluginVersion = VersionStamp(pluginVersionVar.value.toInt())
        val userIgnores = if (ConfigReader.userConfigPath.toFile().exists()) {
            IgnorableWarning.readFromConfig(
                ConfigFactory.parseFile(ConfigReader.userConfigPath.toFile()),
                "UserSettings.conf"
            )
        } else {
            Right(setOf())
        }
        val setupValidator = SetupValidator(popupTools)

        return setupValidator.checkRequirements(loadOrder.importedMods(), pluginVersion, patcherMetadata.version)
            .flatMap {
                userIgnores.flatMap {
                    setupValidator.validateLoadOrderAndOverrideWarnings(
                        it,
                        warningsIgnoredByMods,
                        loadOrder.importedMods()
                    )
                }
            }
    }

    private fun setDebugLogLevel() {
        val ctx = LogManager.getContext(false) as LoggerContext
        val config = ctx.configuration
        config.rootLogger.level = Level.DEBUG
        ctx.updateLoggers()
    }

    private fun writeLoadOrderToLog(context: LoadOrderContent) {
        context.importHistory.withIndex().forEach {
            logger.info("Mod Index ${it.index}: ${it.value}")
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
