using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using System.Text.RegularExpressions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.LeveledLists;

namespace Reqtificator.Transformers.LeveledItems
{
    internal class CompactLeveledItemUnroller : ICompactLeveledListUnroller<LeveledItem, ILeveledItemGetter, ILeveledItemEntryGetter>
    {
        private readonly IImmutableSet<ModKey> _registeredMods;

        private static readonly Regex Pattern = new("^[a-zA-Z0-9]+_CLI_", RegexOptions.IgnoreCase);

        public CompactLeveledItemUnroller(IImmutableSet<ModKey> modsRegisteredForFeature)
        {
            _registeredMods = modsRegisteredForFeature;
        }

        public bool IsCompactLeveledList(ILeveledItemGetter input)
        {
            return input.EditorID is not null && Pattern.IsMatch(input.EditorID!) &&
                   _registeredMods.Contains(input.FormKey.ModKey) && input.Entries is not null;
        }

        public IReadOnlyList<ILeveledItemEntryGetter> GetUnrolledEntries(ILeveledItemGetter input)
        {
            if (input.Entries is null)
            {
                return ImmutableList<LeveledItemEntry>.Empty;
            }

            if (!IsCompactLeveledList(input))
            {
                return input.Entries;
            }

            return input.Entries!
                .Where(e => e.Data is not null)
                .SelectMany(e =>
                {
                    var newEntry = e.DeepCopy();
                    newEntry.Data!.Count = 1;
                    return Enumerable.Repeat(newEntry, e.Data!.Count);
                })
                .ToImmutableList();
        }

        public void UnrollLeveledList(LeveledItem input)
        {
            input.Entries = GetUnrolledEntries(input).Select(r => r.DeepCopy()).ToExtendedList();
        }
    }
}