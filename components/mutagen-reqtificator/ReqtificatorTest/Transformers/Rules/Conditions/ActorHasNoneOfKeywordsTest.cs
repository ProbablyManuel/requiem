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
    public class ActorHasNoneOfKeywordsTest
    {
        private static readonly IFormLinkGetter<IKeywordGetter> Keyword1 =
            new FormLink<IKeywordGetter>(FormKey.Factory("ABC123:Keywords.esp"));

        private static readonly IFormLinkGetter<IKeywordGetter> Keyword2 =
            new FormLink<IKeywordGetter>(FormKey.Factory("ABC456:Keywords.esp"));

        private static readonly IFormLinkGetter<IKeywordGetter> OtherKeyword =
            new FormLink<IKeywordGetter>(FormKey.Factory("ABC789:Keywords.esp"));

        [Fact]
        public void Should_match_an_actor_with_a_single_inheritance_and_none_of_the_keywords()
        {
            var graph = new Mock<IActorInheritanceGraphParser>(MockBehavior.Strict);
            var cache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            IRaceGetter race = new Race(FormKey.Factory("ABCDEF:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { OtherKeyword }
            };
            var actor = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                //inheritance resolution is mocked, the relevant data therefore doesn't need to be defined here
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { OtherKeyword },
                Race = new FormLink<IRaceGetter>(race)
            };
            var resolved = ImmutableDictionary<Flags, INpcGetter>.Empty
                .Add(Flags.Keywords, actor)
                .Add(Flags.Traits, actor);

            graph.SetupGet(g => g.Cache).Returns(cache.Object);
            graph.Setup(g => g.FindAllTemplates(actor, Flags.Keywords, Flags.Traits))
                .Returns(
                    resolved.AsEnumerable());
            cache.Setup(c => c.TryResolve(race.FormKey, out race, ResolveTarget.Winner)).Returns(true);
            var condition = new ActorHasNoneOfKeywords(new HashSet<IFormLinkGetter<IKeywordGetter>>
                {Keyword1, Keyword2}.ToImmutableHashSet(), graph.Object);
            condition.CheckRecord(actor).Should().BeTrue();
        }

        [Fact]
        public void Should_match_an_actor_with_multiple_inheritances_each_having_none_of_the_disallowed_keywords()
        {
            var graph = new Mock<IActorInheritanceGraphParser>(MockBehavior.Strict);
            var cache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            IRaceGetter race = new Race(FormKey.Factory("ABCDEF:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { OtherKeyword }
            };
            var actor = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                //inheritance resolution is mocked, the relevant data therefore doesn't need to be defined here
                Race = new FormLink<IRaceGetter>(race)
            };
            var actorTemplate1 = new Npc(FormKey.Factory("123ABC:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { OtherKeyword },
            };
            var actorTemplate2 = new Npc(FormKey.Factory("123DEF:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = null
            };
            var resolved1 = ImmutableDictionary<Flags, INpcGetter>.Empty
                .Add(Flags.Keywords, actorTemplate1)
                .Add(Flags.Traits, actor);
            var resolved2 = ImmutableDictionary<Flags, INpcGetter>.Empty
                .Add(Flags.Keywords, actorTemplate2)
                .Add(Flags.Traits, actor);

            graph.SetupGet(g => g.Cache).Returns(cache.Object);
            graph.Setup(g => g.FindAllTemplates(actor, Flags.Keywords, Flags.Traits))
                .Returns(new[] { resolved1, resolved2 }.AsEnumerable<IImmutableDictionary<Flags, INpcGetter>>());
            cache.Setup(c => c.TryResolve(race.FormKey, out race, ResolveTarget.Winner)).Returns(true);
            var condition = new ActorHasNoneOfKeywords(new HashSet<IFormLinkGetter<IKeywordGetter>>
                {Keyword1, Keyword2}.ToImmutableHashSet(), graph.Object);
            condition.CheckRecord(actor).Should().BeTrue();
        }

        [Fact]
        public void Should_not_match_an_actor_with_a_single_inheritance_and_at_least_one_of_the_keywords_on_the_race()
        {
            var graph = new Mock<IActorInheritanceGraphParser>(MockBehavior.Strict);
            var cache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            IRaceGetter race = new Race(FormKey.Factory("ABCDEF:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { Keyword1, OtherKeyword }
            };
            var actor = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                //inheritance resolution is mocked, the relevant data therefore doesn't need to be defined here
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { OtherKeyword },
                Race = new FormLink<IRaceGetter>(race)
            };
            var resolved = ImmutableDictionary<Flags, INpcGetter>.Empty
                .Add(Flags.Keywords, actor)
                .Add(Flags.Traits, actor);

            graph.SetupGet(g => g.Cache).Returns(cache.Object);
            graph.Setup(g => g.FindAllTemplates(actor, Flags.Keywords, Flags.Traits))
                .Returns(
                    resolved.AsEnumerable());
            cache.Setup(c => c.TryResolve(race.FormKey, out race, ResolveTarget.Winner)).Returns(true);
            var condition = new ActorHasNoneOfKeywords(new HashSet<IFormLinkGetter<IKeywordGetter>>
                {Keyword1, Keyword2}.ToImmutableHashSet(), graph.Object);
            condition.CheckRecord(actor).Should().BeFalse();
        }

        [Fact]
        public void Should_not_match_an_actor_with_a_single_inheritance_and_at_least_one_of_the_keywords_on_the_actor()
        {
            var graph = new Mock<IActorInheritanceGraphParser>(MockBehavior.Strict);
            var cache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            IRaceGetter race = new Race(FormKey.Factory("ABCDEF:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { OtherKeyword }
            };
            var actor = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                //inheritance resolution is mocked, the relevant data therefore doesn't need to be defined here
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { Keyword2 },
                Race = new FormLink<IRaceGetter>(race)
            };
            var resolved = ImmutableDictionary<Flags, INpcGetter>.Empty
                .Add(Flags.Keywords, actor)
                .Add(Flags.Traits, actor);

            graph.SetupGet(g => g.Cache).Returns(cache.Object);
            graph.Setup(g => g.FindAllTemplates(actor, Flags.Keywords, Flags.Traits))
                .Returns(
                    resolved.AsEnumerable());
            cache.Setup(c => c.TryResolve(race.FormKey, out race, ResolveTarget.Winner)).Returns(true);
            var condition = new ActorHasNoneOfKeywords(new HashSet<IFormLinkGetter<IKeywordGetter>>
                {Keyword1, Keyword2}.ToImmutableHashSet(), graph.Object);
            condition.CheckRecord(actor).Should().BeFalse();
        }

        [Fact]
        public void Should_not_match_an_actor_with_multiple_inheritances_if_any_of_them_has_one_of_the_keywords()
        {
            var graph = new Mock<IActorInheritanceGraphParser>(MockBehavior.Strict);
            var cache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            IRaceGetter race = new Race(FormKey.Factory("ABCDEF:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = null
            };
            var actor = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                //inheritance resolution is mocked, the relevant data therefore doesn't need to be defined here
                Race = new FormLink<IRaceGetter>(race)
            };
            var actorTemplate1 = new Npc(FormKey.Factory("123ABC:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { Keyword2, OtherKeyword },
            };
            var actorTemplate2 = new Npc(FormKey.Factory("123DEF:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { OtherKeyword }
            };
            var resolved1 = ImmutableDictionary<Flags, INpcGetter>.Empty
                .Add(Flags.Keywords, actorTemplate1)
                .Add(Flags.Traits, actor);
            var resolved2 = ImmutableDictionary<Flags, INpcGetter>.Empty
                .Add(Flags.Keywords, actorTemplate2)
                .Add(Flags.Traits, actor);

            graph.SetupGet(g => g.Cache).Returns(cache.Object);
            graph.Setup(g => g.FindAllTemplates(actor, Flags.Keywords, Flags.Traits))
                .Returns(new[] { resolved1, resolved2 }.AsEnumerable<IImmutableDictionary<Flags, INpcGetter>>());
            cache.Setup(c => c.TryResolve(race.FormKey, out race, ResolveTarget.Winner)).Returns(true);
            var condition = new ActorHasNoneOfKeywords(new HashSet<IFormLinkGetter<IKeywordGetter>>
                {Keyword1, Keyword2}.ToImmutableHashSet(), graph.Object);
            condition.CheckRecord(actor).Should().BeFalse();
        }
    }
}