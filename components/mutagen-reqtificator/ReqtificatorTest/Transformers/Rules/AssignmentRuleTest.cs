using System.Collections.Generic;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.Rules;
using Xunit;
using static ReqtificatorTest.Transformers.Rules.AssignmentRuleFixture;

namespace ReqtificatorTest.Transformers.Rules
{
    public class AssignmentRuleTest
    {
        [Fact]
        public void Should_apply_assignments_from_a_matching_node()
        {
            var armor = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { CheckKeywordNode1, CheckKeywordRoot }
            };

            var results = TestRule.GetAssignments(armor);
            results.Should().BeEquivalentTo(new List<Assignment<IKeywordGetter>>
            {
                new(AssignKeywordNode1, $"{TestRule.NodeName}.{Node1.NodeName}")
            });
        }

        [Fact]
        public void Should_apply_assignments_from_all_matching_nodes()
        {
            var armor = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>>
                    {CheckKeywordNode1, CheckKeywordNode2, CheckKeywordRoot}
            };

            var results = TestRule.GetAssignments(armor);
            results.Should().BeEquivalentTo(new List<Assignment<IKeywordGetter>>
            {
                new(AssignKeywordNode1, $"{TestRule.NodeName}.{Node1.NodeName}"),
                new(AssignKeywordNode2, $"{TestRule.NodeName}.{Node2.NodeName}"),
            });
        }

        [Fact]
        public void Should_not_apply_assignments_from_matching_nodes_if_their_parents_do_not_match()
        {
            var armor = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { CheckKeywordNode1, CheckKeywordNode2 }
            };

            var results = TestRule.GetAssignments(armor);
            results.Should().BeEmpty();
        }
    }
}