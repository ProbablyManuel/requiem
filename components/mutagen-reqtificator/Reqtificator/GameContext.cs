using System.Collections.Immutable;
using System.IO;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Installs;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;

namespace Reqtificator
{
    public record GameContext(ImmutableList<IModListingGetter> ActiveMods, string DataFolder, GameRelease Release)
    {
        public static GameContext GetRequiemContext(GameRelease release, ModKey patchToBeGenerated)
        {
            if (!GameLocations.TryGetGameFolder(release, out var gameFolder))
            {
                throw new DirectoryNotFoundException("Could not locate game folder automatically.");
            }

            var dataFolder = Path.Combine(gameFolder, "Data");
            if (!PluginListings.TryGetListingsFile(release, out var path))
            {
                throw new FileNotFoundException("Could not locate load order automatically.");
            }

            var loadOrderEntries = LoadOrder.GetListings(release, dataFolder, true);
            var activeMods = loadOrderEntries.OnlyEnabled().TakeWhile(it => it.ModKey != patchToBeGenerated)
                .ToImmutableList();

            return new GameContext(activeMods, dataFolder, release);
        }
    }
}