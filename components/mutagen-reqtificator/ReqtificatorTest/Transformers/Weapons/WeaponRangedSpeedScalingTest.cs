using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Weapons;
using Xunit;
using KeywordList =
    Noggog.ExtendedList<Mutagen.Bethesda.Plugins.IFormLinkGetter<Mutagen.Bethesda.Skyrim.IKeywordGetter>>;

namespace ReqtificatorTest.Transformers.Weapons
{
    public class WeaponRangedSpeedScalingTest
    {
        private readonly WeaponAnimationType[] _rangedTypes =
        {
            WeaponAnimationType.Bow,
            WeaponAnimationType.Crossbow
        };

        private readonly WeaponAnimationType[] _ignoredTypes =
        {
            WeaponAnimationType.HandToHand,
            WeaponAnimationType.OneHandAxe,
            WeaponAnimationType.OneHandDagger,
            WeaponAnimationType.OneHandMace,
            WeaponAnimationType.OneHandSword,
            WeaponAnimationType.TwoHandAxe,
            WeaponAnimationType.TwoHandSword,
            WeaponAnimationType.Staff
        };

        private readonly Weapon.TranslationMask _verificationMask = new(defaultOn: true)
        {
            Data = new WeaponData.TranslationMask(defaultOn: true) { Speed = false },
            Keywords = false
        };

        [Fact]
        public void Should_adjust_speed_and_add_heavy_bow_keyword_to_eligible_bows()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponRangedSpeedScaling();

            var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Data = new WeaponData()
                {
                    AnimationType = WeaponAnimationType.Bow,
                    Speed = 0.75f
                },
                Keywords = [otherKeyword]
            };
            var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

            result.Should().BeOfType<Modified<Weapon, IWeaponGetter>>();
            result.Record().Equals(input, _verificationMask).Should().BeTrue();
            result.Record().Data!.Speed.Should().BeApproximately(0.3704f, 0.01f);
            result.Record().Keywords!.Should().Contain([Keywords.WeaponBowHeavy, otherKeyword]);
        }

        [Fact]
        public void Should_adjust_speed_and_add_heavy_crossbow_keyword_to_eligible_crossbows()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponRangedSpeedScaling();

            var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Data = new WeaponData()
                {
                    AnimationType = WeaponAnimationType.Crossbow,
                    Speed = 0.75f
                },
                Keywords = [otherKeyword]
            };
            var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

            result.Should().BeOfType<Modified<Weapon, IWeaponGetter>>();
            result.Record().Equals(input, _verificationMask).Should().BeTrue();
            result.Record().Data!.Speed.Should().BeApproximately(0.4445f, 0.01f);
            result.Record().Keywords!.Should().Contain([Keywords.WeaponCrossbowHeavy, otherKeyword]);
        }

        [Fact]
        public void Should_ignore_other_weapon_types()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponRangedSpeedScaling();

            foreach (var weaponType in _ignoredTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Speed = 1.0f
                    },
                    Keywords = [otherKeyword]
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }

        [Fact]
        public void Should_ignore_records_marked_as_already_reqtified()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponRangedSpeedScaling();

            foreach (var weaponType in _rangedTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Speed = 1.0f
                    },
                    Keywords = [otherKeyword, Keywords.AlreadyReqtified]
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }


        [Fact]
        public void Should_ignore_records_marked_as_no_speed_rescaling()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponRangedSpeedScaling();

            foreach (var weaponType in _rangedTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Speed = 1.0f
                    },
                    Keywords = [otherKeyword, Keywords.NoWeaponSpeedRescaling]
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }

        [Fact]
        public void Should_ignore_records_marked_as_not_playable()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponRangedSpeedScaling();

            foreach (var weaponType in _rangedTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Speed = 1.0f,
                        Flags = WeaponData.Flag.NonPlayable
                    },
                    Keywords = [otherKeyword]
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }

        [Fact]
        public void Should_ignore_records_with_a_template()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponRangedSpeedScaling();

            foreach (var weaponType in _rangedTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Speed = 1.0f
                    },
                    Keywords = [otherKeyword],
                    Template = new FormLinkNullable<IWeaponGetter>(FormKey.Factory("ABCDEF:Requiem.esp"))
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }
    }
}