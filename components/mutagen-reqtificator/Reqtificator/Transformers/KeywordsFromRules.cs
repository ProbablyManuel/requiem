using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Hocon;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Exceptions;
using Reqtificator.Transformers.Rules;
using Reqtificator.Transformers.Rules.Conditions;
using Serilog;

namespace Reqtificator.Transformers
{
    internal static class KeywordsFromRules
    {
        public static IImmutableList<AssignmentRule<TMajor, IKeywordGetter>> LoadFromConfigurationFile<TMajor>(
            Config config)
            where TMajor : IMajorRecordGetter, IKeywordedGetter
        {
            IReadOnlySet<IFormLinkGetter<T>> GetFormIdList<T>(HoconField rawValue) where T : class, IMajorRecordGetter
            {
                string FixSkyProcFormIdNotation(string original)
                {
                    Log.Warning($"now fixing oldschool ID '{original}'");
                    return original[6] == ' ' ? $"{original[..6]}:{original[7..]}" : original;
                }

                IReadOnlySet<IFormLinkGetter<T>> RecursiveExtractor(IHoconElement field)
                {
                    var formIds = field.Type switch
                    {
                        HoconType.Array => field.GetArray().SelectMany(RecursiveExtractor),
                        HoconType.Object => field.GetObject().SelectMany(x => RecursiveExtractor(x.Value)),
                        HoconType.String => new HashSet<IFormLinkGetter<T>>
                            {new FormLink<T>(FormKey.Factory(FixSkyProcFormIdNotation(field.GetString())))},
                        HoconType.Number => new HashSet<IFormLinkGetter<T>>
                            {new FormLink<T>(FormKey.Factory(FixSkyProcFormIdNotation(field.GetString())))},
                        _ => throw new ArgumentOutOfRangeException(nameof(field))
                    };
                    return formIds.ToImmutableHashSet();
                }

                return RecursiveExtractor(rawValue).ToImmutableHashSet();
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