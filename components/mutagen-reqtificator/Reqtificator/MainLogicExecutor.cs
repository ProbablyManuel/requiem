using System;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Cache.Implementations;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Allocators;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Configuration;
using Reqtificator.Events;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Actors;
using Reqtificator.Transformers.ActorVariations;
using Reqtificator.Transformers.Armors;
using Reqtificator.Transformers.EncounterZones;
using Reqtificator.Transformers.LeveledCharacters;
using Reqtificator.Transformers.LeveledItems;
using Reqtificator.Transformers.LeveledLists;
using Reqtificator.Transformers.Races;
using Reqtificator.Transformers.Rules;
using Reqtificator.Transformers.Weapons;
using Reqtificator.Utils;
using Serilog;

namespace Reqtificator
{
    internal class MainLogicExecutor
    {
        private readonly InternalEvents _events;
        private readonly GameContext _context;
        private readonly ReqtificatorConfig _reqtificatorConfig;
        private readonly RequiemVersion _version;

        private static readonly string PersistenceFileLocation = Environment.GetFolderPath(Environment.SpecialFolder.Personal) +
                                                                 "/My Games/Skyrim Special Edition/Requiem/FormPersistence.txt";

        public MainLogicExecutor(InternalEvents events, GameContext context, ReqtificatorConfig reqtificatorConfig, RequiemVersion version)
        {
            _events = events;
            _context = context;
            _reqtificatorConfig = reqtificatorConfig;
            _version = version;
        }

        public ErrorOr<SkyrimMod> GeneratePatch(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder,
            UserSettings userSettings, ModKey outputModKey, InternalEvents _events)
        {
            _events.PublishState(ReqtificatorState.Patching(0.0, ""));

            var requiemModKey = new ModKey("Requiem", ModType.Plugin);
            var importedModsLinkCache = loadOrder.ToImmutableLinkCache();
            var reqTags = new ReqTagParser(_events).ParseTagsFromModHeaders(loadOrder);
            var modsWithCompactLeveledLists = reqTags
                .Where(kv => kv.Value.Contains(ReqTags.CompactLeveledLists))
                .Select(kv => kv.Key).ToImmutableHashSet().Add(requiemModKey);
            var modsWithTemperedItems = reqTags
                .Where(kv => kv.Value.Contains(ReqTags.TemperedItems))
                .Select(kv => kv.Key).ToImmutableHashSet().Add(requiemModKey);
            var modsWithRequiemAsMaster = loadOrder.ListedOrder
                .Where(ml => ml.Mod!.ModHeader.MasterReferences.Select(x => x.Master).Contains(requiemModKey))
                .Select(ml => ml.ModKey)
                .ToImmutableHashSet();

            var (generatedPatch, formAllocator) = CreatePatchBaseMod(outputModKey, _version, importedModsLinkCache);

            _events.PublishState(ReqtificatorState.Patching(5, "Ammo"));
            var ammoPatched = PatchAmmunition(loadOrder);

            _events.PublishState(ReqtificatorState.Patching(10, "Encounter Zones"));
            var encounterZonesPatched = PatchEncounterZones(loadOrder, userSettings);

            _events.PublishState(ReqtificatorState.Patching(15, "Doors"));
            var doorsPatched = PatchDoors(loadOrder);

            _events.PublishState(ReqtificatorState.Patching(20, "Containers"));
            var containersPatched = PatchContainers(loadOrder);

            _events.PublishState(ReqtificatorState.Patching(25, "Leveled Items"));
            var leveledItemsPatched = PatchLeveledItems(loadOrder, modsWithCompactLeveledLists, modsWithTemperedItems,
                importedModsLinkCache, userSettings, modsWithRequiemAsMaster);

            _events.PublishState(ReqtificatorState.Patching(30, "Leveled Characters"));
            var leveledCharactersPatched = PatchLeveledCharacters(loadOrder, modsWithCompactLeveledLists,
                importedModsLinkCache, userSettings, modsWithRequiemAsMaster);

            _events.PublishState(ReqtificatorState.Patching(35, "Armors"));
            var armorsPatched = PatchArmors(loadOrder);

            _events.PublishState(ReqtificatorState.Patching(40, "Weapons"));
            var weaponsPatched = PatchWeapons(loadOrder);

            _events.PublishState(ReqtificatorState.Patching(45, "Actors"));
            var actorsPatched = PatchActors(loadOrder, importedModsLinkCache);

            _events.PublishState(ReqtificatorState.Patching(50, "Races"));
            var racesPatched = PatchRaces(loadOrder);

            // unfortunately, we must add the new records directly to the plugin to avoid broken formId links :)

            _events.PublishState(ReqtificatorState.Patching(55, "Actor Variations"));
            var actorVariationsPatched =
                actorsPatched.Map(actors => PatchActorVariations(loadOrder, actors, generatedPatch));

            Log.Information("adding patched records to output mod");

            _events.PublishState(ReqtificatorState.Patching(75, "Generating Patch"));
            var fullPatch = generatedPatch.AsSuccess()
                .Map(m => m.WithRecords(encounterZonesPatched))
                .Map(m => m.WithRecords(doorsPatched))
                .Map(m => m.WithRecords(containersPatched))
                .Map(m => m.WithRecords(leveledItemsPatched))
                .Map(m => m.WithRecords(leveledCharactersPatched))
                .FlatMap(m => armorsPatched.Map(m.WithRecords))
                .Map(m => m.WithRecords(ammoPatched))
                .FlatMap(m => weaponsPatched.Map(m.WithRecords))
                .FlatMap(m => actorsPatched.Map(m.WithRecords))
                .FlatMap(m => actorVariationsPatched.Map(m.WithRecords))
                .Map(m => m.WithRecords(racesPatched));

            formAllocator.Commit();
            return fullPatch;
        }

