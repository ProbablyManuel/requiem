using System.Collections.Immutable;
using Moq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Transformers.Rules;
using Reqtificator.Transformers.Rules.Conditions;

namespace ReqtificatorTest.Transformers.Rules
{
    internal class ActorAssignmentRuleFixture<TAssign> where TAssign : class, IMajorRecordGetter
    {
        internal readonly Mock<IActorInheritanceGraphParser> InheritanceGraph;
        internal readonly IFormLinkGetter<IRaceGetter> CheckRaceNode1;
        internal readonly IFormLinkGetter<IRaceGetter> CheckRaceNode2;
        internal readonly IFormLinkGetter<IRaceGetter> CheckRaceRoot;
        internal readonly IFormLinkGetter<TAssign> AssignRecordNode1;
        internal readonly IFormLinkGetter<TAssign> AssignRecordNode2;
        internal readonly AssignmentRule<INpcGetter, TAssign> Node1;
        internal readonly AssignmentRule<INpcGetter, TAssign> Node2;
        internal readonly AssignmentRule<INpcGetter, TAssign> TestRule;

        public ActorAssignmentRuleFixture()
        {
            InheritanceGraph = new Mock<IActorInheritanceGraphParser>();
            CheckRaceNode1 = new FormLink<IRaceGetter>(FormKey.Factory("123ABC:Skyrim.esm"));
            CheckRaceNode2 = new FormLink<IRaceGetter>(FormKey.Factory("123DEF:Skyrim.esm"));
            CheckRaceRoot = new FormLink<IRaceGetter>(FormKey.Factory("123456:Skyrim.esm"));
            AssignRecordNode1 = new FormLink<TAssign>(FormKey.Factory("ABC123:Assign.esp"));
            AssignRecordNode2 = new FormLink<TAssign>(FormKey.Factory("DEF123:Assign.esp"));
            Node1 = new AssignmentRule<INpcGetter, TAssign>(
                assignments: ImmutableHashSet<IFormLinkGetter<TAssign>>.Empty.Add(AssignRecordNode1),
                conditions: ImmutableHashSet<IAssignmentCondition<INpcGetter>>.Empty.Add(
                    new ActorHasNoneOfRaces(
                        ImmutableHashSet<IFormLinkGetter<IRaceGetter>>.Empty.Add(CheckRaceNode1),
                        InheritanceGraph.Object)),
                nodeName: "some_node",
                subNodes: ImmutableHashSet<AssignmentRule<INpcGetter, TAssign>>.Empty
            );
            Node2 = new AssignmentRule<INpcGetter, TAssign>(
                assignments: ImmutableHashSet<IFormLinkGetter<TAssign>>.Empty.Add(AssignRecordNode2),
                conditions: ImmutableHashSet<IAssignmentCondition<INpcGetter>>.Empty.Add(
                    new ActorHasNoneOfRaces(
                        ImmutableHashSet<IFormLinkGetter<IRaceGetter>>.Empty.Add(CheckRaceNode2),
                        InheritanceGraph.Object)),
                nodeName: "other_node",
                subNodes: ImmutableHashSet<AssignmentRule<INpcGetter, TAssign>>.Empty
            );
            TestRule = new AssignmentRule<INpcGetter, TAssign>(
                assignments: ImmutableHashSet<IFormLinkGetter<TAssign>>.Empty,
                conditions: ImmutableHashSet<IAssignmentCondition<INpcGetter>>.Empty.Add(
                    new ActorHasNoneOfRaces(
                    ImmutableHashSet<IFormLinkGetter<IRaceGetter>>.Empty.Add(CheckRaceRoot),
                    InheritanceGraph.Object)),
                nodeName: "feature_foo",
                subNodes: ImmutableHashSet<AssignmentRule<INpcGetter, TAssign>>.Empty.Add(Node1).Add(Node2)
            );
        }
    }
}