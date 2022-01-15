using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Cache.Implementations;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.Rules;
using Xunit;

namespace ReqtificatorTest.Transformers.Rules
{
    public class ActorInheritanceGraphParserTest
    {
        class Fixture
        {
            internal Npc Template1 { get; }
            internal Npc Template2 { get; }
            internal Npc Template3 { get; }
            internal LeveledNpc LeveledCharWithMultipleActors { get; }

            public Fixture()
            {
                Template1 = new Npc(FormKey.Factory("ABC111:Templates.esm"), SkyrimRelease.SkyrimSE)
                {
                    Template = new FormLinkNullable<INpcSpawnGetter>()
                };
                Template2 = new Npc(FormKey.Factory("ABC222:Templates.esm"), SkyrimRelease.SkyrimSE)
                {
                    Template = new FormLinkNullable<INpcSpawnGetter>()
                };
                Template3 = new Npc(FormKey.Factory("ABC333:Templates.esm"), SkyrimRelease.SkyrimSE)
                {
                    Template = new FormLinkNullable<INpcSpawnGetter>()
                };
                LeveledCharWithMultipleActors =
                    new LeveledNpc(FormKey.Factory("ABC444:Templates.esm"), SkyrimRelease.SkyrimSE)
                    {
                        Entries = new ExtendedList<LeveledNpcEntry>
                        {
                            new()
                            {
                                Data = new LeveledNpcEntryData { Count = 1, Level = 1, Reference = Template2.AsLink() }
                            },
                            new()
                            {
                                Data = new LeveledNpcEntryData { Count = 1, Level = 1, Reference = Template3.AsLink() }
                            }
                        }
                    };
            }

