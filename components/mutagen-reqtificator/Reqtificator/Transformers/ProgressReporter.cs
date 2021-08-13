using Mutagen.Bethesda.Plugins.Records;
using Reqtificator.Events;

namespace Reqtificator.Transformers
{
    internal class ProgressReporter<T, TGetter> : TransformerV2<T, TGetter> where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
    {
        private readonly InternalEvents _eventQueue;

        public ProgressReporter(InternalEvents eventQueue)
        {
            _eventQueue = eventQueue;
        }

        public override TransformationResult<T, TGetter> Process(TransformationResult<T, TGetter> input)
        {
            _eventQueue.PublishRecordProcessed(input);
            return input;
        }
    }
}