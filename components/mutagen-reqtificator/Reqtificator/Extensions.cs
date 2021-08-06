using System;
using System.Collections.Generic;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Noggog;

namespace Reqtificator
{
    internal static class EnumerableExtensions
    {
        public static IEnumerable<TSource> Tap<TSource>(this IEnumerable<TSource> source, Action<TSource> todo)
        {
            return source.Select(e =>
            {
                todo(e);
                return e;
            });
        }

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

    internal static class SkyrimModExtensions
    {
        public static SkyrimMod WithRecords<TMajor>(this SkyrimMod mod, IEnumerable<TMajor> records)
            where TMajor : MajorRecord
        {
            var newMod = new SkyrimMod(mod.ModKey, mod.SkyrimRelease);
            newMod.DeepCopyIn(mod); //see https://github.com/Mutagen-Modding/Mutagen/issues/192
            var cache = newMod.GetGroup<TMajor>();
            records.ForEach(cache.Add);
            return newMod;
        }
    }
}