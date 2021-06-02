using Mutagen.Bethesda;
using Noggog;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;

namespace Reqtificator.Transformers
{
    public abstract class Transformer<T, TI, TGetter> where T : MajorRecord, TI
        where TI : IMajorRecord, TGetter
        where TGetter : IMajorRecordGetter
    {
        public abstract bool ShouldProcess(TGetter record);

        public abstract void Process(TI record);

        public ImmutableList<T> ProcessCollection(IEnumerable<TGetter> records)
        {
            return records.Select(PatchRecord)
                .NotNull()
                .ToImmutableList();
        }

        public T? PatchRecord(TGetter record)
        {
            return LogUtils.WithRecordContextLog<T?>(record)(() =>
            {
                if (ShouldProcess(record))
                {
                    var copy = (T)record.DeepCopy();
                    Process(copy);
                    return copy;
                }

                return null;
            });
        }

        public Transformer<T, TI, TGetter> AndThen(Transformer<T, TI, TGetter> nextOperation)
        {
            return new ChainedTransformer<T, TI, TGetter>(this, nextOperation);
        }
    }

    class ChainedTransformer<T, TI, TGetter> : Transformer<T, TI, TGetter> where T : MajorRecord, TI
        where TI : IMajorRecord, TGetter
        where TGetter : IMajorRecordGetter
    {
        public ChainedTransformer(Transformer<T, TI, TGetter> thisTransform, Transformer<T, TI, TGetter> nextTransform)
        {
            _thisTransform = thisTransform;
            _nextTransform = nextTransform;
        }

        private readonly Transformer<T, TI, TGetter> _thisTransform;
        private readonly Transformer<T, TI, TGetter> _nextTransform;

        public override bool ShouldProcess(TGetter record)
        {
            return _thisTransform.ShouldProcess(record) || _nextTransform.ShouldProcess(record);
        }

        public override void Process(TI record)
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