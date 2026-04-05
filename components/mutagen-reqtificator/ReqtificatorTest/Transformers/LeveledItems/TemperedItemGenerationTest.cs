using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Exceptions;
using Reqtificator.Transformers;
using Reqtificator.Transformers.LeveledItems;
using Xunit;

namespace ReqtificatorTest.Transformers.LeveledItems
{
    public class TemperedItemGenerationTest
    {
        private readonly LeveledItem.TranslationMask _mask = new(defaultOn: true) { Entries = false };

        [Fact]
        public void Should_add_tempered_versions_for_an_item_with_tier_1_quality_half_power_and_falling_distribution()
        {
            var itemRef1 = new FormLink<Weapon>(FormKey.Factory("ABC123:Skyrim.esm"));

            var input = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_Really_Cool_Dagger_Quality1_H_fall",
                Entries =
                [
                    new() {Data = new LeveledItemEntryData {Count = 1, Level = 1, Reference = itemRef1}},
                ]
            };
            var modKey = ModKey.FromFileName("Requiem.esp");
            var transformer = new TemperedItemGeneration(ImmutableHashSet.Create(modKey));

            var result = transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(input));
            result.Should().BeOfType<Modified<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            var expectedData = new LeveledItemEntryData { Count = 1, Level = 1, Reference = itemRef1 };
            var expectedItems = new List<LeveledItemEntry>
            {
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.0f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.0f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.0f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.1f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.1f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.2f}}
            };
            result.Record().Entries.Should().BeEquivalentTo(expectedItems);
        }

        [Fact]
        public void Should_add_tempered_versions_for_an_item_with_tier_2_quality_normal_power_and_rising_distribution()
        {
            var itemRef1 = new FormLink<Weapon>(FormKey.Factory("ABC123:Skyrim.esm"));

            var input = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_Really_Cool_Sword_Quality2_N_rise",
                Entries =
                [
                    new() {Data = new LeveledItemEntryData {Count = 1, Level = 1, Reference = itemRef1}},
                ]
            };
            var modKey = ModKey.FromFileName("Requiem.esp");
            var transformer = new TemperedItemGeneration(ImmutableHashSet.Create(modKey));

            var result = transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(input));
            result.Should().BeOfType<Modified<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            var expectedData = new LeveledItemEntryData { Count = 1, Level = 1, Reference = itemRef1 };
            var expectedItems = new List<LeveledItemEntry>
            {
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.6f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.7f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.8f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.8f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.9f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 1.9f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 2.0f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 2.0f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 2.0f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 2.1f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 2.1f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 2.1f}}
            };
            result.Record().Entries.Should().BeEquivalentTo(expectedItems);
        }

        [Fact]
        public void Should_add_tempered_versions_for_an_item_with_tier_3_quality_double_power_and_const_distribution()
        {
            var itemRef1 = new FormLink<Weapon>(FormKey.Factory("ABC123:Skyrim.esm"));

            var input = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_Really_Cool_Warhammer_Quality3_D_const",
                Entries =
                [
                    new() {Data = new LeveledItemEntryData {Count = 1, Level = 1, Reference = itemRef1}},
                ]
            };
            var modKey = ModKey.FromFileName("Requiem.esp");
            var transformer = new TemperedItemGeneration(ImmutableHashSet.Create(modKey));

            var result = transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(input));
            result.Should().BeOfType<Modified<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(input, _mask).Should().BeTrue();
            var expectedData = new LeveledItemEntryData { Count = 1, Level = 1, Reference = itemRef1 };
            var expectedItems = new List<LeveledItemEntry>
            {
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 3.4f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 3.5f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 3.6f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 3.7f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 3.8f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 3.9f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 4.0f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 4.1f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 4.2f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 4.3f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 4.4f}},
                new() {Data = expectedData, ExtraData = new ExtraData {ItemCondition = 4.5f}}
            };
            result.Record().Entries.Should().BeEquivalentTo(expectedItems);
        }

        [Fact]
        public void Should_not_modify_leveled_items_without_tempering_qualifiers_in_their_editorID()
        {
            var itemRef1 = new FormLink<Weapon>(FormKey.Factory("ABC123:Skyrim.esm"));

            var input = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_this_record_has_no_tempering_qualifiers_and_will_be_ignored",
                Entries =
                [
                    new() {Data = new LeveledItemEntryData {Count = 1, Level = 1, Reference = itemRef1}},
                ]
            };
            var modKey = ModKey.FromFileName("Requiem.esp");
            var transformer = new TemperedItemGeneration(ImmutableHashSet.Create(modKey));

            var result = transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(input));
            result.Should().BeOfType<UnChanged<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

        [Fact]
        public void Should_not_modify_tempered_leveled_items_if_the_origin_mod_did_not_enable_the_feature()
        {
            var itemRef1 = new FormLink<Weapon>(FormKey.Factory("ABC123:Skyrim.esm"));

            var input = new LeveledItem(FormKey.Factory("123456:ModWithoutTheFeature.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_Really_Cool_Warhammer_Quality3_D_const",
                Entries =
                [
                    new() {Data = new LeveledItemEntryData {Count = 1, Level = 1, Reference = itemRef1}},
                ]
            };
            var modKey = ModKey.FromFileName("Requiem.esp");
            var transformer = new TemperedItemGeneration(ImmutableHashSet.Create(modKey));

            var result = transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(input));
            result.Should().BeOfType<UnChanged<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

        [Fact]
        public void Should_throw_an_exception_if_the_provided_tempering_data_is_invalid()
        {
            var itemRef1 = new FormLink<Weapon>(FormKey.Factory("ABC123:Skyrim.esm"));

            var input = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_Really_Cool_Warhammer_Quality3_H_unknownDistribution",
                Entries =
                [
                    new() {Data = new LeveledItemEntryData {Count = 1, Level = 1, Reference = itemRef1}},
                ]
            };
            var modKey = ModKey.FromFileName("Requiem.esp");
            var transformer = new TemperedItemGeneration(ImmutableHashSet.Create(modKey));

            transformer.Invoking(t => { t.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(input)); })
                .Should().Throw<InvalidTemperingDataException>()
                .WithMessage($"*'{input.FormKey}'*");

        }

        [Fact]
        public void Should_throw_an_exception_if_the_provided_leveled_item_has_multiple_entries()
        {
            var itemRef1 = new FormLink<Weapon>(FormKey.Factory("ABC123:Skyrim.esm"));

            var input = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_Really_Cool_Warhammer_Quality3_D_const",
                Entries =
                [
                    new() {Data = new LeveledItemEntryData {Count = 1, Level = 1, Reference = itemRef1}},
                    new() {Data = new LeveledItemEntryData {Count = 1, Level = 1, Reference = itemRef1}},
                ]
            };
            var modKey = ModKey.FromFileName("Requiem.esp");
            var transformer = new TemperedItemGeneration(ImmutableHashSet.Create(modKey));

            transformer.Invoking(t => { t.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(input)); })
                .Should().Throw<InvalidTemperingDataException>()
                .WithMessage($"*'{input.FormKey}'*");

        }

        [Fact]
        public void Should_throw_an_exception_if_the_provided_leveled_item_has_no_entries()
        {
            var input = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_Really_Cool_Warhammer_Quality3_D_const",
                Entries = []
            };
            var modKey = ModKey.FromFileName("Requiem.esp");
            var transformer = new TemperedItemGeneration(ImmutableHashSet.Create(modKey));

            transformer.Invoking(t => { t.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(input)); })
                .Should().Throw<InvalidTemperingDataException>()
                .WithMessage($"*'{input.FormKey}'*");

        }
    }
}