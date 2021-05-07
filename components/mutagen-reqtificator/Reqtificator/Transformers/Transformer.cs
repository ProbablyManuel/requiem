using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda;
using Noggog;

namespace Reqtificator.Transformers
{
    public abstract class Transformer<T, TGetter> where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
    {
        public abstract bool ShouldProcess(TGetter record);

        public abstract void Process(T record);

        public ImmutableList<T> ProcessCollection(IEnumerable<TGetter> records)
        {
            return records.Select(PatchRecord)
                .NotNull()
                .ToImmutableList();
        }

        public T? PatchRecord(TGetter record)
        {
            if (ShouldProcess(record))
            {
                var copy = (T) record.DeepCopy();
                Process(copy);
                return copy;
            }

            return null;
        }

        public Transformer<T, TGetter> AndThen(Transformer<T, TGetter> nextOperation)
        {
            return new ChainedTransformer<T, TGetter>(this, nextOperation);
        }

        class ChainedTransformer<T, TGetter> : Transformer<T, TGetter> where T : MajorRecord, TGetter
            where TGetter : IMajorRecordGetter
        {
            public ChainedTransformer(Transformer<T, TGetter> thisTransform, Transformer<T, TGetter> nextTransform)
            {
                _thisTransform = thisTransform;
                _nextTransform = nextTransform;
            }

            private readonly Transformer<T, TGetter> _thisTransform;
            private readonly Transformer<T, TGetter> _nextTransform;

            public override bool ShouldProcess(TGetter record)
            {
                return _thisTransform.ShouldProcess(record) || _nextTransform.ShouldProcess(record);
            }

            public override void Process(T record)
            {
                if (_thisTransform.ShouldProcess(record))
                {
                    _thisTransform.Process(record);
                }

                //TODO: strictly speaking, this check does take into account changes made by the previous step...
                if (_nextTransform.ShouldProcess(record))
                {
                    _nextTransform.Process(record);
                }
            }
        }
    }
}