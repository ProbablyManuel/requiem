using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Reqtificator.Export;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Actors;
using Reqtificator.Transformers.Armors;
using Reqtificator.Transformers.EncounterZones;
using Reqtificator.Transformers.Weapons;
using System.IO;
using System.Linq;

namespace Reqtificator
{
    internal static class MainLogic
    {
        public static SkyrimMod GeneratePatch(ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder,
            UserSettings userConfig, InternalEvents events, ReqtificatorConfig reqtificatorConfig, ModKey outputModKey)
        {
            var requiemModKey = new ModKey("Requiem", ModType.Plugin);
            var outputMod = new SkyrimMod(outputModKey, SkyrimRelease.SkyrimSE);
            var importedModsLinkCache = loadOrder.ToImmutableLinkCache();

            var numberOfRecords = loadOrder.PriorityOrder.Armor().WinningOverrides().Count() + loadOrder.PriorityOrder.Weapon().WinningOverrides().Count();
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
            var armorsPatched = new ArmorTypeKeyword()
                .AndThen(new ArmorRatingScaling(reqtificatorConfig.ArmorSettings))
                .AndThen(new ProgressReporter<Armor, IArmorGetter>(events))
                .ProcessCollection(armors);

            var weapons = loadOrder.PriorityOrder.Weapon().WinningOverrides();
            var weaponsPatched = new WeaponDamageScaling().AndThen(new WeaponMeleeRangeScaling())
                .AndThen(new WeaponNpcAmmunitionUsage())
                .AndThen(new WeaponRangedSpeedScaling())
                .AndThen(new ProgressReporter<Weapon, IWeaponGetter>(events))
                .ProcessCollection(weapons);

            var actors = loadOrder.PriorityOrder.Npc().WinningOverrides();
            var actorsPatched = new ActorCommonScripts(importedModsLinkCache).ProcessCollection(actors);

            encounterZonesPatched.ForEach(r => outputMod.EncounterZones.Add(r));
            doorsPatched.ForEach(r => outputMod.Doors.Add(r));
            containersPatched.ForEach(r => outputMod.Containers.Add(r));
            armorsPatched.ForEach(r => outputMod.Armors.Add(r));
            ammoPatched.ForEach(r => outputMod.Ammunitions.Add(r));
            weaponsPatched.ForEach(r => outputMod.Weapons.Add(r));
            actorsPatched.ForEach(r => outputMod.Npcs.Add(r));

            var requiem = loadOrder.PriorityOrder.First(x => x.ModKey == requiemModKey);

            var version = new RequiemVersion(5, 0, 0, "a Phoenix perhaps?");
            PatchData.SetPatchHeadersAndVersion(requiem.Mod!, outputMod, version);
            events.PublishPatchCompleted();

            return outputMod;
        }

        public static void WritePatchToDisk(SkyrimMod generatedPatch, string outputDirectory)
        {
            generatedPatch.WriteToBinaryParallel(Path.Combine(outputDirectory, generatedPatch.ModKey.FileName));
        }
    }
}