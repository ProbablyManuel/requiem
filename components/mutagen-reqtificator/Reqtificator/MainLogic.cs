using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Export;
using Reqtificator.Transformers;
using System.IO;
using System.Linq;

namespace Reqtificator
{
    internal static class MainLogic
    {
        public static SkyrimMod GeneratePatch(LoadOrder<IModListing<ISkyrimModGetter>> loadOrder, ModKey outputModKey)
        {
            var requiemModKey = new ModKey("Requiem", ModType.Plugin);
            var outputMod = new SkyrimMod(outputModKey, SkyrimRelease.SkyrimSE);

            var ammoRecords = loadOrder.PriorityOrder.Ammunition().WinningOverrides();
            var ammoPatched = new AmmunitionTransformer().ProcessCollection(ammoRecords);

            foreach (var patched in ammoPatched)
            {
                outputMod.Ammunitions.Add(patched);
            }

            var requiem = loadOrder.PriorityOrder.First(x => x.ModKey == requiemModKey);

            var version = new RequiemVersion(5, 0, 0, "a Phoenix perhaps?");
            PatchData.SetPatchHeadersAndVersion(requiem.Mod!, outputMod, version);

            return outputMod;
        }

        public static void WritePatchToDisk(SkyrimMod generatedPatch, string outputDirectory)
        {
            generatedPatch.WriteToBinaryParallel(Path.Combine(outputDirectory, generatedPatch.ModKey.FileName));
        }
    }
}