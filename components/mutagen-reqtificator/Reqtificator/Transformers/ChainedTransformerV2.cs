using Mutagen.Bethesda;

namespace Reqtificator.Transformers
{
    internal class ChainedTransformerV2<T, TGetter> : TransformerV2<T, TGetter> where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
    {
        private readonly TransformerV2<T, TGetter> _thisTransform;
        private readonly TransformerV2<T, TGetter> _nextTransform;

        public ChainedTransformerV2(TransformerV2<T, TGetter> thisTransform, TransformerV2<T, TGetter> nextTransform)
        {
            _thisTransform = thisTransform;
            _nextTransform = nextTransform;
        }

        public override TransformationResult<T, TGetter> Process(TransformationResult<T, TGetter> input)
        {
            return _nextTransform.Process(_thisTransform.Process(input));
        }
    }
}