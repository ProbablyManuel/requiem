using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Races;
using Xunit;

namespace ReqtificatorTest.Transformers.Races
{
    public class CustomRacePatchingTest
    {
        // Race.TranslationMask _mask = new Race.TranslationMask(defaultOn: true)
        // { ActorEffect = false, Starting = false, UnarmedDamage = false, BaseCarryWeight = false };

        [Fact]
        public void Should_apply_compatibility_changes_to_an_unpatched_living_custom_race()
        {
            IFormLinkGetter<ISpellRecordGetter> otherSpell =
                new FormLink<ISpellRecordGetter>(FormKey.Factory("ABCDEF:Requiem.esp"));
            var record = new Race(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Flags = Race.Flag.Playable,
                ActorEffect = [otherSpell],
                Starting = { { BasicStat.Health, 60f }, { BasicStat.Magicka, 40f }, { BasicStat.Stamina, 50f } },
                Regen = { { BasicStat.Health, 2.0f }, { BasicStat.Magicka, 0.5f }, { BasicStat.Stamina, 2.0f } },
                UnarmedDamage = 42f,
                BaseCarryWeight = 42f,
            };
            var transformer = new CustomRacePatching();

            var result = transformer.Process(new UnChanged<Race, IRaceGetter>(record));
            result.Should().BeOfType<Modified<Race, IRaceGetter>>();

            //FIXME: seems to not work correctly or the deepcopy does some modification?
            // result.Record().Equals(record, _mask).Should().BeTrue();

            result.Record().Starting.Should().BeEquivalentTo(new Dictionary<BasicStat, float>
            {
                { BasicStat.Health, 110f },
                { BasicStat.Magicka, 90f },
                { BasicStat.Stamina, 100f }
            });
            result.Record().ActorEffect.Should()
                .BeEquivalentTo(ImmutableList.Create(otherSpell).Add(Spells.MassEffectBase).Add(Spells.MassEffectNpc)
                    .Add(Spells.NoNaturalHealthRegeneration));
            result.Record().Regen.Should().BeEquivalentTo(new Dictionary<BasicStat, float>
            {
                { BasicStat.Health, 0.2f },
                { BasicStat.Magicka, 1.1f },
                { BasicStat.Stamina, 1.6f }
            });
            result.Record().UnarmedDamage.Should().Be(8f);
            result.Record().BaseCarryWeight.Should().Be(110f);
        }

        [Fact]
        public void Should_ignore_living_races_that_are_not_playable()
        {
            var record = new Race(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Flags = Race.Flag.Swims,
                ActorEffect = [],
                Starting =
                {
                    { BasicStat.Health, 50f },
                    { BasicStat.Magicka, 50f },
                    { BasicStat.Stamina, 50f }
                }
            };
            var transformer = new CustomRacePatching();

            var result = transformer.Process(new UnChanged<Race, IRaceGetter>(record));
            result.Should().BeOfType<UnChanged<Race, IRaceGetter>>();
            result.Record().Equals(record).Should().BeTrue();
        }

        [Fact]
        public void Should_ignore_races_that_have_more_than_150_attribute_points()
        {
            var record = new Race(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Flags = Race.Flag.Playable,
                ActorEffect = [Spells.MassEffectBase, Spells.MassEffectNpc],
                Starting =
                {
                    { BasicStat.Health, 100f },
                    { BasicStat.Magicka, 100f },
                    { BasicStat.Stamina, 100f }
                }
            };
            var transformer = new CustomRacePatching();

            var result = transformer.Process(new UnChanged<Race, IRaceGetter>(record));
            result.Should().BeOfType<UnChanged<Race, IRaceGetter>>();
            result.Record().Equals(record).Should().BeTrue();
        }
    }
}