using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.LeveledCharacters;
using Xunit;

namespace ReqtificatorTest.Transformers.LeveledCharacters
{
    public class CompactLeveledCharacterUnrollerTest
    {
        private readonly LeveledNpc.TranslationMask _mask = new(defaultOn: true) { Entries = false };

        [Fact]
        public void Should_unroll_a_compacted_leveled_NPC_originating_from_a_mod_registered_for_this_feature()
        {
            var itemRef1 = new FormLink<Npc>(FormKey.Factory("ABC123:Skyrim.esm"));
            var itemRef2 = new FormLink<Npc>(FormKey.Factory("ABC456:Skyrim.esm"));

            var input = new LeveledNpc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_CLChar_BossMonsters",
                Entries =
                [
                    new() {Data = new LeveledNpcEntryData {Count = 3, Level = 1, Reference = itemRef1}},
                    new() {Data = new LeveledNpcEntryData {Count = 2, Level = 1, Reference = itemRef2}}
                ]
            };
            var modKey = ModKey.FromFileName("Requiem.esp");
            var transformer = new CompactLeveledCharacterUnroller(ImmutableHashSet.Create(modKey));
            var reference = input.DeepCopy();

            transformer.UnrollLeveledList(input);
            input.Equals(reference, _mask).Should().BeTrue();
            var expectedItems = new List<LeveledNpcEntry>
            {
                new() {Data = new LeveledNpcEntryData {Count = 1, Level = 1, Reference = itemRef1}},
                new() {Data = new LeveledNpcEntryData {Count = 1, Level = 1, Reference = itemRef1}},
                new() {Data = new LeveledNpcEntryData {Count = 1, Level = 1, Reference = itemRef1}},
                new() {Data = new LeveledNpcEntryData {Count = 1, Level = 1, Reference = itemRef2}},
                new() {Data = new LeveledNpcEntryData {Count = 1, Level = 1, Reference = itemRef2}}
            };
            input.Entries.Should().BeEquivalentTo(expectedItems);
        }

        [Fact]
        public void Should_not_unroll_a_compacted_leveled_NPC_originating_from_an_unregistered_mod()
        {
            var itemRef1 = new FormLink<Npc>(FormKey.Factory("ABC123:Skyrim.esm"));
            var itemRef2 = new FormLink<Npc>(FormKey.Factory("ABC456:Skyrim.esm"));
            var input = new LeveledNpc(FormKey.Factory("123456:UnregisteredMod.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_CLChar_BossMonsters",
                Entries =
                [
                    new() {Data = new LeveledNpcEntryData {Count = 3, Level = 1, Reference = itemRef1}},
                    new() {Data = new LeveledNpcEntryData {Count = 2, Level = 1, Reference = itemRef2}}
                ]
            };
            var modKey = ModKey.FromFileName("Requiem.esp");
            var unroller = new CompactLeveledCharacterUnroller(ImmutableHashSet.Create(modKey));

            unroller.IsCompactLeveledList(input).Should().BeFalse();
        }

        [Fact]
        public void Should_not_unroll_a_leveled_NPC_not_matching_the_expected_pattern()
        {
            var itemRef1 = new FormLink<Npc>(FormKey.Factory("ABC123:Skyrim.esm"));
            var itemRef2 = new FormLink<Npc>(FormKey.Factory("ABC456:Skyrim.esm"));
            var input = new LeveledNpc(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "Does_not_start_with_the_expected_'<ModPrefix>_CLChar'_pattern",
                Entries =
                [
                    new() {Data = new LeveledNpcEntryData {Count = 3, Level = 1, Reference = itemRef1}},
                    new() {Data = new LeveledNpcEntryData {Count = 2, Level = 1, Reference = itemRef2}}
                ]
            };
            var modKey = ModKey.FromFileName("Requiem.esp");
            var unroller = new CompactLeveledCharacterUnroller(ImmutableHashSet.Create(modKey));

            unroller.IsCompactLeveledList(input).Should().BeFalse();
        }
    }
}