using System.Collections.Generic;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Transformers.Rules;
using Reqtificator.Transformers.Rules.Conditions;

namespace ReqtificatorTest.Transformers.Rules
{
    internal static class AssignmentRuleFixture
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

        internal static readonly AssignmentRule<IArmorGetter, IKeywordGetter> Node1 = new(
            assignments: new HashSet<IFormLinkGetter<IKeywordGetter>> { AssignKeywordNode1 },
            conditions: new HashSet<IAssignmentCondition<IArmorGetter>>
                {new HasAllKeywords<IArmorGetter>(new HashSet<IFormLinkGetter<IKeywordGetter>> {CheckKeywordNode1})},
            nodeName: "some_node",
            subNodes: new HashSet<AssignmentRule<IArmorGetter, IKeywordGetter>>()
        );

        internal static readonly AssignmentRule<IArmorGetter, IKeywordGetter> Node2 = new(
            assignments: new HashSet<IFormLinkGetter<IKeywordGetter>> { AssignKeywordNode2 },
            conditions: new HashSet<IAssignmentCondition<IArmorGetter>>
                {new HasAllKeywords<IArmorGetter>(new HashSet<IFormLinkGetter<IKeywordGetter>> {CheckKeywordNode2})},
            nodeName: "other_node",
            subNodes: new HashSet<AssignmentRule<IArmorGetter, IKeywordGetter>>()
        );

        internal static readonly AssignmentRule<IArmorGetter, IKeywordGetter> TestRule = new(
            assignments: new HashSet<IFormLinkGetter<IKeywordGetter>>(),
            conditions: new HashSet<IAssignmentCondition<IArmorGetter>>
                {new HasAllKeywords<IArmorGetter>(new HashSet<IFormLinkGetter<IKeywordGetter>> {CheckKeywordRoot})},
            nodeName: "feature_foo",
            subNodes: new HashSet<AssignmentRule<IArmorGetter, IKeywordGetter>> { Node1, Node2 }
        );
    }
}