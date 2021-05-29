using Mutagen.Bethesda;

namespace Reqtificator.Transformers
{
    internal record ChainedTransformer<T, TI, TGetter>(Transformer<T, TI, TGetter> ThisTransform,
        Transformer<T, TI, TGetter> NextTransform) : Transformer<T, TI, TGetter> where T : MajorRecord, TI
        where TI : IMajorRecord, TGetter
        where TGetter : IMajorRecordGetter
    {
        public override bool ShouldProcess(TGetter record)
        {
            return ThisTransform.ShouldProcess(record) || NextTransform.ShouldProcess(record);
        }

        public override void Process(TI record)
        {
            if (ThisTransform.ShouldProcess(record))
            {
                ThisTransform.Process(record);
            }

            if (NextTransform.ShouldProcess(record))
            {
                NextTransform.Process(record);
            }
        }
    }
}