            public ImmutableModLinkCache<ISkyrimMod, ISkyrimModGetter> getCache(params Npc[] npcsToAdd)
            {
                var myMod = new SkyrimMod("testplugin.esm", SkyrimRelease.SkyrimSE);
                myMod.Npcs.Add(Template1);
                myMod.Npcs.Add(Template2);
                myMod.Npcs.Add(Template3);
                myMod.LeveledNpcs.Add(LeveledCharWithMultipleActors);
                npcsToAdd.ForEach(myMod.Npcs.Add);
                return new ImmutableModLinkCache<ISkyrimMod, ISkyrimModGetter>(myMod,
                    LinkCachePreferences.Default);
            }
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_with_no_template()
        {
            var fixture = new Fixture();
            var actorWithNoInheritance = new Npc(FormKey.Factory("123AAA:Npc.esm"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>()
            };
            var resolver = new ActorInheritanceGraphParser(fixture.getCache(actorWithNoInheritance));

            var results = resolver.FindAllTemplates(actorWithNoInheritance,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits).ToImmutableList();
            results.Should().HaveCount(1);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
                {
                    { NpcConfiguration.TemplateFlag.Keywords, actorWithNoInheritance },
                    { NpcConfiguration.TemplateFlag.Traits, actorWithNoInheritance }
                }
            );
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_with_no_relevant_inheritance_flags()
        {
            var fixture = new Fixture();
            var actorWithUnrelatedTemplateFlags = new Npc(FormKey.Factory("123CCC:Npc.esm"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(fixture.Template1),
                Configuration = new NpcConfiguration()
                {
                    TemplateFlags = NpcConfiguration.TemplateFlag.Script | NpcConfiguration.TemplateFlag.Factions
                }
            };
            var resolver = new ActorInheritanceGraphParser(fixture.getCache(actorWithUnrelatedTemplateFlags));

            var results = resolver.FindAllTemplates(actorWithUnrelatedTemplateFlags,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits).ToImmutableList();
            results.Should().HaveCount(1);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
                {
                    { NpcConfiguration.TemplateFlag.Keywords, actorWithUnrelatedTemplateFlags },
                    { NpcConfiguration.TemplateFlag.Traits, actorWithUnrelatedTemplateFlags }
                }
            );
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_with_a_simple_direct_template()
        {
            var fixture = new Fixture();
            var actorWithSimpleActorTemplate = new Npc(FormKey.Factory("123BBB:Npc.esm"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(fixture.Template1),
                Configuration = new NpcConfiguration()
                {
                    TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Traits
                }
            };

            var resolver = new ActorInheritanceGraphParser(fixture.getCache(actorWithSimpleActorTemplate));

            var results = resolver.FindAllTemplates(actorWithSimpleActorTemplate,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits).ToImmutableList();
            results.Should().HaveCount(1);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
                {
                    { NpcConfiguration.TemplateFlag.Keywords, fixture.Template1 },
                    { NpcConfiguration.TemplateFlag.Traits, fixture.Template1 }
                }
            );
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_with_partial_inheritance()
        {
            var fixture = new Fixture();
            var actorWithPartialInheritance = new Npc(FormKey.Factory("123DDD:Npc.esm"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(fixture.Template1),
                Configuration = new NpcConfiguration()
                {
                    TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Script
                }
            };
            var resolver = new ActorInheritanceGraphParser(fixture.getCache(actorWithPartialInheritance));

            var results = resolver.FindAllTemplates(actorWithPartialInheritance,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits).ToImmutableList();
            results.Should().HaveCount(1);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
                {
                    { NpcConfiguration.TemplateFlag.Keywords, fixture.Template1 },
                    { NpcConfiguration.TemplateFlag.Traits, actorWithPartialInheritance }
                }
            );
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_inheriting_from_a_leveled_character()
        {
            var fixture = new Fixture();
            var actorWithLeveledCharTemplate = new Npc(FormKey.Factory("123EEE:Npc.esm"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(fixture.LeveledCharWithMultipleActors),
                Configuration = new NpcConfiguration()
                {
                    TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Traits
                }
            };
            var resolver = new ActorInheritanceGraphParser(fixture.getCache(actorWithLeveledCharTemplate));

            var results = resolver.FindAllTemplates(actorWithLeveledCharTemplate,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits).ToImmutableList();
            results.Should().HaveCount(2);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
            {
                { NpcConfiguration.TemplateFlag.Keywords, fixture.Template2 },
                { NpcConfiguration.TemplateFlag.Traits, fixture.Template2 }
            });
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
            {
                { NpcConfiguration.TemplateFlag.Keywords, fixture.Template3 },
                { NpcConfiguration.TemplateFlag.Traits, fixture.Template3 }
            });
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_with_partial_lchar_inheritance()
        {
            var fixture = new Fixture();
            var actorWithLeveledCharTemplate = new Npc(FormKey.Factory("123EEE:Npc.esm"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(fixture.LeveledCharWithMultipleActors),
                Configuration = new NpcConfiguration()
                {
                    TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Traits
                }
            };
            var actorWithPartialInheritanceAndNestedLeveledCharTemplate =
                new Npc(FormKey.Factory("123FFF:Npc.esm"), SkyrimRelease.SkyrimSE)
                {
                    Template = new FormLinkNullable<INpcSpawnGetter>(actorWithLeveledCharTemplate),
                    Configuration = new NpcConfiguration()
                    {
                        TemplateFlags = NpcConfiguration.TemplateFlag.Keywords |
                                        NpcConfiguration.TemplateFlag.Script
                    }
                };

            var resolver =
                new ActorInheritanceGraphParser(fixture.getCache(actorWithLeveledCharTemplate,
                    actorWithPartialInheritanceAndNestedLeveledCharTemplate));

            var results = resolver.FindAllTemplates(actorWithPartialInheritanceAndNestedLeveledCharTemplate,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits,
                NpcConfiguration.TemplateFlag.Script).ToImmutableList();
            results.Should().HaveCount(2);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcSpawnGetter>()
                {
                    { NpcConfiguration.TemplateFlag.Keywords, fixture.Template2 },
                    { NpcConfiguration.TemplateFlag.Traits, actorWithPartialInheritanceAndNestedLeveledCharTemplate },
                    { NpcConfiguration.TemplateFlag.Script, actorWithLeveledCharTemplate }
                }
            );
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcSpawnGetter>()
                {
                    { NpcConfiguration.TemplateFlag.Keywords, fixture.Template3 },
                    { NpcConfiguration.TemplateFlag.Traits, actorWithPartialInheritanceAndNestedLeveledCharTemplate },
                    { NpcConfiguration.TemplateFlag.Script, actorWithLeveledCharTemplate }
                }
            );
        }
    }
}