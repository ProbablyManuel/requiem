using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;
using Reqtificator.Configuration;
using Reqtificator.Exceptions;
using Reqtificator.Transformers;

namespace Reqtificator
{
    internal interface IInternalEvents
    {
        public void PublishReadyToPatch(UserSettings userConfig, IEnumerable<ModKey> activeMods);
        public void RequestPatch(UserSettings userSettings);
        public void PublishPatchStarted(int numberOfRecords);
        public void PublishFinished(PatchStatus status, ImmutableList<string> arguments);
        public void ReportException(Exception ex);
        public void ReportDeprecationWarning(IDeprecationWarning warning);

        public void PublishRecordProcessed<T, TGetter>(TransformationResult<T, TGetter> result)
            where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter;
    }

    internal class InternalEvents : IInternalEvents
    {
        public event EventHandler<PatchContext> ReadyToPatch = delegate { };
        public event EventHandler<UserSettings> PatchRequested = delegate { };
        public event EventHandler<PatchStarted> PatchStarted = delegate { };
        public event EventHandler<PatchingFinished> PatchingFinished = delegate { };
        public event EventHandler<Exception> ExceptionOccured = delegate { };
        public event EventHandler<IDeprecationWarning> DeprecationWarningOccured = delegate { };

        public event EventHandler<RecordProcessedResult<IMajorRecordGetter>> RecordProcessed = delegate { };

        public void PublishReadyToPatch(UserSettings userConfig, IEnumerable<ModKey> activeMods)
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

        public void PublishFinished(PatchStatus status, ImmutableList<string> arguments)
        {
            PatchingFinished.Invoke(this, new PatchingFinished(status, arguments));
        }

        public void ReportException(Exception ex)
        {
            ExceptionOccured.Invoke(this, ex);
        }

        public void ReportDeprecationWarning(IDeprecationWarning warning)
        {
            DeprecationWarningOccured.Invoke(this, warning);
        }

        public void PublishRecordProcessed<T, TGetter>(TransformationResult<T, TGetter> result)
            where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
        {
            RecordProcessed.Invoke(this,
                new RecordProcessedResult<IMajorRecordGetter>(result.Record(), result.IsModified()));
        }
    }

    public enum PatchStatus
    {
        Success,
        MissingRequiem,
        GeneralError
    }

    public record PatchContext(UserSettings UserSettings, IEnumerable<ModKey> ActiveMods);

    public record PatchStarted(int NumberOfRecords);

    public record PatchingFinished(PatchStatus Status, ImmutableList<string> Arguments);

    public record RecordProcessedResult<T>(T Record, bool Changed) where T : IMajorRecordGetter;
}