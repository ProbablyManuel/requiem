using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers;
using Reqtificator.Transformers.LeveledItems;
using Xunit;

namespace ReqtificatorTest.Transformers.LeveledItems
{
    public class LeveledItemMergingTest
    {
        private readonly LeveledItem.TranslationMask _mask = new(defaultOn: true) { Entries = false };
        public static readonly FormLink<Armor> ItemRef1 = new FormLink<Armor>(FormKey.Factory("ABC123:Skyrim.esm"));
        public static readonly FormLink<Armor> ItemRef2 = new FormLink<Armor>(FormKey.Factory("ABC456:Skyrim.esm"));
        public static readonly FormLink<Armor> ItemRef3 = new FormLink<Armor>(FormKey.Factory("ABC789:Skyrim.esm"));

        public static readonly ModKey Requiem = new ModKey("Requiem", ModType.Plugin);
        public static readonly ModKey Patch1 = new ModKey("Epic Loot", ModType.Plugin);
        public static readonly ModKey Patch2 = new ModKey("Serious Loot", ModType.LightMaster);

        public static readonly IImmutableSet<ModKey> ModsWithRequiemAsMaster =
            ImmutableHashSet<ModKey>.Empty.Add(Patch1).Add(Patch2);


        private class Fixture
        {
            public readonly ILeveledItemGetter BaseVersion;
            public readonly ILeveledItemGetter UpdateVersion1;
            public readonly ILeveledItemGetter UpdateVersion2;

            public Fixture(LeveledItem version2)
            {
                BaseVersion = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    EditorID = "REQ_LeveledListForMerging",
                    Entries = new ExtendedList<LeveledItemEntry>
                    {
                        new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                        new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                        new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                        new() { Data = new LeveledItemEntryData { Count = 2, Level = 1, Reference = ItemRef2 } }
                    }
                };
                UpdateVersion1 = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
                {
                    EditorID = "REQ_LeveledListForMerging",
                    Entries = new ExtendedList<LeveledItemEntry>
                    {
                        new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                        new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                        new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                        new() { Data = new LeveledItemEntryData { Count = 4, Level = 1, Reference = ItemRef3 } }
                    }
                };
                UpdateVersion2 = version2;
            }

            public void SetupCacheMock(Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>> cache)
            {
                cache.Setup(c =>
                        c.ResolveAllContexts<ILeveledItem, ILeveledItemGetter>(UpdateVersion2.FormKey,
                            ResolveTarget.Winner))
                    .Returns(new List<IModContext<ISkyrimMod, ISkyrimModGetter, ILeveledItem, ILeveledItemGetter>>
                    {
                        new ModContext<ISkyrimMod, ISkyrimModGetter, ILeveledItem, ILeveledItemGetter>(Patch2,
                            UpdateVersion2, null!, null!),
                        new ModContext<ISkyrimMod, ISkyrimModGetter, ILeveledItem, ILeveledItemGetter>(Patch1,
                            UpdateVersion1, null!, null!),
                        new ModContext<ISkyrimMod, ISkyrimModGetter, ILeveledItem, ILeveledItemGetter>(Requiem,
                            BaseVersion, null!, null!)
                    });
            }
        }

        [Fact]
        public void Should_merge_additions_from_any_contributing_mods()
        {
            var updateVersion2 = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_LeveledListForMerging",
                Entries = new ExtendedList<LeveledItemEntry>
                {
                    new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 2, Level = 1, Reference = ItemRef2 } },
                    new() { Data = new LeveledItemEntryData { Count = 4, Level = 1, Reference = ItemRef3 } }
                }
            };

            var f = new Fixture(updateVersion2);
            var cache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            f.SetupCacheMock(cache);

            var transformer = new LeveledItemMerging(cache.Object, ModsWithRequiemAsMaster);

            var result = transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
            result.Should().BeOfType<Modified<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(f.UpdateVersion2, _mask).Should().BeTrue();
            var expectedItems = new List<LeveledItemEntry>
            {
                new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 2, Level = 1, Reference = ItemRef2 } },
                new() { Data = new LeveledItemEntryData { Count = 4, Level = 1, Reference = ItemRef3 } },
                new() { Data = new LeveledItemEntryData { Count = 4, Level = 1, Reference = ItemRef3 } }
            };
            result.Record().Entries.Should().BeEquivalentTo(expectedItems);
        }

        [Fact]
        public void Should_merge_positive_modifications_if_all_merge_candidates_apply_them()
        {
            var updateVersion2 = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_LeveledListForMerging",
                Entries = new ExtendedList<LeveledItemEntry>
                {
                    new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 2, Level = 1, Reference = ItemRef2 } }
                }
            };

            var f = new Fixture(updateVersion2);
            var cache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            f.SetupCacheMock(cache);

            var transformer = new LeveledItemMerging(cache.Object, ModsWithRequiemAsMaster);

            var result = transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
            result.Should().BeOfType<Modified<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(f.UpdateVersion2, _mask).Should().BeTrue();
            var expectedItems = new List<LeveledItemEntry>
            {
                new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 2, Level = 1, Reference = ItemRef2 } },
                new() { Data = new LeveledItemEntryData { Count = 4, Level = 1, Reference = ItemRef3 } }
            };
            result.Record().Entries.Should().BeEquivalentTo(expectedItems);
        }

        [Fact]
        public void Should_merge_negative_modifications_if_all_merge_candidates_apply_them()
        {
            var updateVersion2 = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_LeveledListForMerging",
                Entries = new ExtendedList<LeveledItemEntry>
                {
                    new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 2, Level = 1, Reference = ItemRef2 } }
                }
            };

            var f = new Fixture(updateVersion2);
            var cache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            f.SetupCacheMock(cache);

            var transformer = new LeveledItemMerging(cache.Object, ModsWithRequiemAsMaster);

            var result = transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
            result.Should().BeOfType<Modified<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(f.UpdateVersion2, _mask).Should().BeTrue();
            var expectedItems = new List<LeveledItemEntry>
            {
                new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 2, Level = 1, Reference = ItemRef2 } },
                new() { Data = new LeveledItemEntryData { Count = 4, Level = 1, Reference = ItemRef3 } }
            };
            result.Record().Entries.Should().BeEquivalentTo(expectedItems);
        }

        [Fact]
        public void Should_merge_deletions_if_all_merge_candidates_apply_them()
        {
            var updateVersion2 = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_LeveledListForMerging",
                Entries = new ExtendedList<LeveledItemEntry>
                {
                    new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } }
                }
            };

            var f = new Fixture(updateVersion2);
            var cache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            f.SetupCacheMock(cache);

            var transformer = new LeveledItemMerging(cache.Object, ModsWithRequiemAsMaster);

            var result = transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
            result.Should().BeOfType<Modified<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(f.UpdateVersion2, _mask).Should().BeTrue();
            var expectedItems = new List<LeveledItemEntry>
            {
                new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 20, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 4, Level = 1, Reference = ItemRef3 } }
            };
            result.Record().Entries.Should().BeEquivalentTo(expectedItems);
        }
    }
}