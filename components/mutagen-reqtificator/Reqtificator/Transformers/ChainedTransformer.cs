using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins.Records;

namespace Reqtificator.Transformers
{
    internal class ChainedTransformer<T, TI, TGetter> : Transformer<T, TI, TGetter> where T : MajorRecord, TI
        where TI : IMajorRecord, TGetter
        where TGetter : IMajorRecordGetter
    {
        private readonly Transformer<T, TI, TGetter> _thisTransform;
        private readonly Transformer<T, TI, TGetter> _nextTransform;

        public ChainedTransformer(Transformer<T, TI, TGetter> thisTransform,
            Transformer<T, TI, TGetter> nextTransform)
        {
            _thisTransform = thisTransform;
            _nextTransform = nextTransform;
        }

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

            if (_nextTransform.ShouldProcess(record))
            {
                _nextTransform.Process(record);
            }
        }
    }
}