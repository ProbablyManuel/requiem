using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.LeveledLists;

namespace Reqtificator.Transformers.LeveledCharacters
{
    internal class LeveledCharacterMerger : ILeveledListMerger<LeveledNpc, ILeveledNpcGetter, ILeveledNpcEntryGetter>
    {
        public IReadOnlyList<LeveledListMerging.LeveledListEntry> GetEntries(
            IReadOnlyList<ILeveledNpcEntryGetter> input)
        {
            return input.Where(e => e.Data != null)
                .Select(e =>
                    new LeveledListMerging.LeveledListEntry(e.Data!.Reference.FormKey, e.Data!.Level, e.Data!.Count))
                .ToImmutableList();
        }

        public void SetEntries(LeveledNpc input, IReadOnlyList<LeveledListMerging.LeveledListEntry> entries)
        {
            input.Entries = entries.Select(e => new LeveledNpcEntry()
            {
                Data = new LeveledNpcEntryData()
                {
                    Count = (short)e.Count,
                    Level = (short)e.Level,
                    Reference = e.Reference.ToLink<INpcSpawnGetter>()
                }
            }).ToExtendedList();
        }
    }
}