using System.Collections.Immutable;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Exceptions;

namespace Reqtificator
{
    internal static class Utils
    {
        public static ErrorOr<IImmutableSet<IFormLinkGetter<TGetter>>> GetRecordsFromAllImports<TGetter>(
            IFormLinkGetter<IFormListGetter> formListLink,
            ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache) where TGetter : class, ISkyrimMajorRecordGetter
        {
            IImmutableSet<IFormLinkGetter<TGetter>> records = ImmutableHashSet<IFormLinkGetter<TGetter>>.Empty;
            foreach (var recordVersion in formListLink.ResolveAll(linkCache))
            {
                foreach (var entry in recordVersion.Items)
                {
                    if (entry.TryResolve<TGetter>(linkCache, out var resolved))
                    {
                        records = records.Add(resolved.AsLink());
                    }
                    else
                    {
                        var error = new InvalidRecordReferenceException<TGetter>(new FormLink<TGetter>(entry.FormKey));
                        return new Failed<IImmutableSet<IFormLinkGetter<TGetter>>>(error);
                    }
                }
            }

            return records.AsSuccess();
        }
    }
}