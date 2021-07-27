using System.Collections.Immutable;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Transformers.Rules;
using Reqtificator.Transformers.Rules.Conditions;

namespace ReqtificatorTest.Transformers.Rules
{
    internal static class AssignmentRuleFixture<TMajor> where TMajor : IMajorRecordGetter, IKeywordedGetter
    {
        internal static readonly IFormLinkGetter<IKeywordGetter> CheckKeywordNode1 =
            new FormLink<IKeywordGetter>(FormKey.Factory("123ABC:Skyrim.esm"));

        internal static readonly IFormLinkGetter<IKeywordGetter> CheckKeywordNode2 =
            new FormLink<IKeywordGetter>(FormKey.Factory("123DEF:Skyrim.esm"));

        internal static readonly IFormLinkGetter<IKeywordGetter> CheckKeywordRoot =
            new FormLink<IKeywordGetter>(FormKey.Factory("123456:Skyrim.esm"));

        internal static readonly IFormLinkGetter<IKeywordGetter> AssignKeywordNode1 =
            new FormLink<IKeywordGetter>(FormKey.Factory("ABC123:Assign.esp"));

        internal static readonly IFormLinkGetter<IKeywordGetter> AssignKeywordNode2 =
            new FormLink<IKeywordGetter>(FormKey.Factory("DEF123:Assign.esp"));

        internal static readonly AssignmentRule<TMajor, IKeywordGetter> Node1 = new(
            assignments: ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty.Add(AssignKeywordNode1),
            conditions: ImmutableHashSet<IAssignmentCondition<TMajor>>.Empty.Add(
                new HasAllKeywords<TMajor>(
                    ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty.Add(CheckKeywordNode1))),
            nodeName: "some_node",
            subNodes: ImmutableHashSet<AssignmentRule<TMajor, IKeywordGetter>>.Empty
        );

        internal static readonly AssignmentRule<TMajor, IKeywordGetter> Node2 = new(
            assignments: ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty.Add(AssignKeywordNode2),
            conditions: ImmutableHashSet<IAssignmentCondition<TMajor>>.Empty.Add(
                new HasAllKeywords<TMajor>(
                    ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty.Add(CheckKeywordNode2))),
            nodeName: "other_node",
            subNodes: ImmutableHashSet<AssignmentRule<TMajor, IKeywordGetter>>.Empty
        );

        internal static readonly AssignmentRule<TMajor, IKeywordGetter> TestRule = new(
            assignments: ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty,
            conditions: ImmutableHashSet<IAssignmentCondition<TMajor>>.Empty.Add(
                new HasAllKeywords<TMajor>(
                    ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty.Add(CheckKeywordRoot))),
            nodeName: "feature_foo",
            subNodes: ImmutableHashSet<AssignmentRule<TMajor, IKeywordGetter>>.Empty.Add(Node1).Add(Node2)
        );
    }
}