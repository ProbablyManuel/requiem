using System;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;

namespace Reqtificator.Exceptions
{
    public abstract class ReqtificatorException : Exception
    {
    }

    public class InvalidRecordReferenceException<T> : ReqtificatorException where T : class, IMajorRecordGetter
    {
        public IFormLinkGetter<T> Unresolved { get; }

        public InvalidRecordReferenceException(IFormLinkGetter<T> unresolved)
        {
            Unresolved = unresolved;
        }
    };
}