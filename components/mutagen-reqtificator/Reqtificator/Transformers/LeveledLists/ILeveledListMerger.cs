using System.Collections.Generic;
using Mutagen.Bethesda.Plugins.Records;

namespace Reqtificator.Transformers.LeveledLists
{
    internal interface ILeveledListMerger<T, TGetter, TEntry> where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
    {
        public IReadOnlyList<LeveledListMerging.LeveledListEntry> GetEntries(IReadOnlyList<TEntry> input);

        public void SetEntries(T input, IReadOnlyList<LeveledListMerging.LeveledListEntry> entries);
    }
}