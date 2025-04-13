using System.Linq;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Configuration;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Actors;
using Xunit;

namespace ReqtificatorTest.Transformers.Actors
{
    public class PlayerChangesTest
    {
        private static readonly Npc.TranslationMask Mask = new(defaultOn: true)
        {
            Configuration = new NpcConfiguration.TranslationMask(defaultOn: true)
            {
                HealthOffset = false,
                StaminaOffset = false,
                MagickaOffset = false
            },
            ActorEffect = false
        };

        [Fact]
        public void Should_apply_the_attribute_changes_and_remove_spells_to_the_player_character()
        {
            var spellToRemove = new FormLink<ISpellGetter>(FormKey.Factory("ABC123:Skyrim.esm"));
            var otherSpell = new FormLink<ISpellGetter>(FormKey.Factory("ABC456:Skyrim.esm"));
            var config = new PlayerConfig(13, -42, 7, new[] { spellToRemove });
            var transformer = new PlayerChanges(config);

            var input = new Npc(FormKey.Factory("000007:Skyrim.esm"), SkyrimRelease.SkyrimSE)
            {
                Configuration = new NpcConfiguration { HealthOffset = 0, StaminaOffset = 0, MagickaOffset = 0 },
                ActorEffect = [spellToRemove, otherSpell]
            };
            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().Equals(input, Mask).Should().BeTrue();
            result.Record().ActorEffect.Should().BeEquivalentTo(new[] { otherSpell }.ToList());
            result.Record().Configuration.HealthOffset.Should().Be((short)config.HealthOffset);
            result.Record().Configuration.MagickaOffset.Should().Be((short)config.MagickaOffset);
            result.Record().Configuration.StaminaOffset.Should().Be((short)config.StaminaOffset);
        }

        [Fact]
        public void Should_ignore_all_other_nps()
        {
            var spellToRemove = new FormLink<ISpellGetter>(FormKey.Factory("ABC123:Skyrim.esm"));
            var otherSpell = new FormLink<ISpellGetter>(FormKey.Factory("ABC456:Skyrim.esm"));
            var config = new PlayerConfig(13, -42, 7, new[] { spellToRemove });
            var transformer = new PlayerChanges(config);

            var input = new Npc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Configuration = new NpcConfiguration { HealthOffset = 0, StaminaOffset = 0, MagickaOffset = 0 },
                ActorEffect = [spellToRemove, otherSpell]
            };
            var result = transformer.Process(new UnChanged<Npc, INpcGetter>(input));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }
    }
}