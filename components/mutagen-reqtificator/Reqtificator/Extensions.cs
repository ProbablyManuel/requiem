using System.Collections.Generic;
using Mutagen.Bethesda;

namespace Reqtificator
{
    public static class RecordListExtensions
    {
        public static bool ContainsNot<TMajor>(this IReadOnlyList<IFormLinkGetter<TMajor>> list,
            FormKey formKey)
            where TMajor : class, IMajorRecordCommonGetter
        {
            return !list.Contains(formKey);
        }
    }
}