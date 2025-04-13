using System.Collections.Immutable;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.Rules.Conditions;
using Xunit;

namespace ReqtificatorTest.Transformers.Rules.Conditions
{
    public class HasNoneOfKeywordsTest
    {
        private static readonly IFormLinkGetter<IKeywordGetter> Keyword1 =
            new FormLink<IKeywordGetter>(FormKey.Factory("ABC123:Keywords.esp"));

        private static readonly IFormLinkGetter<IKeywordGetter> Keyword2 =
            new FormLink<IKeywordGetter>(FormKey.Factory("ABC456:Keywords.esp"));

        private static readonly IFormLinkGetter<IKeywordGetter> OtherKeyword =
            new FormLink<IKeywordGetter>(FormKey.Factory("ABC789:Keywords.esp"));

        [Fact]
        public void Should_not_match_records_that_have_all_keywords()
        {
            var armor = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = [Keyword1, Keyword2, OtherKeyword]
            };

            var condition = new HasNoneOfKeywords<IArmorGetter>(
                ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty.Add(Keyword1).Add(Keyword2));
            condition.CheckRecord(armor).Should().BeFalse();
        }

        [Fact]
        public void Should_not_match_records_that_have_only_a_subset_of_the_required_keywords()
        {
            var armor = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = [Keyword1, OtherKeyword]
            };

            var condition = new HasNoneOfKeywords<IArmorGetter>(ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty
                .Add(Keyword1).Add(Keyword2));
            condition.CheckRecord(armor).Should().BeFalse();
        }

        [Fact]
        public void Should_match_records_that_have_none_of_the_required_keywords()
        {
            var armor = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = [OtherKeyword]
            };

            var condition =
                new HasNoneOfKeywords<IArmorGetter>(ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty
                    .Add(Keyword1).Add(Keyword2));
            condition.CheckRecord(armor).Should().BeTrue();
        }

        [Fact]
        public void Should_match_records_that_have_no_keywords_defined()
        {
            var armor = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = null
            };

            var condition =
                new HasNoneOfKeywords<IArmorGetter>(ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty
                    .Add(Keyword1).Add(Keyword2));
            condition.CheckRecord(armor).Should().BeTrue();
        }
    }
}