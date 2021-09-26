using System.Collections.Generic;
using Mutagen.Bethesda.Plugins.Records;

namespace Reqtificator.Transformers.LeveledLists
{
    internal interface ICompactLeveledListUnroller<T, TGetter, TEntry> where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
    {
        public bool IsCompactLeveledList(TGetter input);

        public IReadOnlyList<TEntry> GetUnrolledEntries(TGetter input);

        public void UnrollLeveledList(T input);
    }
}