        private static (SkyrimMod, TextFileFormKeyAllocator) CreatePatchBaseMod(ModKey outputModKey, RequiemVersion version,
            ImmutableLoadOrderLinkCache<ISkyrimMod, ISkyrimModGetter> cache)
        {
            var patch = new SkyrimMod(outputModKey, SkyrimRelease.SkyrimSE)
            {
                ModHeader =
                {
                    Author = "The Requiem Dungeon Masters",
                    Description =
                        "This is an autogenerated patch for the mod \"Requiem - The Role-Playing Overhaul\". " +
                        $"Generated for Requiem version: {version.ShortVersion()} -- build with Mutagen"
                }
            };
            System.IO.Directory.CreateDirectory(System.IO.Path.GetDirectoryName(PersistenceFileLocation)!);
            var allocator = new TextFileFormKeyAllocator(patch, PersistenceFileLocation);
            patch.SetAllocator(allocator);

            //TODO: error handling...
            if (cache.TryResolve<IGlobalGetter>(GlobalVariables.VersionStampPatch.FormKey,
                out var original))
            {
                var record = patch.Globals.GetOrAddAsOverride(original);
                record.RawFloat = version.AsNumeric();
            }

            var requiemHelp = cache.Resolve<IFormListGetter>(FormLists.HelpMenuRequiem.FormKey);
            var helpMenuPc = cache.Resolve<IFormListGetter>(FormLists.HelpMenuPc.FormKey);
            var helpMenuXbox = cache.Resolve<IFormListGetter>(FormLists.HelpMenuXbox.FormKey);

            var patchedHelpMenuPc = patch.FormLists.GetOrAddAsOverride(helpMenuPc);
            var patchedHelpMenuXbox = patch.FormLists.GetOrAddAsOverride(helpMenuXbox);

            patchedHelpMenuPc.Items.AddRange(requiemHelp.Items);
            patchedHelpMenuXbox.Items.AddRange(requiemHelp.Items);

            return (patch, allocator);
        }

        private static ImmutableList<Ammunition> PatchAmmunition(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder)
        {
            var ammoRecords = loadOrder.PriorityOrder.Ammunition().WinningOverrides();
            return new AmmunitionTransformer().ProcessCollection(ammoRecords);
        }

        private static ImmutableList<EncounterZone> PatchEncounterZones(
            ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder,
            UserSettings userSettings)
        {
            var encounterZones = loadOrder.PriorityOrder.EncounterZone().WinningOverrides();
            return new OpenCombatBoundaries(loadOrder, userSettings).ProcessCollection(encounterZones);
        }

        private static ImmutableList<Door> PatchDoors(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder)
        {
            var doors = loadOrder.PriorityOrder.Door().WinningOverrides();
            return new CustomLockpicking<Door, IDoorGetter>().ProcessCollection(doors);
        }

