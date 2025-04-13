using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Actors;
using Reqtificator.Transformers.Rules;
using ReqtificatorTest.Transformers.Rules;
using Xunit;

namespace ReqtificatorTest.Transformers.Actors
{
    public class ActorSpellsFromRulesTest
    {
        private readonly Npc.TranslationMask _mask = new(defaultOn: true) { ActorEffect = false };

        private readonly IFormLinkGetter<ISpellRecordGetter> _dummySpell =
            new FormLink<ISpellRecordGetter>(FormKey.Factory("123456:Spells.esm"));

        [Fact]
        public void Should_apply_assignments_from_a_matching_rule_for_an_actor()
        {
            var f = new ActorAssignmentRuleFixture<ISpellGetter>();
            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Configuration = new NpcConfiguration { TemplateFlags = NpcConfiguration.TemplateFlag.Script },
                // inheritance resolution is mocked by the supplied ActorInheritanceGraphParser
                ActorEffect = [_dummySpell],
                Race = f.CheckRaceNode2.AsSetter()
            };

            var transformer =
                new ActorSpellsFromRules(ImmutableList<AssignmentRule<INpcGetter, ISpellGetter>>.Empty.Add(f.TestRule));
            f.InheritanceGraph.Setup(g => g.FindAllTemplates(input, NpcConfiguration.TemplateFlag.Traits))
                .Returns(
                [
                    ImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>.Empty.Add(
                        NpcConfiguration.TemplateFlag.Traits, input)
                ]);

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            var expectedSpells = input.ActorEffect!.ToImmutableList().Add(f.AssignRecordNode1);
            result.Record().ActorEffect.Should().BeEquivalentTo(expectedSpells);
        }

        [Fact]
        public void Should_apply_assignments_from_multiple_matching_rules()
        {
            var f = new ActorAssignmentRuleFixture<ISpellGetter>();
            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Configuration = new NpcConfiguration { TemplateFlags = NpcConfiguration.TemplateFlag.Script },
                // inheritance resolution is mocked by the supplied ActorInheritanceGraphParser
                ActorEffect = [_dummySpell],
                Race = new FormLink<IRaceGetter>(FormKey.Factory("123456:OtherRaces.esm"))
            };

            var transformer =
                new ActorSpellsFromRules(ImmutableList<AssignmentRule<INpcGetter, ISpellGetter>>.Empty.Add(f.TestRule));
            f.InheritanceGraph.Setup(g => g.FindAllTemplates(input, NpcConfiguration.TemplateFlag.Traits))
                .Returns(
                [
                    ImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>.Empty.Add(
                        NpcConfiguration.TemplateFlag.Traits, input)
                ]);

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            var expectedSpells = input.ActorEffect!.ToImmutableList().Add(f.AssignRecordNode1).Add(f.AssignRecordNode2);
            result.Record().ActorEffect.Should().BeEquivalentTo(expectedSpells);
        }

        [Fact]
        public void Should_not_change_actors_if_no_rule_matches()
        {
            var f = new ActorAssignmentRuleFixture<ISpellGetter>();
            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Configuration = new NpcConfiguration { TemplateFlags = NpcConfiguration.TemplateFlag.Script },
                // inheritance resolution is mocked by the supplied ActorInheritanceGraphParser
                ActorEffect = [_dummySpell],
                Race = f.CheckRaceRoot.AsSetter()
            };

            var transformer =
                new ActorSpellsFromRules(ImmutableList<AssignmentRule<INpcGetter, ISpellGetter>>.Empty.Add(f.TestRule));
            f.InheritanceGraph.Setup(g => g.FindAllTemplates(input, NpcConfiguration.TemplateFlag.Traits))
                .Returns(
                [
                    ImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>.Empty.Add(
                        NpcConfiguration.TemplateFlag.Traits, input)
                ]);

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

        [Fact]
        public void Should_not_change_actors_if_they_inherit_their_perks_and_spells()
        {
            var f = new ActorAssignmentRuleFixture<ISpellGetter>();
            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Configuration = new NpcConfiguration { TemplateFlags = NpcConfiguration.TemplateFlag.SpellList },
                // inheritance resolution is mocked by the supplied ActorInheritanceGraphParser
                ActorEffect = [_dummySpell],
                Race = f.CheckRaceNode2.AsSetter()
            };

            var transformer =
                new ActorSpellsFromRules(ImmutableList<AssignmentRule<INpcGetter, ISpellGetter>>.Empty.Add(f.TestRule));
            f.InheritanceGraph.Setup(g => g.FindAllTemplates(input, NpcConfiguration.TemplateFlag.Traits))
                .Returns(
                [
                    ImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>.Empty.Add(
                        NpcConfiguration.TemplateFlag.Traits, input)
                ]);

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }
    }
}