using System.Collections.Generic;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator
{
    internal static class EnumerableExtensions
    {
        public static bool ContainsNot<TSource>(this IEnumerable<TSource> source, TSource value)
        {
            return !source.Contains(value);
        }

        public static bool ContainsNot<TMajor>(this IReadOnlyList<IFormLinkGetter<TMajor>> list,
            FormKey formKey)
            where TMajor : class, IMajorRecordCommonGetter
        {
            return !list.Contains(formKey);
        }
    }

    internal static class FormLinkExtensions
    {
        public static bool IsNotNull<TMajor>(this IFormLinkNullableGetter<TMajor> link)
            where TMajor : class, IMajorRecordCommonGetter
        {
            return !link.IsNull;
        }
    }

    internal static class KeywordedRecordExtensions
    {
        public static bool HasKeyword<TMajor>(this TMajor record, IFormLinkGetter<IKeywordGetter> keyword)
            where TMajor : IMajorRecordGetter, IKeywordedGetter<IKeywordGetter>
        {
            return record.Keywords?.Contains(keyword.FormKey) ?? false;
        }
    }

}