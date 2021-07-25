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
    public class ActorPerksFromRulesTest
    {
        private readonly Npc.TranslationMask _mask = new(defaultOn: true) { Perks = false };

        PerkPlacement dummyPerk = new()
        {
            Perk = new FormLink<IPerkGetter>(FormKey.Factory("ABCDEF:Perks.esm")),
            Rank = 1
        };

        [Fact]
        public void Should_apply_assignments_from_a_matching_rule_for_an_actor()
        {
            var f = new ActorAssignmentRuleFixture<IPerkGetter>();
            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Configuration = new NpcConfiguration { TemplateFlags = NpcConfiguration.TemplateFlag.Script },
                // inheritance resolution is mocked by the supplied ActorInheritanceGraphParser
                Perks = new ExtendedList<PerkPlacement> { dummyPerk },
                Race = f.CheckRaceNode2.AsSetter()
            };

            var transformer =
                new ActorPerksFromRules(ImmutableList<AssignmentRule<INpcGetter, IPerkGetter>>.Empty.Add(f.TestRule));
            f.InheritanceGraph.Setup(g => g.FindAllTemplates(input, NpcConfiguration.TemplateFlag.Traits))
                .Returns(new List<IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>>
                {
                    ImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>.Empty.Add(
                        NpcConfiguration.TemplateFlag.Traits, input)
                });

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            var expectedNewPerk = new PerkPlacement { Perk = f.AssignRecordNode1.AsSetter(), Rank = 1 };
            var expectedPerks = input.Perks!.ToImmutableList().Add(expectedNewPerk);
            result.Record().Perks.Should().BeEquivalentTo(expectedPerks);
        }

        [Fact]
        public void Should_apply_assignments_from_multiple_matching_rules()
        {
            var f = new ActorAssignmentRuleFixture<IPerkGetter>();
            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Configuration = new NpcConfiguration { TemplateFlags = NpcConfiguration.TemplateFlag.Script },
                // inheritance resolution is mocked by the supplied ActorInheritanceGraphParser
                Perks = new ExtendedList<PerkPlacement> { dummyPerk },
                Race = new FormLink<IRaceGetter>(FormKey.Factory("123456:OtherRaces.esm"))
            };

            var transformer =
                new ActorPerksFromRules(ImmutableList<AssignmentRule<INpcGetter, IPerkGetter>>.Empty.Add(f.TestRule));
            f.InheritanceGraph.Setup(g => g.FindAllTemplates(input, NpcConfiguration.TemplateFlag.Traits))
                .Returns(new List<IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>>
                {
                    ImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>.Empty.Add(
                        NpcConfiguration.TemplateFlag.Traits, input)
                });

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            var expectedNewPerk1 = new PerkPlacement { Perk = f.AssignRecordNode1.AsSetter(), Rank = 1 };
            var expectedNewPerk2 = new PerkPlacement { Perk = f.AssignRecordNode2.AsSetter(), Rank = 1 };
            var expectedPerks = input.Perks!.ToImmutableList().Add(expectedNewPerk1).Add(expectedNewPerk2);
            result.Record().Perks.Should().BeEquivalentTo(expectedPerks);
        }

        [Fact]
        public void Should_not_change_actors_if_no_rule_matches()
        {
            var f = new ActorAssignmentRuleFixture<IPerkGetter>();
            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Configuration = new NpcConfiguration { TemplateFlags = NpcConfiguration.TemplateFlag.Script },
                // inheritance resolution is mocked by the supplied ActorInheritanceGraphParser
                Perks = new ExtendedList<PerkPlacement> { dummyPerk },
                Race = f.CheckRaceRoot.AsSetter()
            };

            var transformer =
                new ActorPerksFromRules(ImmutableList<AssignmentRule<INpcGetter, IPerkGetter>>.Empty.Add(f.TestRule));
            f.InheritanceGraph.Setup(g => g.FindAllTemplates(input, NpcConfiguration.TemplateFlag.Traits))
                .Returns(new List<IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>>
                {
                    ImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>.Empty.Add(
                        NpcConfiguration.TemplateFlag.Traits, input)
                });

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

        [Fact]
        public void Should_not_change_actors_if_they_inherit_their_perks_and_spells()
        {
            var f = new ActorAssignmentRuleFixture<IPerkGetter>();
            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Configuration = new NpcConfiguration { TemplateFlags = NpcConfiguration.TemplateFlag.SpellList },
                // inheritance resolution is mocked by the supplied ActorInheritanceGraphParser
                Perks = new ExtendedList<PerkPlacement> { dummyPerk },
                Race = f.CheckRaceNode2.AsSetter()
            };

            var transformer =
                new ActorPerksFromRules(ImmutableList<AssignmentRule<INpcGetter, IPerkGetter>>.Empty.Add(f.TestRule));
            f.InheritanceGraph.Setup(g => g.FindAllTemplates(input, NpcConfiguration.TemplateFlag.Traits))
                .Returns(new List<IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>>
                {
                    ImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>.Empty.Add(
                        NpcConfiguration.TemplateFlag.Traits, input)
                });

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }
    }
}