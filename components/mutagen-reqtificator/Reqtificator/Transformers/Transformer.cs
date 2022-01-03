using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Exceptions;

namespace Reqtificator.Transformers
{
    internal abstract class Transformer<T, TI, TGetter>
        where T : MajorRecord, TI where TI : class, IMajorRecord, TGetter where TGetter : class, IMajorRecordGetter
    {
        public abstract TransformationResult<T, TGetter> Process(TransformationResult<T, TGetter> input);

        public ImmutableList<T> ProcessCollection(IEnumerable<IModContext<ISkyrimMod, ISkyrimModGetter, TI, TGetter>> records)
        {
            return records.Select(PatchRecord)
                .WhereCastable<TransformationResult<T, TGetter>, Modified<T, TGetter>>()
                .Select(r => r.Underlying)
                .ToImmutableList();
        }

        private TransformationResult<T, TGetter> PatchRecord(IModContext<ISkyrimMod, ISkyrimModGetter, TI, TGetter> context)
        {
            return LogUtils.WithRecordContextLog<TransformationResult<T, TGetter>>(context.Record)(() =>
            {
                try
                {
                    return Process(new UnChanged<T, TGetter>(context.Record));
                }
                catch (Exception ex)
                {
                    throw new RecordProcessingException<TGetter>(ex, context.Record, context.ModKey);
                }

            });
        }

        public Transformer<T, TI, TGetter> AndThen(Transformer<T, TI, TGetter> nextTransform)
        {
            return new ChainedTransformer<T, TI, TGetter>(this, nextTransform);
        }
    }
}