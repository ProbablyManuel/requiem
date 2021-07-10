using System.Collections.Generic;
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
            assignments: new HashSet<IFormLinkGetter<IKeywordGetter>> { AssignKeywordNode1 },
            conditions: new HashSet<IAssignmentCondition<TMajor>>
                {new HasAllKeywords<TMajor>(new HashSet<IFormLinkGetter<IKeywordGetter>> {CheckKeywordNode1})},
            nodeName: "some_node",
            subNodes: new HashSet<AssignmentRule<TMajor, IKeywordGetter>>()
        );

        internal static readonly AssignmentRule<TMajor, IKeywordGetter> Node2 = new(
            assignments: new HashSet<IFormLinkGetter<IKeywordGetter>> { AssignKeywordNode2 },
            conditions: new HashSet<IAssignmentCondition<TMajor>>
                {new HasAllKeywords<TMajor>(new HashSet<IFormLinkGetter<IKeywordGetter>> {CheckKeywordNode2})},
            nodeName: "other_node",
            subNodes: new HashSet<AssignmentRule<TMajor, IKeywordGetter>>()
        );

        internal static readonly AssignmentRule<TMajor, IKeywordGetter> TestRule = new(
            assignments: new HashSet<IFormLinkGetter<IKeywordGetter>>(),
            conditions: new HashSet<IAssignmentCondition<TMajor>>
                {new HasAllKeywords<TMajor>(new HashSet<IFormLinkGetter<IKeywordGetter>> {CheckKeywordRoot})},
            nodeName: "feature_foo",
            subNodes: new HashSet<AssignmentRule<TMajor, IKeywordGetter>> { Node1, Node2 }
        );
    }
}