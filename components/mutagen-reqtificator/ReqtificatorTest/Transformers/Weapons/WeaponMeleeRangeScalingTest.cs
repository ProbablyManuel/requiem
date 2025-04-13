using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Weapons;
using Xunit;
using KeywordList = Noggog.ExtendedList<Mutagen.Bethesda.Plugins.IFormLinkGetter<Mutagen.Bethesda.Skyrim.IKeywordGetter>>;

namespace ReqtificatorTest.Transformers.Weapons
{
    public class WeaponMeleeRangeScalingTest
    {
        private readonly WeaponAnimationType[] _meleeTypes =
        [
            WeaponAnimationType.HandToHand,
            WeaponAnimationType.OneHandAxe,
            WeaponAnimationType.OneHandDagger,
            WeaponAnimationType.OneHandMace,
            WeaponAnimationType.OneHandSword,
            WeaponAnimationType.TwoHandAxe,
            WeaponAnimationType.TwoHandSword
        ];

        private readonly WeaponAnimationType[] _ignoredTypes =
        [
            WeaponAnimationType.Bow,
            WeaponAnimationType.Crossbow,
            WeaponAnimationType.Staff
        ];

        private readonly Weapon.TranslationMask _verificationMask = new(defaultOn: true)
        {
            Data = new WeaponData.TranslationMask(defaultOn: true) { Reach = false }
        };

        [Fact]
        public void Should_scale_down_reach_of_eligible_melee_weapons()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponMeleeRangeScaling();

            foreach (var weaponType in _meleeTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Reach = 10.0f
                    },
                    Keywords = [otherKeyword]
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<Modified<Weapon, IWeaponGetter>>();
                result.Record().Equals(input, _verificationMask).Should().BeTrue();
                result.Record().Data!.Reach.Should().BeApproximately(input.Data.Reach * 0.7f, 0.01f);
            }
        }

        [Fact]
        public void Should_ignore_any_non_melee_weapon_types()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponMeleeRangeScaling();

            foreach (var weaponType in _ignoredTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Reach = 10.0f
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
            var transformer = new WeaponMeleeRangeScaling();

            foreach (var weaponType in _meleeTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Reach = 10.0f
                    },
                    Keywords = [otherKeyword, Keywords.AlreadyReqtified]
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }

        [Fact]
        public void Should_ignore_records_marked_as_no_weapon_reach_rescaling()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponMeleeRangeScaling();

            foreach (var weaponType in _meleeTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Reach = 10.0f
                    },
                    Keywords = [otherKeyword, Keywords.NoWeaponReachRescaling]
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
            var transformer = new WeaponMeleeRangeScaling();

            foreach (var weaponType in _meleeTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Reach = 10.0f
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