using System;
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
    public class WeaponDamageScalingTest
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

        private readonly WeaponAnimationType[] _rangedTypes =
        [
            WeaponAnimationType.Bow,
            WeaponAnimationType.Crossbow
        ];

        private readonly WeaponAnimationType[] _ignoredTypes =
        [
            WeaponAnimationType.Staff
        ];

        private readonly Weapon.TranslationMask _verificationMask = new(defaultOn: true)
        {
            BasicStats = new WeaponBasicStats.TranslationMask(defaultOn: true) { Damage = false },
        };

        [Fact]
        public void Should_scale_eligible_melee_weapons_of_any_type_with_a_factor_of_6()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponDamageScaling();

            foreach (var weaponType in _meleeTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType
                    },
                    Keywords = [otherKeyword],
                    BasicStats = new WeaponBasicStats { Damage = 5 },
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<Modified<Weapon, IWeaponGetter>>();
                result.Record().Equals(input, _verificationMask).Should().BeTrue();
                result.Record().BasicStats!.Damage.Should().Be((ushort)(input.BasicStats.Damage * 6));
            }
        }

        [Fact]
        public void Should_scale_eligible_ranged_weapons_of_any_type_with_a_factor_of_4()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponDamageScaling();

            foreach (var weaponType in _rangedTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType
                    },
                    Keywords = [otherKeyword],
                    BasicStats = new WeaponBasicStats { Damage = 5 },
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<Modified<Weapon, IWeaponGetter>>();
                result.Record().Equals(input, _verificationMask).Should().BeTrue();
                result.Record().BasicStats!.Damage.Should().Be((ushort)(input.BasicStats.Damage * 4));
            }
        }

        [Fact]
        public void Should_not_modify_records_that_are_neither_melee_nor_ranged_weapons()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponDamageScaling();

            foreach (var weaponType in _ignoredTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType
                    },
                    Keywords = [otherKeyword],
                    BasicStats = new WeaponBasicStats { Damage = 5 }
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));
                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }

        [Fact]
        public void Should_not_modify_records_that_are_marked_as_already_reqtified()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponDamageScaling();

            foreach (var weaponType in Enum.GetValues<WeaponAnimationType>())
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType
                    },
                    Keywords = [otherKeyword, Keywords.AlreadyReqtified],
                    BasicStats = new WeaponBasicStats { Damage = 5 }
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));
                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }

        [Fact]
        public void Should_not_modify_records_that_are_marked_as_no_weapon_damage_scaling()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponDamageScaling();

            foreach (var weaponType in Enum.GetValues<WeaponAnimationType>())
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType
                    },
                    Keywords = [otherKeyword, Keywords.NoDamageRescaling],
                    BasicStats = new WeaponBasicStats { Damage = 5 }
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));
                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }

        [Fact]
        public void Should_not_modify_records_that_have_a_template()
        {
            var templateArmor = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new WeaponDamageScaling();

            foreach (var weaponType in Enum.GetValues<WeaponAnimationType>())
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData { AnimationType = weaponType },
                    BasicStats = new WeaponBasicStats { Damage = 5 },
                    Template = new FormLinkNullable<IWeaponGetter>(templateArmor)
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));
                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }
    }
}