        private static ImmutableList<Container> PatchContainers(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder)
        {
            var containers = loadOrder.PriorityOrder.Container().WinningOverrides();
            return new CustomLockpicking<Container, IContainerGetter>().ProcessCollection(containers);
        }

        private static ImmutableList<LeveledItem> PatchLeveledItems(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder,
            IImmutableSet<ModKey> modsWithCompactLeveledLists, IImmutableSet<ModKey> modsWithTemperedItems,
            ImmutableLoadOrderLinkCache<ISkyrimMod, ISkyrimModGetter> cache, UserSettings settings,
            IImmutableSet<ModKey> modsWithRequiemAsMaster)
        {
            var leveledItems = loadOrder.PriorityOrder.LeveledItem().WinningOverrides();
            return new CompactLeveledListUnrolling<LeveledItem, ILeveledItemGetter, ILeveledItemEntryGetter>(
                    new CompactLeveledItemUnroller(modsWithCompactLeveledLists))
                .AndThen(new LeveledListMerging<LeveledItem, ILeveledItemGetter, ILeveledItemEntryGetter>(
                    settings.MergeLeveledLists, cache, modsWithRequiemAsMaster,
                    new CompactLeveledItemUnroller(modsWithCompactLeveledLists), new LeveledItemMerger()))
                .AndThen(new TemperedItemGeneration(modsWithTemperedItems))
                .ProcessCollection(leveledItems);
        }

        private static ImmutableList<LeveledNpc> PatchLeveledCharacters(
            ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder,
            IImmutableSet<ModKey> modsWithCompactLeveledLists,
            ImmutableLoadOrderLinkCache<ISkyrimMod, ISkyrimModGetter> cache, UserSettings settings,
            IImmutableSet<ModKey> modsWithRequiemAsMaster)
        {
            var leveledCharacters = loadOrder.PriorityOrder.LeveledNpc().WinningOverrides();
            return new CompactLeveledListUnrolling<LeveledNpc, ILeveledNpcGetter, ILeveledNpcEntryGetter>(
                    new CompactLeveledCharacterUnroller(modsWithCompactLeveledLists))
                .AndThen(new LeveledListMerging<LeveledNpc, ILeveledNpcGetter, ILeveledNpcEntryGetter>(
                    settings.MergeLeveledLists, cache, modsWithRequiemAsMaster,
                    new CompactLeveledCharacterUnroller(modsWithCompactLeveledLists), new LeveledCharacterMerger()))
                .ProcessCollection(leveledCharacters);
        }

        private ErrorOr<ImmutableList<Armor>> PatchArmors(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder)
        {
            var armors = loadOrder.PriorityOrder.Armor().WinningOverrides();
            var armorRules = RecordUtils.LoadModConfigFiles(_context, "ArmorKeywordAssignments")
                .FlatMap(configs => configs.Select(x =>
                        AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(x.Item2, x.Item1))
                    .Aggregate(ImmutableList<AssignmentRule<IArmorGetter, IKeywordGetter>>.Empty.AsSuccess(),
                        (acc, elem) => acc.FlatMap(list => elem.Map(list.AddRange)))
                );

            return armorRules.Map(rules =>
                new ArmorTypeKeyword()
                    .AndThen(new ArmorRatingScaling(_reqtificatorConfig.ArmorSettings))
                    .AndThen(new ArmorKeywordsFromRules(rules))
                    .AndThen(new ProgressReporter<Armor, IArmorGetter>(_events))
                    .ProcessCollection(armors)
            );
        }

        private ErrorOr<ImmutableList<Weapon>> PatchWeapons(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder)
        {
            var weapons = loadOrder.PriorityOrder.Weapon().WinningOverrides();
            var weaponRules = RecordUtils.LoadModConfigFiles(_context, "WeaponKeywordAssignments")
                .FlatMap(configs => configs.Select(x =>
                        AssignmentsFromRules.LoadKeywordRules<IWeaponGetter>(x.Item2, x.Item1))
                    .Aggregate(ImmutableList<AssignmentRule<IWeaponGetter, IKeywordGetter>>.Empty.AsSuccess(),
                        (acc, elem) => acc.FlatMap(list => elem.Map(list.AddRange)))
                );
            return weaponRules.Map(rules =>
                new WeaponDamageScaling().AndThen(new WeaponMeleeRangeScaling())
                    .AndThen(new WeaponNpcAmmunitionUsage())
                    .AndThen(new WeaponRangedSpeedScaling())
                    .AndThen(new WeaponKeywordsFromRules(rules))
                    .AndThen(new ProgressReporter<Weapon, IWeaponGetter>(_events))
                    .ProcessCollection(weapons));
        }

