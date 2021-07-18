using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.Rules;
using Reqtificator.Transformers.Rules.Conditions;
using Xunit;
using Flags = Mutagen.Bethesda.Skyrim.NpcConfiguration.TemplateFlag;

namespace ReqtificatorTest.Transformers.Rules.Conditions
{
    public class ActorHasAnyRaceTest
    {
        private static readonly IFormLinkGetter<IRaceGetter> Race1 =
            new FormLink<IRaceGetter>(FormKey.Factory("ABC123:Keywords.esp"));

        private static readonly IFormLinkGetter<IRaceGetter> Race2 =
            new FormLink<IRaceGetter>(FormKey.Factory("ABC456:Keywords.esp"));

        private static readonly IFormLinkGetter<IRaceGetter> OtherRace =
            new FormLink<IRaceGetter>(FormKey.Factory("ABC789:Keywords.esp"));

        [Fact]
        public void Should_match_an_actor_with_a_single_inheritance_and_one_of_the_expected_races()
        {
            var graph = new Mock<IActorInheritanceGraphParser>(MockBehavior.Strict);
            var cache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            var actor = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                //inheritance resolution is mocked, the relevant data therefore doesn't need to be defined here
                Race = new FormLink<IRaceGetter>(Race1.FormKey)
            };
            var resolved = ImmutableDictionary<Flags, INpcGetter>.Empty.Add(Flags.Traits, actor);

            graph.Setup(g => g.FindAllTemplates(actor, Flags.Traits)).Returns(resolved.AsEnumerable());
            var condition = new ActorHasAnyRace(new HashSet<IFormLinkGetter<IRaceGetter>>
                {Race1, Race2}.ToImmutableHashSet(), graph.Object);
            condition.CheckRecord(actor).Should().BeTrue();
        }

        [Fact]
        public void Should_match_an_actor_with_multiple_inheritances_each_having_one_of_the_expected_races()
        {
            var graph = new Mock<IActorInheritanceGraphParser>(MockBehavior.Strict);
            var actor = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                //inheritance resolution is mocked, the relevant data therefore doesn't need to be defined here
                Race = new FormLink<IRaceGetter>(OtherRace.FormKey)
            };
            var actorTemplate1 = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Race = new FormLink<IRaceGetter>(Race1.FormKey)
            };
            var actorTemplate2 = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Race = new FormLink<IRaceGetter>(Race2.FormKey)
            };
            var resolved1 = ImmutableDictionary<Flags, INpcGetter>.Empty.Add(Flags.Traits, actorTemplate1);
            var resolved2 = ImmutableDictionary<Flags, INpcGetter>.Empty.Add(Flags.Traits, actorTemplate2);

            graph.Setup(g => g.FindAllTemplates(actor, Flags.Traits))
                .Returns(new[] { resolved1, resolved2 }.AsEnumerable<ImmutableDictionary<Flags, INpcGetter>>());
            var condition = new ActorHasAnyRace(new HashSet<IFormLinkGetter<IRaceGetter>>
                {Race1, Race2}.ToImmutableHashSet(), graph.Object);
            condition.CheckRecord(actor).Should().BeTrue();
        }

        [Fact]
        public void Should_not_match_an_actor_with_a_single_inheritance_and_a_different_race()
        {
            var graph = new Mock<IActorInheritanceGraphParser>(MockBehavior.Strict);
            var actor = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                //inheritance resolution is mocked, the relevant data therefore doesn't need to be defined here
                Race = new FormLink<IRaceGetter>(OtherRace.FormKey)
            };
            var resolved = ImmutableDictionary<Flags, INpcGetter>.Empty.Add(Flags.Traits, actor);

            graph.Setup(g => g.FindAllTemplates(actor, Flags.Traits)).Returns(resolved.AsEnumerable());
            var condition = new ActorHasAnyRace(new HashSet<IFormLinkGetter<IRaceGetter>>
                {Race1, Race2}.ToImmutableHashSet(), graph.Object);
            condition.CheckRecord(actor).Should().BeFalse();
        }

        [Fact]
        public void Should_match_an_actor_with_multiple_inheritances_with_at_least_one_not_having_an_expected_race()
        {
            var graph = new Mock<IActorInheritanceGraphParser>(MockBehavior.Strict);
            var actor = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                //inheritance resolution is mocked, the relevant data therefore doesn't need to be defined here
                Race = new FormLink<IRaceGetter>(Race1.FormKey)
            };
            var actorTemplate1 = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Race = new FormLink<IRaceGetter>(OtherRace.FormKey)
            };
            var actorTemplate2 = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Race = new FormLink<IRaceGetter>(Race2.FormKey)
            };
            var resolved1 = ImmutableDictionary<Flags, INpcGetter>.Empty.Add(Flags.Traits, actorTemplate1);
            var resolved2 = ImmutableDictionary<Flags, INpcGetter>.Empty.Add(Flags.Traits, actorTemplate2);

            graph.Setup(g => g.FindAllTemplates(actor, Flags.Traits))
                .Returns(new[] { resolved1, resolved2 }.AsEnumerable<ImmutableDictionary<Flags, INpcGetter>>());
            var condition = new ActorHasAnyRace(new HashSet<IFormLinkGetter<IRaceGetter>>
                {Race1, Race2}.ToImmutableHashSet(), graph.Object);
            condition.CheckRecord(actor).Should().BeFalse();
        }
    }
}