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

namespace Reqtificator.Transformers
{
    internal static class KeywordsFromRules
    {
        public static ErrorOr<ImmutableList<AssignmentRule<TMajor, IKeywordGetter>>> LoadFromConfigurationFile<TMajor>(
            Config config, string sourceFile)
            where TMajor : IMajorRecordGetter, IKeywordedGetter
        {
            IReadOnlySet<IFormLinkGetter<T>> GetFormIdList<T>(HoconField rawValue) where T : class, IMajorRecordGetter
            {
                string FixSkyProcFormIdNotation(string original)
                {
                    return original[6] == ' ' ? $"{original[..6]}:{original[7..]}" : original;
                }

                IReadOnlySet<IFormLinkGetter<T>> RecursiveExtractor(IHoconElement field, string path)
                {
                    IFormLinkGetter<T> ExtractFormKey(string input)
                    {
                        try
                        {
                            return new FormLink<T>(FormKey.Factory(FixSkyProcFormIdNotation(input)));
                        }
                        catch (ArgumentException e)
                        {
                            throw new RuleConfigurationParsingException(sourceFile, path, e);
                        }
                    }

                    var formIds = field.Type switch
                    {
                        HoconType.Array => field.GetArray().SelectMany(x => RecursiveExtractor(x, path)),
                        HoconType.Object => field.GetObject().SelectMany(x => RecursiveExtractor(x.Value, path)),
                        HoconType.String => new HashSet<IFormLinkGetter<T>> { ExtractFormKey(field.GetString()) },
                        HoconType.Number => new HashSet<IFormLinkGetter<T>> { ExtractFormKey(field.GetString()) },
                        _ => throw new RuleConfigurationParsingException(sourceFile, path)
                    };
                    return formIds.ToImmutableHashSet();
                }

                return RecursiveExtractor(rawValue, rawValue.Path.ToString()).ToImmutableHashSet();
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
                try
                {
                    return content.Key switch
                    {
                        "keywords_all" => new HasAllKeywords<TMajor>(GetFormIdList<IKeywordGetter>(content)),
                        "keywords_any" => new HasAnyKeyword<TMajor>(GetFormIdList<IKeywordGetter>(content)),
                        "keywords_none" => new HasNoneOfKeywords<TMajor>(GetFormIdList<IKeywordGetter>(content)),
                        _ => null
                    };
                }
                catch (ArgumentException e)
                {
                    throw new RuleConfigurationParsingException(sourceFile, content.Path.ToString(), e);
                }
            }

            return AssignmentRule<TMajor, IKeywordGetter>.LoadFromConfigurationFile(config, AssignmentExtractor,
                ConditionExtractor);
        }
    }
}