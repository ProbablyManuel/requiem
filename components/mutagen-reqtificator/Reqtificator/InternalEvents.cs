using System;
using Mutagen.Bethesda.Plugins.Records;
using Reqtificator.Configuration;
using Reqtificator.Transformers;

namespace Reqtificator
{
    internal class InternalEvents
    {
        public event EventHandler<UserSettings> PatchRequested = delegate { };
        public event EventHandler<PatchCompleted> PatchCompleted = delegate { };
        public event EventHandler<Exception> ExceptionOccured = delegate { };

        public event EventHandler<RecordProcessedResult<IMajorRecordGetter>> RecordProcessed = delegate { };

        public void RequestPatch(UserSettings userSettings)
        {
            PatchRequested.Invoke(this, userSettings);
        }

        public void PublishPatchCompleted()
        {
            PatchCompleted.Invoke(this, new PatchCompleted());
        }

        public void ReportException(Exception ex)
        {
            ExceptionOccured.Invoke(this, ex);
        }

        public void PublishRecordProcessed<T, TGetter>(TransformationResult<T, TGetter> result)
            where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
        {
            RecordProcessed.Invoke(this,
                new RecordProcessedResult<IMajorRecordGetter>(result.Record(), result.IsModified()));
        }
    }

    public record PatchCompleted;

    public record RecordProcessedResult<T>(T Record, bool Changed) where T : IMajorRecordGetter;
}