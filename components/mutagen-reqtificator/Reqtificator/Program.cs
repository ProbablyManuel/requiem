using System;
using System.Linq;
using System.Threading.Tasks;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Mutagen.Bethesda.Synthesis;
using Reqtificator.Export;
using Reqtificator.Transformers;

//TODO: figure out why I need to do this when adding Mutagen as a dependency
namespace System.Runtime.CompilerServices
{
    internal record IsExternalInit;
}

namespace Reqtificator
{
    public class Program
    {
        public static async Task<int> Main(string[] args)
        {
            return await SynthesisPipeline.Instance
                .AddPatch<ISkyrimMod, ISkyrimModGetter>(RunPatch)
                .SetTypicalOpen(GameRelease.SkyrimSE, "Requiem for the Mutated.esp")
                .Run(args);
        }

        public static void RunPatch(IPatcherState<ISkyrimMod, ISkyrimModGetter> state)
        {
            //TODO: refactor this into a nice verification function
            var requiemModKey = new ModKey("Requiem", ModType.Plugin);

            if (state.LoadOrder.PriorityOrder.All(x => x.ModKey != requiemModKey))
            {
                Console.WriteLine("oops, where's Requiem.esp? -- hit enter to abort the patcher");
                Console.ReadLine();
                return;
            }

            var ammoRecords = state.LoadOrder.PriorityOrder.Ammunition().WinningOverrides();
            var ammoPatched = new AmmunitionTransformer().ProcessCollection(ammoRecords);

            foreach (var patched in ammoPatched)
            {
                state.PatchMod.Ammunitions.Add(patched);
            }

            var requiem = state.LoadOrder.PriorityOrder.First(x => x.ModKey == requiemModKey);

            var version = new RequiemVersion(5, 0, 0, "a Phoenix perhaps?");
            var processor = new PatchData();
            processor.SetPatchHeadersAndVersion(requiem.Mod!, state.PatchMod, version);

            Console.WriteLine("Done! Press enter to finish your self-compiled Mutagen patcher.");
            Console.ReadLine();
        }
    }
}