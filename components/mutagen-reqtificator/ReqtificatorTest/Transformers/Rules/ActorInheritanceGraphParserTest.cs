using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Plugins.Cache.Internals.Implementations;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Exceptions;
using Reqtificator.Transformers.Rules;
using Xunit;

namespace ReqtificatorTest.Transformers.Rules
{
    public class ActorInheritanceGraphParserTest
    {
        private sealed class Fixture
        {
            internal Npc Template1 { get; }
            internal Npc Template2 { get; }
            internal Npc Template3 { get; }
            internal LeveledNpc LeveledCharWithMultipleActors { get; }
            internal ModKey testModKey = new("testplugin", ModType.Master);

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
                        Entries =
                        [
                            new()
                            {
                                Data = new LeveledNpcEntryData { Count = 1, Level = 1, Reference = Template2.ToLink() }
                            },
                            new()
                            {
                                Data = new LeveledNpcEntryData { Count = 1, Level = 1, Reference = Template3.ToLink() }
                            }
                        ]
                    };
            }

            public ImmutableModLinkCache<ISkyrimMod, ISkyrimModGetter> GetCache(params Npc[] npcsToAdd)
            {
                var myMod = new SkyrimMod(testModKey, SkyrimRelease.SkyrimSE);
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
            var resolver = new ActorInheritanceGraphParser(fixture.GetCache(actorWithNoInheritance));

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
            var resolver = new ActorInheritanceGraphParser(fixture.GetCache(actorWithUnrelatedTemplateFlags));

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

            var resolver = new ActorInheritanceGraphParser(fixture.GetCache(actorWithSimpleActorTemplate));

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
            var resolver = new ActorInheritanceGraphParser(fixture.GetCache(actorWithPartialInheritance));

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
            var resolver = new ActorInheritanceGraphParser(fixture.GetCache(actorWithLeveledCharTemplate));

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
                new ActorInheritanceGraphParser(fixture.GetCache(actorWithLeveledCharTemplate,
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

        [Fact]
        public void Should_throw_an_exception_if_the_inheritance_graph_contains_a_circular_dependency()
        {
            var fixture = new Fixture();
            var modKey1 = new ModKey("Npc", ModType.Master);
            var modKey2 = new ModKey("More Npcs", ModType.Plugin);
            var otherFormId = new FormKey(modKey1, 0x123FFF);
            var circularDependentActor1 = new Npc(new FormKey(modKey1, 0x123EEE), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(otherFormId),
                Configuration = new NpcConfiguration()
                {
                    TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Traits
                }
            };
            var circularDependentActor2 = new Npc(otherFormId, SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(circularDependentActor1),
                Configuration = new NpcConfiguration()
                {
                    TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Script
                }
            };
            var actorWithCircularTemplates = new Npc(new FormKey(modKey2, 0x123AAA), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(circularDependentActor2),
                Configuration = new NpcConfiguration()
                {
                    TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Script
                }
            };
            var resolver = new ActorInheritanceGraphParser(fixture.GetCache(circularDependentActor1,
                circularDependentActor2, actorWithCircularTemplates));

            Func<IImmutableList<IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>>> results = () =>
                resolver.FindAllTemplates(actorWithCircularTemplates,
                    NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits,
                    NpcConfiguration.TemplateFlag.Script).ToImmutableList();

            var exception = results.Should().Throw<CircularInheritanceException>().Which;
            exception.Duplicate.FormKey.Should().Be(circularDependentActor2.FormKey);
            exception.TemplateChain.Should().ContainInOrder(new List<(ModKey, INpcSpawnGetter)>
            {
                (fixture.testModKey, actorWithCircularTemplates),
                (fixture.testModKey, circularDependentActor2),
                (fixture.testModKey, circularDependentActor1)
            });
        }

        [Fact]
        public void Should_throw_an_exception_if_the_inheritance_graph_contains_an_unresolved_reference()
        {
            var fixture = new Fixture();
            var unresolvedReference = new FormLinkNullable<INpcSpawnGetter>(FormKey.Factory("000001:Void.esm"));
            var actorWithUnresolvedTemplate = new Npc(FormKey.Factory("123EEE:Npc.esm"), SkyrimRelease.SkyrimSE)
            {
                Template = unresolvedReference,
                Configuration = new NpcConfiguration()
                {
                    TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Traits
                }
            };
            var otherActor = new Npc(FormKey.Factory("123FFF:Npc.esm"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(actorWithUnresolvedTemplate),
                Configuration = new NpcConfiguration()
                {
                    TemplateFlags = NpcConfiguration.TemplateFlag.Keywords | NpcConfiguration.TemplateFlag.Script
                }
            };
            var resolver = new ActorInheritanceGraphParser(fixture.GetCache(actorWithUnresolvedTemplate, otherActor));

            Func<IImmutableList<IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>>> results = () =>
                resolver.FindAllTemplates(otherActor,
                    NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits,
                    NpcConfiguration.TemplateFlag.Script).ToImmutableList();

            var exception = results.Should().Throw<MissingTemplateException>().Which;
            exception.MissingTemplate.FormKey.Should().Be(unresolvedReference.FormKey);
            exception.TemplateChain.Should().ContainInOrder(new List<(ModKey, INpcSpawnGetter)>
            {
                (fixture.testModKey, otherActor),
                (fixture.testModKey, actorWithUnresolvedTemplate)
            });
        }
    }
}