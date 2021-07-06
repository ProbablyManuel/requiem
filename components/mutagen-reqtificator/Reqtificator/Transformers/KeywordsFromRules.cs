using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Hocon;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Transformers.Rules;
using Reqtificator.Transformers.Rules.Conditions;

namespace Reqtificator.Transformers
{
    internal static class KeywordsFromRules
    {
        public static IImmutableList<AssignmentRule<TMajor, IKeywordGetter>> LoadFromConfigurationFile<TMajor>(Config config)
            where TMajor : IMajorRecordGetter, IKeywordedGetter
        {
            IReadOnlySet<IFormLinkGetter<T>> GetFormIdList<T>(HoconField rawValue) where T : class, IMajorRecordGetter
            {
                return rawValue.Value.GetStringList()
                    .Select(s => new FormLink<T>(FormKey.Factory(s)))
                    .ToImmutableHashSet<IFormLinkGetter<T>>();
            }

            IReadOnlySet<IFormLinkGetter<IKeywordGetter>>? AssignmentExtractor(HoconField content)
            {
                return content.Key switch
                {
                    "keywords_assign" => GetFormIdList<IKeywordGetter>(content),
                    _ => null
                };
            }

            IAssignmentCondition<TMajor>? ConditionExtractor(HoconField content)
            {
                return content.Key switch
                {
                    "keywords_all" => new HasAllKeywords<TMajor>(GetFormIdList<IKeywordGetter>(content)),
                    "keywords_any" => new HasAnyKeyword<TMajor>(GetFormIdList<IKeywordGetter>(content)),
                    "keywords_none" => new HasNoneOfKeywords<TMajor>(GetFormIdList<IKeywordGetter>(content)),
                    _ => null
                };
            }

            return AssignmentRule<TMajor, IKeywordGetter>.LoadFromConfigurationFile(config, AssignmentExtractor,
                ConditionExtractor);
        }
    }
}