using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins.Records;
using Noggog;

namespace Reqtificator.Transformers
{
    internal abstract class TransformerV2<T, TGetter>
        where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
    {
        public abstract TransformationResult<T, TGetter> Process(TransformationResult<T, TGetter> input);

        public ImmutableList<T> ProcessCollection(IEnumerable<TGetter> records)
        {
            return records.Select(PatchRecord)
                .WhereCastable<TransformationResult<T, TGetter>, Modified<T, TGetter>>()
                .Select(r => r.Underlying)
                .ToImmutableList();
        }

        private TransformationResult<T, TGetter> PatchRecord(TGetter record)
        {
            return LogUtils.WithRecordContextLog<TransformationResult<T, TGetter>>(record)(() =>
            {
                var initial = new UnChanged<T, TGetter>(record);
                return Process(initial);
            });
        }

        public TransformerV2<T, TGetter> AndThen(TransformerV2<T, TGetter> nextTransform)
        {
            return new ChainedTransformerV2<T, TGetter>(this, nextTransform);
        }
    }
}