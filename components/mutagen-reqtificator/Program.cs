using System;
using System.Collections.Generic;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Synthesis;
using Mutagen.Bethesda.Skyrim;
using System.Threading.Tasks;

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
            foreach (var npcGetter in state.LoadOrder.PriorityOrder.Npc().WinningOverrides())
            {
                var npc = state.PatchMod.Npcs.GetOrAddAsOverride(npcGetter);
                npc.Height /= 4;
                npc.Equals(npcGetter);
            }
            System.Console.WriteLine("Done!  Press enter to your self-compiled Mutagen patcher.");
            System.Console.ReadLine();
        }
    }
}