        private ErrorOr<ImmutableList<Npc>> PatchActors(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder,
            ImmutableLoadOrderLinkCache<ISkyrimMod, ISkyrimModGetter> importedModsLinkCache)
        {
            var actorPerkRules = RecordUtils.LoadModConfigFiles(_context, "ActorAssignmentRules")
                .FlatMap(configs => configs.Select(x =>
                        AssignmentsFromRules.LoadPerkRules(x.Item2, x.Item1, importedModsLinkCache))
                    .Aggregate(ImmutableList<AssignmentRule<INpcGetter, IPerkGetter>>.Empty.AsSuccess(),
                        (acc, elem) => acc.FlatMap(list => elem.Map(list.AddRange)))
                );
            var actorSpellRules = RecordUtils.LoadModConfigFiles(_context, "ActorAssignmentRules")
                .FlatMap(configs => configs.Select(x =>
                        AssignmentsFromRules.LoadSpellRules(x.Item2, x.Item1, importedModsLinkCache))
                    .Aggregate(ImmutableList<AssignmentRule<INpcGetter, ISpellGetter>>.Empty.AsSuccess(),
                        (acc, elem) => acc.FlatMap(list => elem.Map(list.AddRange)))
                );
            var actorRules = actorPerkRules.FlatMap(perks => actorSpellRules.Map(spells => (perks, spells)));

            var actors = loadOrder.PriorityOrder.Npc().WinningOverrides();
            var globalPerks =
                RecordUtils.GetRecordsFromAllImports<IPerkGetter>(FormLists.GlobalPerks, importedModsLinkCache);
            return globalPerks.FlatMap(perks => actorRules.Map(rules =>
                new ActorVisualAutoMerge(importedModsLinkCache, loadOrder)
                    .AndThen(new ActorCommonScripts(importedModsLinkCache))
                    .AndThen(new ActorGlobalPerks(perks))
                    .AndThen(new ActorPerksFromRules(rules.perks))
                    .AndThen(new ActorSpellsFromRules(rules.spells))
                    .AndThen(new PlayerChanges(_reqtificatorConfig.PlayerConfig))
                    .ProcessCollection(actors)));
        }

        private static ImmutableList<LeveledNpc> PatchActorVariations(
            ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder,
            IImmutableList<Npc> patchedActors,
            ISkyrimMod targetMod)
        {
            var patchedActorsPseudoMod =
                new SkyrimMod(new ModKey("RTFI_patchedActors", ModType.Plugin), SkyrimRelease.SkyrimSE);
            var pseudoModListing = new ModListing<ISkyrimModGetter>(patchedActorsPseudoMod);
            patchedActors.ForEach(r => patchedActorsPseudoMod.Npcs.Add(r));
            var linkCacheWithPatchedActors = loadOrder.ListedOrder.Append(pseudoModListing)
                .ToImmutableLinkCache<ISkyrimMod, ISkyrimModGetter>();

            var variationsToGenerate = ActorVariationsGenerator.FindAllActorVariations(
                loadOrder.PriorityOrder.LeveledNpc().WinningOverrides(), linkCacheWithPatchedActors);

            var generatedVariations =
                ActorVariationsGenerator.BuildActorVariationContent(variationsToGenerate.ToImmutableList(),
                    linkCacheWithPatchedActors, targetMod);

            var updatedActorVariations = ActorVariationsGenerator.UpdateActorVariationLists(
                loadOrder.PriorityOrder.LeveledNpc().WinningOverrides(), generatedVariations.Item2,
                linkCacheWithPatchedActors);

            pseudoModListing.Dispose();
            return updatedActorVariations;
        }

        private static ImmutableList<Race> PatchRaces(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder)
        {
            var races = loadOrder.PriorityOrder.Race().WinningOverrides();
            return new CustomRacePatching().ProcessCollection(races);
        }
    }
}