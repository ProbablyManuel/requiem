using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Hocon;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.Rules.Conditions;

namespace Reqtificator.Transformers.Rules
{
    internal class KeywordAssignmentRule<TMajor> : AssignmentRule<TMajor, IKeywordGetter>
        where TMajor : IMajorRecordGetter, IKeywordedGetter
    {
        internal KeywordAssignmentRule(IReadOnlySet<IAssignmentCondition<TMajor>> conditions,
            IReadOnlySet<IFormLinkGetter<IKeywordGetter>> assignments,
            IReadOnlySet<AssignmentRule<TMajor, IKeywordGetter>> subNodes,
            string nodeName) : base(conditions, assignments, subNodes, nodeName)
        {
        }
    }

    internal static class KeywordAssignmentRule
    {
        internal static IReadOnlyList<KeywordAssignmentRule<TMajor>> LoadFromConfigurationFile<TMajor>(Config config)
            where TMajor : IMajorRecordGetter, IKeywordedGetter
        {
            IAssignmentCondition<TMajor>? ConditionExtractor(HoconField content)
            {
                return content.Key switch
                {
                    "keywords_all" => new HasAllKeywords<TMajor>(content.Value.GetStringList()
                        .Select(s => new FormLink<IKeywordGetter>(FormKey.Factory(s)))
                        .ToImmutableHashSet<IFormLinkGetter<IKeywordGetter>>()),
                    _ => null
                };
            }

            KeywordAssignmentRule<TMajor> NodeExtractor(HoconField content)
            {
                var assignments = content.GetObject()
                    .GetOrDefault("keywords_assign")?.Value.GetStringList()
                    .Select(s => new FormLink<IKeywordGetter>(FormKey.Factory(s)))
                    .ToImmutableHashSet<IFormLinkGetter<IKeywordGetter>>();
                var conditions = ImmutableHashSet<IAssignmentCondition<TMajor>>.Empty;
                var subNodes = ImmutableHashSet<AssignmentRule<TMajor, IKeywordGetter>>.Empty;

                content.GetObject().ForEach(e =>
                {
                    var maybeCondition = ConditionExtractor(e.Value);
                    if (maybeCondition is not null) conditions = conditions.Add(maybeCondition);
                    else if (e.Key != "keywords_assign")
                    {
                        subNodes = subNodes.Add(NodeExtractor(e.Value));
                    }
                });
                return new KeywordAssignmentRule<TMajor>(
                    assignments: assignments ?? ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty,
                    conditions: conditions,
                    nodeName: content.Key,
                    subNodes: subNodes
                );
            }

            return config.Root.GetObject().Where(e => e.Key.StartsWith("feature_"))
                .Select(kv => NodeExtractor(kv.Value))
                .ToImmutableList();
        }
    }
}