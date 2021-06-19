using Mutagen.Bethesda;

namespace Reqtificator.Transformers
{
    abstract record TransformationResult<T, TGetter>
        where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
    {
        internal abstract T GetMutableRecord();

        internal abstract TGetter GetRecord();
    }

    record UnChanged<T, TGetter>(TGetter Record) : TransformationResult<T, TGetter> where T : MajorRecord, TGetter
        where TGetter : IMajorRecordGetter
    {
        internal override T GetMutableRecord() => (T)Record.DeepCopy();

        internal override TGetter GetRecord() => Record;
    }

    record Modified<T, TGetter>(T Record) : TransformationResult<T, TGetter> where T : MajorRecord, TGetter
        where TGetter : IMajorRecordGetter
    {
        internal override T GetMutableRecord() => Record;

        internal override TGetter GetRecord() => Record;
    }
}