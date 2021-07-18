using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Hocon;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Exceptions;
using Reqtificator.Transformers.Rules;
using Reqtificator.Transformers.Rules.Conditions;

namespace Reqtificator.Transformers
{
    internal static class AssignmentsFromRules
    {
        public static ErrorOr<ImmutableList<AssignmentRule<TMajor, IKeywordGetter>>> LoadKeywordRules<TMajor>(
            Config config, string sourceFile)
            where TMajor : IMajorRecordGetter, IKeywordedGetter
        {
            IImmutableSet<IFormLinkGetter<IKeywordGetter>>? AssignmentExtractor(HoconField content)
            {
                return content.Key switch
                {
                    "keywords_assign" => GetFormIdList<IKeywordGetter>(content, sourceFile),
                    _ => null
                };
            }

            IAssignmentCondition<TMajor>? ConditionExtractor(HoconField content)
            {
                try
                {
                    return content.Key switch
                    {
                        "keywords_all" =>
                            new HasAllKeywords<TMajor>(GetFormIdList<IKeywordGetter>(content, sourceFile)),
                        "keywords_any" => new HasAnyKeyword<TMajor>(GetFormIdList<IKeywordGetter>(content, sourceFile)),
                        "keywords_none" => new HasNoneOfKeywords<TMajor>(
                            GetFormIdList<IKeywordGetter>(content, sourceFile)),
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

        public static ErrorOr<ImmutableList<AssignmentRule<INpcGetter, IPerkGetter>>> LoadPerkRules(
            Config config, string sourceFile, ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache)
        {
            IImmutableSet<IFormLinkGetter<IPerkGetter>>? AssignmentExtractor(HoconField content)
            {
                return content.Key switch
                {
                    "perks_assign" => GetFormIdList<IPerkGetter>(content, sourceFile),
                    "spells_assign" => ImmutableHashSet<IFormLinkGetter<IPerkGetter>>.Empty, //ignore other assignments
                    _ => null
                };
            }

            var resolver = new ActorInheritanceGraphParser(linkCache);

            return AssignmentRule<INpcGetter, IPerkGetter>.LoadFromConfigurationFile(config, AssignmentExtractor,
                NpcConditionExtractor(sourceFile, resolver));
        }

        public static ErrorOr<ImmutableList<AssignmentRule<INpcGetter, ISpellGetter>>> LoadSpellRules(
            Config config, string sourceFile, ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache)
        {
            IImmutableSet<IFormLinkGetter<ISpellGetter>>? AssignmentExtractor(HoconField content)
            {
                return content.Key switch
                {
                    "spells_assign" => GetFormIdList<ISpellGetter>(content, sourceFile),
                    "perks_assign" => ImmutableHashSet<IFormLinkGetter<ISpellGetter>>.Empty, //ignore other assignments
                    _ => null
                };
            }

            var resolver = new ActorInheritanceGraphParser(linkCache);

            return AssignmentRule<INpcGetter, ISpellGetter>.LoadFromConfigurationFile(config, AssignmentExtractor,
                NpcConditionExtractor(sourceFile, resolver));
        }

        private static Func<HoconField, IAssignmentCondition<INpcGetter>?> NpcConditionExtractor(string sourceFile,
            IActorInheritanceGraphParser resolver)
        {
            return content =>
            {
                try
                {
                    return content.Key switch
                    {
                        "keywords_all" =>
                            new ActorHasAllKeywords(GetFormIdList<IKeywordGetter>(content, sourceFile), resolver),
                        "keywords_any" => new ActorHasAnyKeyword(GetFormIdList<IKeywordGetter>(content, sourceFile),
                            resolver),
                        "keywords_none" => new ActorHasNoneOfKeywords(
                            GetFormIdList<IKeywordGetter>(content, sourceFile), resolver),
                        "race_any" => new ActorHasAnyRace(GetFormIdList<IRaceGetter>(content, sourceFile), resolver),
                        "race_none" => new ActorHasNoneOfRaces(GetFormIdList<IRaceGetter>(content, sourceFile),
                            resolver),
                        _ => null
                    };
                }
                catch (ArgumentException e)
                {
                    throw new RuleConfigurationParsingException(sourceFile, content.Path.ToString(), e);
                }
            };
        }

        private static IImmutableSet<IFormLinkGetter<T>> GetFormIdList<T>(HoconField rawValue, string sourceFile)
            where T : class, IMajorRecordGetter
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
    }
}