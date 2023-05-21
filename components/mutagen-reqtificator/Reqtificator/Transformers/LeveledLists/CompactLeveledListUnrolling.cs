using Mutagen.Bethesda.Plugins.Records;

namespace Reqtificator.Transformers.LeveledLists
{
    internal class CompactLeveledListUnrolling<T, TI, TGetter, TEntry> : Transformer<T, TI, TGetter> where T : MajorRecord, TI where TI : class, IMajorRecord, TGetter where TGetter : class, IMajorRecordGetter
    {
        private readonly ICompactLeveledListUnroller<T, TGetter, TEntry> _unroller;

        public CompactLeveledListUnrolling(ICompactLeveledListUnroller<T, TGetter, TEntry> unroller)
        {
            _unroller = unroller;
        }

        public override TransformationResult<T, TGetter> Process(TransformationResult<T, TGetter> input)
        {
            if (!_unroller.IsCompactLeveledList(input.Record()))
            {
                return input;
            }

            return input.Modify(record => _unroller.UnrollLeveledList(record));
        }
    }
}