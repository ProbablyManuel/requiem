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
    public class WeaponNpcAmmunitionUsageTest
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
            Data = new WeaponData.TranslationMask(defaultOn: true) { Flags = false }
        };

        [Fact]
        public void Should_enable_npc_ammunition_usage_for_eligible_ranged_weapons()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponNpcAmmunitionUsage();

            foreach (var weaponType in _rangedTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Flags = WeaponData.Flag.MinorCrime
                    },
                    Keywords = new KeywordList { otherKeyword }
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<Modified<Weapon, IWeaponGetter>>();
                result.Record().Equals(input, _verificationMask).Should().BeTrue();
                result.Record().Data!.Flags.Should().Be(input.Data!.Flags | WeaponData.Flag.NPCsUseAmmo);
            }
        }

        [Fact]
        public void Should_ignore_other_weapon_types()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponNpcAmmunitionUsage();

            foreach (var weaponType in _ignoredTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Flags = WeaponData.Flag.MinorCrime
                    },
                    Keywords = new KeywordList { otherKeyword }
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
            var transformer = new WeaponNpcAmmunitionUsage();

            foreach (var weaponType in _rangedTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Flags = WeaponData.Flag.MinorCrime
                    },
                    Keywords = new KeywordList { otherKeyword, Keywords.AlreadyReqtified }
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
            var transformer = new WeaponNpcAmmunitionUsage();

            foreach (var weaponType in _rangedTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Flags = WeaponData.Flag.NonPlayable
                    },
                    Keywords = new KeywordList { otherKeyword }
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }

        [Fact]
        public void Should_ignore_records_already_having_npc_ammo_usage_flag()
        {
            var otherKeyword = Keywords.ArmorBody;
            var transformer = new WeaponNpcAmmunitionUsage();

            foreach (var weaponType in _rangedTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Flags = WeaponData.Flag.NPCsUseAmmo
                    },
                    Keywords = new KeywordList { otherKeyword }
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
            var transformer = new WeaponNpcAmmunitionUsage();

            foreach (var weaponType in _rangedTypes)
            {
                var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    Data = new WeaponData()
                    {
                        AnimationType = weaponType,
                        Flags = WeaponData.Flag.MinorCrime
                    },
                    Keywords = new KeywordList { otherKeyword },
                    Template = new FormLinkNullable<IWeaponGetter>(FormKey.Factory("ABCDEF:Requiem.esp"))
                };
                var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));

                result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }
    }
}