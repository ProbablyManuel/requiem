using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda;
using Noggog;

namespace Reqtificator.Transformers
{
    internal abstract record Transformer<T, TI, TGetter> where T : MajorRecord, TI
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

        private T? PatchRecord(TGetter record)
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
}