using System.Collections.Immutable;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Weapons;
using Xunit;
using static ReqtificatorTest.Transformers.Rules.AssignmentRuleFixture<Mutagen.Bethesda.Skyrim.IWeaponGetter>;

namespace ReqtificatorTest.Transformers.Weapons
{
    public class WeaponKeywordsFromRulesTest
    {
        private readonly Weapon.TranslationMask _mask = new(defaultOn: true) { Keywords = false };

        [Fact]
        public void Should_apply_assignments_from_a_matching_rule_for_a_weapon_without_template()
        {
            var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = [CheckKeywordNode1, CheckKeywordRoot],
                Template = new FormLinkNullable<IWeaponGetter>(FormKey.Null)
            };

            var transformer = new WeaponKeywordsFromRules([TestRule]);

            var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));
            result.Should().BeOfType<Modified<Weapon, IWeaponGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            var expectedKeywords = input.Keywords!.ToImmutableList().Add(AssignKeywordNode1);
            result.Record().Keywords.Should().BeEquivalentTo(expectedKeywords);
        }

        [Fact]
        public void Should_apply_assignments_from_multiple_matching_rules_for_a_weapon_without_template()
        {
            var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = [CheckKeywordNode1, CheckKeywordNode2, CheckKeywordRoot],
                Template = new FormLinkNullable<IWeaponGetter>(FormKey.Null)
            };

            var transformer = new WeaponKeywordsFromRules([TestRule]);

            var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));
            result.Should().BeOfType<Modified<Weapon, IWeaponGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            var expectedKeywords = input.Keywords!.ToImmutableList().Add(AssignKeywordNode1).Add(AssignKeywordNode2);
            result.Record().Keywords.Should().BeEquivalentTo(expectedKeywords);
        }

        [Fact]
        public void Should_not_change_weapons_if_no_rule_matches()
        {
            var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = [CheckKeywordNode1, CheckKeywordNode2],
                Template = new FormLinkNullable<IWeaponGetter>(FormKey.Null)
            };

            var transformer = new WeaponKeywordsFromRules([TestRule]);

            var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));
            result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

        [Fact]
        public void Should_not_change_weapons_with_a_template()
        {
            var input = new Weapon(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = [CheckKeywordNode1, CheckKeywordRoot],
                Template = new FormLinkNullable<IWeaponGetter>(FormKey.Factory("ABCDEF:Template.esm"))
            };

            var transformer = new WeaponKeywordsFromRules([TestRule]);

            var result = transformer.Process(new UnChanged<Weapon, IWeaponGetter>(input));
            result.Should().BeOfType<UnChanged<Weapon, IWeaponGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }
    }
}