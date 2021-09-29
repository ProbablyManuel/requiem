using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.LeveledLists;

namespace Reqtificator.Transformers.LeveledItems
{
    internal class LeveledItemMerger : ILeveledListMerger<LeveledItem, ILeveledItemGetter, ILeveledItemEntryGetter>
    {
        public IReadOnlyList<LeveledListMerging.LeveledListEntry> GetEntries(
            IReadOnlyList<ILeveledItemEntryGetter> input)
        {
            return input.Where(e => e.Data != null)
                .Select(e =>
                    new LeveledListMerging.LeveledListEntry(e.Data!.Reference.FormKey, e.Data!.Level, e.Data!.Count))
                .ToImmutableList();
        }

        public void SetEntries(LeveledItem input, IReadOnlyList<LeveledListMerging.LeveledListEntry> entries)
        {
            input.Entries = entries.Select(e => new LeveledItemEntry()
            {
                Data = new LeveledItemEntryData()
                { Count = (short)e.Count, Level = (short)e.Level, Reference = e.Reference.AsLink<IItemGetter>() }
            }).ToExtendedList();
        }
    }
}