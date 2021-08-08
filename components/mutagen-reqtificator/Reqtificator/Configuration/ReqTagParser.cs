using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using System.Text.RegularExpressions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Exceptions;
using Serilog;

namespace Reqtificator.Configuration
{
    internal class ReqTagParser
    {
        private readonly IInternalEvents _events;

        public ReqTagParser(IInternalEvents events)
        {
            _events = events;
        }

        public IImmutableDictionary<ModKey, IImmutableSet<ReqTags>> ParseTagsFromModHeaders(
            ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder)
        {
            var regex = new Regex(
                "<<(?<legacy>REQ:\"[A-Za-z0-9]+\";\\s*)?(?<tags>REQ:(?:UNROLL|MUTATE|TEMPER)(?:;\\s*REQ:(?:UNROLL|MUTATE|TEMPER))?)>>");

            IImmutableSet<ReqTags> ExtractTags(ISkyrimModGetter mod)
            {
                var match = regex.Match(mod.ModHeader.Description ?? "");
                if (!match.Success) return ImmutableHashSet<ReqTags>.Empty;

                if (match.Groups["legacy"].Success)
                {
                    Log.Warning($"Mod {mod.ModKey} is using a deprecated version of ReqTag declaration");
                    _events.ReportDeprecationWarning(new ReqTagPrefixDeprecationWarning(mod.ModKey));
                }
                var tags = match.Groups["tags"].Value.Split(';').Select(s => s.Trim());
                return tags.Select(t => t switch
                {
                    "REQ:TEMPER" => ReqTags.TemperedItems,
                    "REQ:MUTATE" => ReqTags.ActorVariations,
                    "REQ:UNROLL" => ReqTags.CompactLeveledLists,
                    _ => throw new ArgumentException($"unexpected ReqTag value {t}")
                }).ToImmutableHashSet();
            }

            return loadOrder.ListedOrder
                .Select(ml => KeyValuePair.Create(ml.ModKey, ExtractTags(ml.Mod!)))
                .ToImmutableDictionary();
        }
    }

    internal enum ReqTags
    {
        ActorVariations,
        TemperedItems,
        CompactLeveledLists
    }
}