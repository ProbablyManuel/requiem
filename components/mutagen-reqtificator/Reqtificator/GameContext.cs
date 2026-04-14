using System;
using System.Collections.Immutable;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;

namespace Reqtificator
{
    internal record GameContext(ImmutableList<ILoadOrderListingGetter> ActiveMods, string DataFolder, GameRelease Release)
    {
        public static bool IsAvailable(GameRelease release)
        {
            return PluginListings.TryGetListingsFile(release, out _);
        }

        public static GameContext GetRequiemContext(GameRelease release, Collection<ModKeyStruct> patchesToBeGenerated)
        {
            if (patchesToBeGenerated == null)
            {
                throw new ArgumentNullException(nameof(patchesToBeGenerated));
            }
            string dataFolder = Directory.GetCurrentDirectory();

            var modKeys = new Collection<ModKey>();
            foreach (var modKey in patchesToBeGenerated)
            {
                modKeys.Add(modKey.PluginKey);
            }

            var loadOrderEntries = LoadOrder.GetLoadOrderListings(release, dataFolder, true);
            var activeMods = loadOrderEntries.OnlyEnabled().TakeWhile(it => !modKeys.Contains(it.ModKey))
                .ToImmutableList();

            return new GameContext(activeMods, dataFolder, release);
        }
    }
}