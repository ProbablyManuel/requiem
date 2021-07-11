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
            internal Npc ActorWithNoInheritance { get; }
            internal Npc ActorWithSimpleActorTemplate { get; }
            internal Npc ActorWithUnrelatedTemplateFlags { get; }
            internal Npc ActorWithPartialInheritance { get; }
            internal Npc ActorWithLeveledCharTemplate { get; }
            internal Npc ActorWithPartialInheritanceAndNestedLeveledCharTemplate { get; }
            public ImmutableModLinkCache<ISkyrimMod, ISkyrimModGetter> LinkCache { get; }

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
                                Data = new LeveledNpcEntryData {Count = 1, Level = 1, Reference = Template2.AsLink()}
                            },
                            new()
                            {
                                Data = new LeveledNpcEntryData {Count = 1, Level = 1, Reference = Template3.AsLink()}
                            }
                        }
                    };

                ActorWithNoInheritance = new Npc(FormKey.Factory("123AAA:Npc.esm"), SkyrimRelease.SkyrimSE)
                {
                    Template = new FormLinkNullable<INpcSpawnGetter>()
                };
                ActorWithSimpleActorTemplate = new Npc(FormKey.Factory("123BBB:Npc.esm"), SkyrimRelease.SkyrimSE)
                {
                    Template = new FormLinkNullable<INpcSpawnGetter>(Template1),
                    Configuration = new NpcConfiguration()
                    {
                        TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Traits
                    }
                };
                ActorWithUnrelatedTemplateFlags = new Npc(FormKey.Factory("123CCC:Npc.esm"), SkyrimRelease.SkyrimSE)
                {
                    Template = new FormLinkNullable<INpcSpawnGetter>(Template1),
                    Configuration = new NpcConfiguration()
                    {
                        TemplateFlags = NpcConfiguration.TemplateFlag.Script | NpcConfiguration.TemplateFlag.Factions
                    }
                };
                ActorWithPartialInheritance = new Npc(FormKey.Factory("123DDD:Npc.esm"), SkyrimRelease.SkyrimSE)
                {
                    Template = new FormLinkNullable<INpcSpawnGetter>(Template1),
                    Configuration = new NpcConfiguration()
                    {
                        TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Script
                    }
                };
                ActorWithLeveledCharTemplate = new Npc(FormKey.Factory("123EEE:Npc.esm"), SkyrimRelease.SkyrimSE)
                {
                    Template = new FormLinkNullable<INpcSpawnGetter>(LeveledCharWithMultipleActors),
                    Configuration = new NpcConfiguration()
                    {
                        TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Traits
                    }
                };
                ActorWithPartialInheritanceAndNestedLeveledCharTemplate =
                    new Npc(FormKey.Factory("123FFF:Npc.esm"), SkyrimRelease.SkyrimSE)
                    {
                        Template = new FormLinkNullable<INpcSpawnGetter>(ActorWithLeveledCharTemplate),
                        Configuration = new NpcConfiguration()
                        {
                            TemplateFlags = NpcConfiguration.TemplateFlag.Keywords |
                                            NpcConfiguration.TemplateFlag.Script
                        }
                    };

                var myMod = new SkyrimMod("testplugin.esm", SkyrimRelease.SkyrimSE);
                myMod.Npcs.Add(Template1);
                myMod.Npcs.Add(Template2);
                myMod.Npcs.Add(Template3);
                myMod.LeveledNpcs.Add(LeveledCharWithMultipleActors);
                myMod.Npcs.Add(ActorWithNoInheritance);
                myMod.Npcs.Add(ActorWithSimpleActorTemplate);
                myMod.Npcs.Add(ActorWithUnrelatedTemplateFlags);
                myMod.Npcs.Add(ActorWithPartialInheritance);
                myMod.Npcs.Add(ActorWithLeveledCharTemplate);
                myMod.Npcs.Add(ActorWithPartialInheritanceAndNestedLeveledCharTemplate);

                LinkCache = new ImmutableModLinkCache<ISkyrimMod, ISkyrimModGetter>(myMod,
                    LinkCachePreferences.Default);
            }
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_with_no_template()
        {
            var fixture = new Fixture();
            var resolver = new ActorInheritanceGraphParser(fixture.LinkCache);

            var results = resolver.FindAllTemplates(fixture.ActorWithNoInheritance,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits).ToImmutableList();
            results.Should().HaveCount(1);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
                {
                    {NpcConfiguration.TemplateFlag.Keywords, fixture.ActorWithNoInheritance},
                    {NpcConfiguration.TemplateFlag.Traits, fixture.ActorWithNoInheritance}
                }
            );
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_with_no_relevant_inheritance_flags()
        {
            var fixture = new Fixture();
            var resolver = new ActorInheritanceGraphParser(fixture.LinkCache);

            var results = resolver.FindAllTemplates(fixture.ActorWithUnrelatedTemplateFlags,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits).ToImmutableList();
            results.Should().HaveCount(1);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
                {
                    {NpcConfiguration.TemplateFlag.Keywords, fixture.ActorWithUnrelatedTemplateFlags},
                    {NpcConfiguration.TemplateFlag.Traits, fixture.ActorWithUnrelatedTemplateFlags}
                }
            );
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_with_a_simple_direct_template()
        {
            var fixture = new Fixture();
            var resolver = new ActorInheritanceGraphParser(fixture.LinkCache);

            var results = resolver.FindAllTemplates(fixture.ActorWithSimpleActorTemplate,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits).ToImmutableList();
            results.Should().HaveCount(1);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
                {
                    {NpcConfiguration.TemplateFlag.Keywords, fixture.Template1},
                    {NpcConfiguration.TemplateFlag.Traits, fixture.Template1}
                }
            );
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_with_partial_inheritance()
        {
            var fixture = new Fixture();
            var resolver = new ActorInheritanceGraphParser(fixture.LinkCache);

            var results = resolver.FindAllTemplates(fixture.ActorWithPartialInheritance,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits).ToImmutableList();
            results.Should().HaveCount(1);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
                {
                    {NpcConfiguration.TemplateFlag.Keywords, fixture.Template1},
                    {NpcConfiguration.TemplateFlag.Traits, fixture.ActorWithPartialInheritance}
                }
            );
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_inheriting_from_a_leveled_character()
        {
            var fixture = new Fixture();
            var resolver = new ActorInheritanceGraphParser(fixture.LinkCache);

            var results = resolver.FindAllTemplates(fixture.ActorWithLeveledCharTemplate,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits).ToImmutableList();
            results.Should().HaveCount(2);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
            {
                {NpcConfiguration.TemplateFlag.Keywords, fixture.Template2},
                {NpcConfiguration.TemplateFlag.Traits, fixture.Template2}
            });
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcGetter>
            {
                {NpcConfiguration.TemplateFlag.Keywords, fixture.Template3},
                {NpcConfiguration.TemplateFlag.Traits, fixture.Template3}
            });
        }

        [Fact]
        public void Should_return_the_correct_inheritance_information_for_an_actor_with_partial_lchar_inheritance()
        {
            var fixture = new Fixture();
            var resolver = new ActorInheritanceGraphParser(fixture.LinkCache);

            var results = resolver.FindAllTemplates(fixture.ActorWithPartialInheritanceAndNestedLeveledCharTemplate,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits,
                NpcConfiguration.TemplateFlag.Script).ToImmutableList();
            results.Should().HaveCount(2);
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcSpawnGetter>()
                {
                    {NpcConfiguration.TemplateFlag.Keywords, fixture.Template2},
                    {NpcConfiguration.TemplateFlag.Traits, fixture.ActorWithPartialInheritanceAndNestedLeveledCharTemplate},
                    {NpcConfiguration.TemplateFlag.Script, fixture.ActorWithLeveledCharTemplate}
                }
            );
            results.Should().ContainEquivalentOf(new Dictionary<NpcConfiguration.TemplateFlag, INpcSpawnGetter>()
                {
                    {NpcConfiguration.TemplateFlag.Keywords, fixture.Template3},
                    {NpcConfiguration.TemplateFlag.Traits, fixture.ActorWithPartialInheritanceAndNestedLeveledCharTemplate},
                    {NpcConfiguration.TemplateFlag.Script, fixture.ActorWithLeveledCharTemplate}
                }
            );
        }
    }
}