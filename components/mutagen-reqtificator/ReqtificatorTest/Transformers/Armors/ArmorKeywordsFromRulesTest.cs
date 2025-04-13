using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Armors;
using Reqtificator.Transformers.Rules;
using Xunit;
using static ReqtificatorTest.Transformers.Rules.AssignmentRuleFixture<Mutagen.Bethesda.Skyrim.IArmorGetter>;

namespace ReqtificatorTest.Transformers.Armors
{
    public class ArmorKeywordsFromRulesTest
    {
        private readonly Armor.TranslationMask _mask = new(defaultOn: true) { Keywords = false };

        [Fact]
        public void Should_apply_assignments_from_a_matching_rule_for_an_armor_without_template()
        {
            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = [CheckKeywordNode1, CheckKeywordRoot],
                TemplateArmor = new FormLinkNullable<IArmorGetter>(FormKey.Null)
            };

            var transformer = new ArmorKeywordsFromRules([TestRule]);

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().BeOfType<Modified<Armor, IArmorGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            var expectedKeywords = input.Keywords!.ToImmutableList().Add(AssignKeywordNode1);
            result.Record().Keywords.Should().BeEquivalentTo(expectedKeywords);
        }

        [Fact]
        public void Should_apply_assignments_from_multiple_matching_rules_for_an_armor_without_template()
        {
            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = [CheckKeywordNode1, CheckKeywordNode2, CheckKeywordRoot],
                TemplateArmor = new FormLinkNullable<IArmorGetter>(FormKey.Null)
            };

            var transformer = new ArmorKeywordsFromRules([TestRule]);

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().BeOfType<Modified<Armor, IArmorGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            var expectedKeywords = input.Keywords!.ToImmutableList().Add(AssignKeywordNode1).Add(AssignKeywordNode2);
            result.Record().Keywords.Should().BeEquivalentTo(expectedKeywords);
        }

        [Fact]
        public void Should_not_change_armors_if_no_rule_matches()
        {
            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = [CheckKeywordNode1, CheckKeywordNode2],
                TemplateArmor = new FormLinkNullable<IArmorGetter>(FormKey.Null)
            };

            var transformer = new ArmorKeywordsFromRules([TestRule]);

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().BeOfType<UnChanged<Armor, IArmorGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

        [Fact]
        public void Should_not_change_armors_with_a_template()
        {
            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = [CheckKeywordNode1, CheckKeywordRoot],
                TemplateArmor = new FormLinkNullable<IArmorGetter>(FormKey.Factory("ABCDEF:Template.esm"))
            };

            var transformer = new ArmorKeywordsFromRules([TestRule]);

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().BeOfType<UnChanged<Armor, IArmorGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }
    }
}