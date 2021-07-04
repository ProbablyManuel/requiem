using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;
using Reqtificator.Configuration;
using Reqtificator.Transformers;
using System;
using System.Collections.Generic;

namespace Reqtificator
{
    internal class InternalEvents
    {
        public event EventHandler<PatchContext> ReadyToPatch = delegate { };
        public event EventHandler<UserSettings> PatchRequested = delegate { };
        public event EventHandler<PatchStarted> PatchStarted = delegate { };
        public event EventHandler<PatchCompleted> PatchCompleted = delegate { };
        public event EventHandler<Exception> ExceptionOccured = delegate { };

        public event EventHandler<RecordProcessedResult<IMajorRecordGetter>> RecordProcessed = delegate { };

        internal void PublishReadyToPatch(UserSettings userConfig, IEnumerable<ModKey> activeMods)
        {
            ReadyToPatch.Invoke(this, new PatchContext(userConfig, activeMods));
        }

        public void RequestPatch(UserSettings userSettings)
        {
            PatchRequested.Invoke(this, userSettings);
        }
        public void PublishPatchStarted(int numberOfRecords)
        {
            PatchStarted.Invoke(this, new PatchStarted(numberOfRecords));
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

    public record PatchContext(UserSettings UserSettings, IEnumerable<ModKey> ActiveMods);
    public record PatchStarted(int NumberOfRecords);
    public record PatchCompleted;
    public record RecordProcessedResult<T>(T Record, bool Changed) where T : IMajorRecordGetter;
}