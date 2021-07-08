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
                    return original[6] == ' ' ? $"{original[..6]}:{original[7..]}" : original;
                }

                bool IsUnqoutedSkyProcFormId(HoconArray array)
                {
                    if (array.Count == 1 && array[0].Children.Count == 3 && array[0][1] is HoconWhitespace)
                    {
                        return (array[0][0], array[0][2]) switch
                        {
                            (HoconLong formId, HoconUnquotedString mod) => true,
                            (HoconUnquotedString formId, HoconUnquotedString mod) => true,
                            _ => false
                        };
                    }

                    return false;
                }

                IReadOnlySet<IFormLinkGetter<T>> RecursiveExtractor(IHoconElement field)
                {
                    return field switch
                    {
                        HoconField hf => RecursiveExtractor(hf.Value),
                        HoconValue hv => hv.Children.SelectMany(RecursiveExtractor).ToImmutableHashSet(),
                        HoconArray arr when IsUnqoutedSkyProcFormId(arr) => new HashSet<IFormLinkGetter<T>>(),
                        HoconArray arr => arr.SelectMany(RecursiveExtractor).ToImmutableHashSet(),
                        HoconObject obj => obj.Values.SelectMany(RecursiveExtractor).ToImmutableHashSet(),
                        HoconQuotedString str => new HashSet<IFormLinkGetter<T>>
                            {new FormLink<T>(FormKey.Factory(FixSkyProcFormIdNotation(str.Value)))},
                        HoconUnquotedString str => new HashSet<IFormLinkGetter<T>>
                            {new FormLink<T>(FormKey.Factory(FixSkyProcFormIdNotation(str.Value)))},
                        HoconTripleQuotedString str => new HashSet<IFormLinkGetter<T>>
                            {new FormLink<T>(FormKey.Factory(FixSkyProcFormIdNotation(str.Value)))},
                        // TODO: get information about origin file
                        _ => throw new RuleConfigurationParsingException("unknown origin :/", rawValue.Path.ToString())
                    };
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