using Mutagen.Bethesda.Plugins;

namespace Reqtificator.Gui
{
    public class ModViewModel
    {
        public string Name { get; }
        public bool NpcVisuals { get; set; }
        public bool RaceVisuals { get; set; }

        public ModViewModel(ModKey mod, bool npcVisuals, bool raceVisuals)
        {
            Name = mod.FileName;
            ModKey = mod;
            NpcVisuals = npcVisuals;
            RaceVisuals = raceVisuals;
        }

        public ModKey ModKey { get; }
    }
}
