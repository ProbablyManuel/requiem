using System.Collections.Immutable;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Actors;
using Xunit;

namespace ReqtificatorTest.Transformers.Actors
{
    public class ActorCommonScriptsTest
    {
        private readonly Npc.TranslationMask _mask = new(defaultOn: true)
        {
            VirtualMachineAdapter = new VirtualMachineAdapter.TranslationMask(defaultOn: true) { Scripts = false }
        };

        private readonly Npc.TranslationMask _maskWithFlag = new(defaultOn: true)
        {
            VirtualMachineAdapter = new VirtualMachineAdapter.TranslationMask(defaultOn: true) { Scripts = false },
            Configuration = new NpcConfiguration.TranslationMask(defaultOn: true) { TemplateFlags = false }
        };

        private readonly ImmutableList<ScriptEntry> _scripts =
            new[] { new ScriptEntry { Name = "REQ_NPCData" } }.ToImmutableList();

        [Fact]
        public void Should_add_the_global_scripts_to_actors_without_template()
        {
            var linkCache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            var otherScript = new ScriptEntry { Name = "MightyFooScript" };
            var transformer = new ActorCommonScripts(linkCache.Object);

            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(FormKey.Null),
                Configuration = new NpcConfiguration { TemplateFlags = 0 },
                VirtualMachineAdapter = new VirtualMachineAdapter
                {
                    Scripts = new ExtendedList<ScriptEntry> { otherScript }
                }
            };
            INpcSpawnGetter returnValue = null;
            linkCache.Setup(c => c.TryResolve(FormKey.Null, out returnValue, ResolveTarget.Winner)).Returns(false);

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            result.Record().VirtualMachineAdapter!.Scripts.Should().BeEquivalentTo(_scripts.Add(otherScript));
        }

        [Fact]
        public void Should_add_the_global_scripts_to_actors_with_a_leveled_character_template()
        {
            var linkCache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            var template = FormKey.Factory("123456:Template.esm");
            var otherScript = new ScriptEntry { Name = "MightyFooScript" };
            var transformer = new ActorCommonScripts(linkCache.Object);

            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(template),
                Configuration = new NpcConfiguration { TemplateFlags = 0 },
                VirtualMachineAdapter = new VirtualMachineAdapter
                {
                    Scripts = new ExtendedList<ScriptEntry> { otherScript }
                }
            };
            INpcSpawnGetter returnValue = new LeveledNpc(template, SkyrimRelease.SkyrimSE);
            linkCache.Setup(c => c.TryResolve(template, out returnValue, ResolveTarget.Winner)).Returns(true);

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            result.Record().VirtualMachineAdapter!.Scripts.Should().BeEquivalentTo(_scripts.Add(otherScript));
        }

        [Fact]
        public void Should_add_the_global_scripts_to_actors_with_a_leveled_character_template_with_script_inheritance()
        {
            var linkCache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            var template = FormKey.Factory("123456:Template.esm");
            var otherScript = new ScriptEntry { Name = "MightyFooScript" };
            var transformer = new ActorCommonScripts(linkCache.Object);

            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(template),
                Configuration = new NpcConfiguration { TemplateFlags = NpcConfiguration.TemplateFlag.Script },
                VirtualMachineAdapter = new VirtualMachineAdapter
                {
                    Scripts = new ExtendedList<ScriptEntry> { otherScript }
                }
            };
            INpcSpawnGetter returnValue = new LeveledNpc(template, SkyrimRelease.SkyrimSE);
            linkCache.Setup(c => c.TryResolve(template, out returnValue, ResolveTarget.Winner)).Returns(true);

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, _maskWithFlag).Should().BeTrue();
            result.Record().VirtualMachineAdapter!.Scripts.Should().BeEquivalentTo(_scripts.Add(otherScript));
            result.Record().Configuration.TemplateFlags.HasFlag(NpcConfiguration.TemplateFlag.Script).Should()
                .BeFalse();
        }

        [Fact]
        public void Should_add_the_global_scripts_to_actors_with_an_actor_template_and_no_script_inheritance()
        {
            var linkCache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            var template = FormKey.Factory("123456:Template.esm");
            var otherScript = new ScriptEntry { Name = "MightyFooScript" };
            var transformer = new ActorCommonScripts(linkCache.Object);

            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(template),
                Configuration = new NpcConfiguration { TemplateFlags = 0 },
                VirtualMachineAdapter = new VirtualMachineAdapter
                {
                    Scripts = new ExtendedList<ScriptEntry> { otherScript }
                }
            };
            INpcSpawnGetter returnValue = new Npc(template, SkyrimRelease.SkyrimSE);
            linkCache.Setup(c => c.TryResolve(template, out returnValue, ResolveTarget.Winner)).Returns(true);

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            result.Record().VirtualMachineAdapter!.Scripts.Should().BeEquivalentTo(_scripts.Add(otherScript));
        }

        [Fact]
        public void Should_ignore_actors_that_inherit_scripts_from_another_actor()
        {
            var linkCache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            var template = FormKey.Factory("123456:Template.esm");
            var otherScript = new ScriptEntry { Name = "MightyFooScript" };
            var transformer = new ActorCommonScripts(linkCache.Object);

            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(template),
                Configuration = new NpcConfiguration { TemplateFlags = NpcConfiguration.TemplateFlag.Script },
                VirtualMachineAdapter = new VirtualMachineAdapter
                {
                    Scripts = new ExtendedList<ScriptEntry> { otherScript }
                }
            };
            INpcSpawnGetter returnValue = new Npc(template, SkyrimRelease.SkyrimSE);
            linkCache.Setup(c => c.TryResolve(template, out returnValue, ResolveTarget.Winner)).Returns(true);

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

        [Fact]
        public void Should_ignore_the_player_record()
        {
            var linkCache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            var otherScript = new ScriptEntry { Name = "MightyFooScript" };
            var transformer = new ActorCommonScripts(linkCache.Object);

            var input = new Npc(Npcs.Player.FormKey, SkyrimRelease.SkyrimSE)
            {
                Template = new FormLinkNullable<INpcSpawnGetter>(FormKey.Null),
                Configuration = new NpcConfiguration { TemplateFlags = 0 },
                VirtualMachineAdapter = new VirtualMachineAdapter
                {
                    Scripts = new ExtendedList<ScriptEntry> { otherScript }
                }
            };

            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }
    }
}