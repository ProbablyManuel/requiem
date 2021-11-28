using Mutagen.Bethesda.Plugins.Records;

namespace Reqtificator.Events
{
    public record RecordProcessedResult<T>(T Record, bool Changed) where T : IMajorRecordGetter;
}