using Mutagen.Bethesda;

namespace Reqtificator.Transformers
{
    abstract record TransformationResult<T, TGetter>
        where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
    {
        internal abstract T ToMutableRecord();

        internal abstract TGetter Record();
    }

    record UnChanged<T, TGetter>(TGetter Underlying) : TransformationResult<T, TGetter> where T : MajorRecord, TGetter
        where TGetter : IMajorRecordGetter
    {
        internal override T ToMutableRecord() => (T)Underlying.DeepCopy();

        internal override TGetter Record() => Underlying;
    }

    record Modified<T, TGetter>(T Underlying) : TransformationResult<T, TGetter> where T : MajorRecord, TGetter
        where TGetter : IMajorRecordGetter
    {
        internal override T ToMutableRecord() => Underlying;

        internal override TGetter Record() => Underlying;
    }
}