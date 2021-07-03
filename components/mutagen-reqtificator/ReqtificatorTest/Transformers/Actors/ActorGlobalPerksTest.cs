using System.Collections.Immutable;
using System.Linq;
using FluentAssertions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Actors;
using Xunit;

namespace ReqtificatorTest.Transformers.Actors
{
    public class ActorGlobalPerksTest
    {
        private static readonly Npc.TranslationMask Mask = new(defaultOn: true) { Perks = false };

        private static readonly ImmutableHashSet<PerkPlacement> PerksToAdd =
            ImmutableHashSet<PerkPlacement>.Empty
                .Add(new PerkPlacement
                { Perk = new FormLink<IPerkGetter>(FormKey.Factory("123ABC:Requiem.esp")), Rank = 1 })
                .Add(new PerkPlacement
                { Perk = new FormLink<IPerkGetter>(FormKey.Factory("123DEF:Requiem.esp")), Rank = 1 });

        private static readonly PerkPlacement OtherPerk = new()
        {
            Perk = new FormLink<IPerkGetter>(FormKey.Factory("ABCDEF:Skyrim.esm")),
            Rank = 1
        };

        [Fact]
        public void Should_add_the_global_scripts_to_actors_without_template()
        {
            var transformer = new ActorGlobalPerks(PerksToAdd.Select(p => p.Perk.AsGetter()).ToImmutableHashSet());

            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(FormKey.Null),
                Configuration = new NpcConfiguration { TemplateFlags = 0 },
                Perks = new ExtendedList<PerkPlacement> { OtherPerk }
            };
            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, Mask).Should().BeTrue();
            result.Record().Perks!.Should().BeEquivalentTo(PerksToAdd.Add(OtherPerk));
        }

        [Fact]
        public void Should_add_the_global_scripts_to_actors_with_template_but_no_perk_inheritance()
        {
            var transformer = new ActorGlobalPerks(PerksToAdd.Select(p => p.Perk.AsGetter()).ToImmutableHashSet());

            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(FormKey.Factory("567890:Skyrim.esm")),
                Configuration = new NpcConfiguration { TemplateFlags = 0 },
                Perks = new ExtendedList<PerkPlacement> { OtherPerk }
            };
            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, Mask).Should().BeTrue();
            result.Record().Perks!.Should().BeEquivalentTo(PerksToAdd.Add(OtherPerk));
        }

        [Fact]
        public void Should_handle_eligible_actors_with_no_perks_section_properly()
        {
            var transformer = new ActorGlobalPerks(PerksToAdd.Select(p => p.Perk.AsGetter()).ToImmutableHashSet());

            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(FormKey.Null),
                Configuration = new NpcConfiguration { TemplateFlags = 0 },
                Perks = null
            };
            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, Mask).Should().BeTrue();
            result.Record().Perks!.Should().BeEquivalentTo(PerksToAdd);
        }

        [Fact]
        public void Should_only_apply_new_perks_to_eligible_actors_and_avoid_duplicates()
        {
            var transformer = new ActorGlobalPerks(PerksToAdd.Select(p => p.Perk.AsGetter()).ToImmutableHashSet());

            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(FormKey.Null),
                Configuration = new NpcConfiguration { TemplateFlags = 0 },
                Perks = new ExtendedList<PerkPlacement> { PerksToAdd.First() }
            };
            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, Mask).Should().BeTrue();
            result.Record().Perks!.Should().BeEquivalentTo(PerksToAdd);
        }

        [Fact]
        public void Should_not_patch_records_that_have_a_template_and_inherit_perks()
        {
            var transformer = new ActorGlobalPerks(PerksToAdd.Select(p => p.Perk.AsGetter()).ToImmutableHashSet());

            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(FormKey.Factory("ABCDEF:Requiem.esp")),
                Configuration = new NpcConfiguration { TemplateFlags = NpcConfiguration.TemplateFlag.SpellList },
                Perks = null
            };
            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }
    }
}