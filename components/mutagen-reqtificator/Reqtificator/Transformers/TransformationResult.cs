using System;
using Mutagen.Bethesda;

namespace Reqtificator.Transformers
{
    abstract record TransformationResult<T, TGetter>
        where T : MajorRecord, TGetter where TGetter : IMajorRecordGetter
    {
        internal abstract TransformationResult<T, TGetter> Modify(Action<T> func);
        internal abstract TGetter Record();
    }

    record UnChanged<T, TGetter>(TGetter Underlying) : TransformationResult<T, TGetter> where T : MajorRecord, TGetter
        where TGetter : IMajorRecordGetter
    {
        internal override TransformationResult<T, TGetter> Modify(Action<T> func)
        {
            var mutable = (T)Underlying.DeepCopy();
            func(mutable);
            return new Modified<T, TGetter>(mutable);
        }

        internal override TGetter Record() => Underlying;
    }

    record Modified<T, TGetter>(T Underlying) : TransformationResult<T, TGetter> where T : MajorRecord, TGetter
        where TGetter : IMajorRecordGetter
    {
        internal override TransformationResult<T, TGetter> Modify(Action<T> func)
        {
            func(Underlying);
            return new Modified<T, TGetter>(Underlying);
        }

        internal override TGetter Record() => Underlying;
    }
}