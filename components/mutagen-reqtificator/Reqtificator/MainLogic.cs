using System.Collections.Immutable;
using System.IO;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Reqtificator.Export;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Actors;
using Reqtificator.Transformers.Armors;
using Reqtificator.Transformers.EncounterZones;
using Reqtificator.Transformers.Rules;
using Reqtificator.Transformers.Weapons;

namespace Reqtificator
{
    internal static class MainLogic
    {
        public static ErrorOr<SkyrimMod> GeneratePatch(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder,
            UserSettings userConfig, InternalEvents events, ReqtificatorConfig reqtificatorConfig, GameContext context,
            ModKey outputModKey)
        {
            var requiemModKey = new ModKey("Requiem", ModType.Plugin);
            var importedModsLinkCache = loadOrder.ToImmutableLinkCache();

            var numberOfRecords = loadOrder.PriorityOrder.Armor().WinningOverrides().Count() +
                                  loadOrder.PriorityOrder.Weapon().WinningOverrides().Count();
            events.PublishPatchStarted(numberOfRecords);

            var ammoRecords = loadOrder.PriorityOrder.Ammunition().WinningOverrides();
            var ammoPatched = new AmmunitionTransformer().ProcessCollection(ammoRecords);

            var encounterZones = loadOrder.PriorityOrder.EncounterZone().WinningOverrides();
            var encounterZonesPatched =
                new OpenCombatBoundaries(loadOrder, userConfig).ProcessCollection(encounterZones);

            var doors = loadOrder.PriorityOrder.Door().WinningOverrides();
            var doorsPatched = new CustomLockpicking<Door, IDoor, IDoorGetter>().ProcessCollection(doors);

            var containers = loadOrder.PriorityOrder.Container().WinningOverrides();
            var containersPatched =
                new CustomLockpicking<Container, IContainer, IContainerGetter>().ProcessCollection(containers);

            var armors = loadOrder.PriorityOrder.Armor().WinningOverrides();
            var armorRules = Utils.LoadModConfigFiles(context, "ArmorKeywordAssignments")
                .FlatMap(configs => configs.Select(x =>
                        AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(x.Item2, x.Item1))
                    .Aggregate(ImmutableList<AssignmentRule<IArmorGetter, IKeywordGetter>>.Empty.AsSuccess(),
                        (acc, elem) => acc.FlatMap(list => elem.Map(list.AddRange)))
                );
            var armorsPatched = armorRules.Map(rules =>
                new ArmorTypeKeyword()
                    .AndThen(new ArmorRatingScaling(reqtificatorConfig.ArmorSettings))
                    .AndThen(new ArmorKeywordsFromRules(rules))
                    .AndThen(new ProgressReporter<Armor, IArmorGetter>(events))
                    .ProcessCollection(armors)
            );

            var weapons = loadOrder.PriorityOrder.Weapon().WinningOverrides();
            var weaponRules = Utils.LoadModConfigFiles(context, "WeaponKeywordAssignments")
                .FlatMap(configs => configs.Select(x =>
                        AssignmentsFromRules.LoadKeywordRules<IWeaponGetter>(x.Item2, x.Item1))
                    .Aggregate(ImmutableList<AssignmentRule<IWeaponGetter, IKeywordGetter>>.Empty.AsSuccess(),
                        (acc, elem) => acc.FlatMap(list => elem.Map(list.AddRange)))
                );
            var weaponsPatched = weaponRules.Map(rules =>
                new WeaponDamageScaling().AndThen(new WeaponMeleeRangeScaling())
                    .AndThen(new WeaponNpcAmmunitionUsage())
                    .AndThen(new WeaponRangedSpeedScaling())
                    .AndThen(new WeaponKeywordsFromRules(rules))
                    .AndThen(new ProgressReporter<Weapon, IWeaponGetter>(events))
                    .ProcessCollection(weapons));

            var actorPerkRules = Utils.LoadModConfigFiles(context, "ActorAssignmentRules")
                .FlatMap(configs => configs.Select(x =>
                        AssignmentsFromRules.LoadPerkRules(x.Item2, x.Item1, importedModsLinkCache))
                    .Aggregate(ImmutableList<AssignmentRule<INpcGetter, IPerkGetter>>.Empty.AsSuccess(),
                        (acc, elem) => acc.FlatMap(list => elem.Map(list.AddRange)))
                );
            var actorSpellRules = Utils.LoadModConfigFiles(context, "ActorAssignmentRules")
                .FlatMap(configs => configs.Select(x =>
                        AssignmentsFromRules.LoadSpellRules(x.Item2, x.Item1, importedModsLinkCache))
                    .Aggregate(ImmutableList<AssignmentRule<INpcGetter, ISpellGetter>>.Empty.AsSuccess(),
                        (acc, elem) => acc.FlatMap(list => elem.Map(list.AddRange)))
                );
            var actorRules = actorPerkRules.FlatMap(perks => actorSpellRules.Map(spells => (perks, spells)));

            var actors = loadOrder.PriorityOrder.Npc().WinningOverrides();
            var globalPerks = Utils.GetRecordsFromAllImports<IPerkGetter>(FormLists.GlobalPerks, importedModsLinkCache);
            var actorsPatched = globalPerks.FlatMap(perks => actorRules.Map(rules =>
                new ActorCommonScripts(importedModsLinkCache)
                    .AndThen(new ActorGlobalPerks(perks))
                    .AndThen(new ActorPerksFromRules(rules.perks))
                    .AndThen(new ActorSpellsFromRules(rules.spells))
                    .AndThen(new PlayerChanges(reqtificatorConfig.PlayerConfig))
                    .ProcessCollection(actors)));

            var outputMod = new SkyrimMod(outputModKey, SkyrimRelease.SkyrimSE).AsSuccess()
                .Map(m => m.WithRecords(encounterZonesPatched))
                .Map(m => m.WithRecords(doorsPatched))
                .Map(m => m.WithRecords(containersPatched))
                .FlatMap(m => armorsPatched.Map(m.WithRecords))
                .Map(m => m.WithRecords(ammoPatched))
                .FlatMap(m => weaponsPatched.Map(m.WithRecords))
                .FlatMap(m => actorsPatched.Map(m.WithRecords));

            var requiem = loadOrder.PriorityOrder.First(x => x.ModKey == requiemModKey);

            var version = new RequiemVersion(5, 0, 0, "a Phoenix perhaps?");
            _ = outputMod.Map(m =>
            {
                PatchData.SetPatchHeadersAndVersion(requiem.Mod!, m, version);
                return m;
            });

            return outputMod;
        }


        public static void WritePatchToDisk(SkyrimMod generatedPatch, string outputDirectory)
        {
            generatedPatch.WriteToBinaryParallel(Path.Combine(outputDirectory, generatedPatch.ModKey.FileName));
        }
    }
}