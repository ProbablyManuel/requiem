using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.LeveledLists
{
    internal class CompactLeveledListUnrolling<T, TGetter, TEntry> : TransformerV2<T, TGetter> where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
    {
        private readonly ICompactLeveledListUnroller<T, TGetter, TEntry> _unroller;

        public CompactLeveledListUnrolling(ICompactLeveledListUnroller<T, TGetter, TEntry> unroller)
        {
            _unroller = unroller;
        }

        public override TransformationResult<T, TGetter> Process(TransformationResult<T, TGetter> input)
        {
            if (!_unroller.IsCompactLeveledList(input.Record())) return input;

            return input.Modify(record => _unroller.UnrollLeveledList(record));
        }
    }
}