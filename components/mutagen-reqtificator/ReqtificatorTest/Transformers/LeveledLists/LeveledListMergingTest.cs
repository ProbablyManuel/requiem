using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers;
using Reqtificator.Transformers.LeveledItems;
using Reqtificator.Transformers.LeveledLists;
using Xunit;

namespace ReqtificatorTest.Transformers.LeveledLists
{
    public class LeveledListMergingTest
    {
        private readonly LeveledItem.TranslationMask _mask = new(defaultOn: true) { Entries = false };
        public static readonly FormLink<Armor> ItemRef1 = new FormLink<Armor>(FormKey.Factory("ABC123:Skyrim.esm"));
        public static readonly FormLink<Armor> ItemRef2 = new FormLink<Armor>(FormKey.Factory("ABC456:Skyrim.esm"));
        public static readonly FormLink<Armor> ItemRef3 = new FormLink<Armor>(FormKey.Factory("ABC789:Skyrim.esm"));

        public static readonly ModKey Requiem = new ModKey("Requiem", ModType.Plugin);
        public static readonly ModKey Patch1 = new ModKey("Epic Loot", ModType.Plugin);
        public static readonly ModKey Patch2 = new ModKey("Serious Loot", ModType.LightMaster);

        public static readonly SkyrimMod ModRequiem = new SkyrimMod(Requiem, SkyrimRelease.SkyrimSE);
        public static readonly SkyrimMod ModPatch1 = new SkyrimMod(Patch1, SkyrimRelease.SkyrimSE);
        public static readonly SkyrimMod ModPatch2 = new SkyrimMod(Patch2, SkyrimRelease.SkyrimSE);

        public static readonly IImmutableSet<ModKey> ModsWithRequiemAsMaster =
            ImmutableHashSet<ModKey>.Empty.Add(Patch1).Add(Patch2);

        //TODO: rewrite the entire test suite using a proper load order object instead of mocking library classes

        private class Fixture
        {
            public readonly ILeveledItemGetter BaseVersion;
            public readonly ILeveledItemGetter UpdateVersion1;
            public readonly ILeveledItemGetter UpdateVersion2;
            public readonly Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>> Cache = new(MockBehavior.Strict);

            public readonly LeveledListMerging<LeveledItem, ILeveledItem, ILeveledItemGetter, ILeveledItemEntryGetter>
                Transformer;

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
                Cache.SetupGet(c => c.ListedOrder).Returns(ImmutableList.Create(ModRequiem, ModPatch1, ModPatch2));
                Transformer =
                    new LeveledListMerging<LeveledItem, ILeveledItem, ILeveledItemGetter, ILeveledItemEntryGetter>(true,
                        Cache.Object, ModsWithRequiemAsMaster,
                        new CompactLeveledItemUnroller(ImmutableHashSet<ModKey>.Empty),
                        new LeveledItemMerger());
            }

            public void SetupStandardBehaviorCacheMock()
            {
                Cache.Setup(c =>
                        c.ResolveAllContexts<LeveledItem, ILeveledItemGetter>(UpdateVersion2.FormKey,
                            ResolveTarget.Winner))
                    .Returns(new List<IModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>>
                    {
                        new ModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>(Patch2,
                            UpdateVersion2, null!, null!),
                        new ModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>(Patch1,
                            UpdateVersion1, null!, null!),
                        new ModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>(Requiem,
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
            f.SetupStandardBehaviorCacheMock();

            var result = f.Transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
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
            f.SetupStandardBehaviorCacheMock();

            var result = f.Transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
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
            f.SetupStandardBehaviorCacheMock();

            var result = f.Transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
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
            f.SetupStandardBehaviorCacheMock();

            var result = f.Transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
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

        [Fact]
        public void Should_merge_compact_leveled_lists_correctly()
        {
            var baseVersion = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_CLI_LeveledListForMerging",
                Entries = new ExtendedList<LeveledItemEntry>
                {
                    new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 2, Level = 1, Reference = ItemRef2 } }
                }
            };
            var updateVersion1 = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_CLI_LeveledListForMerging",
                Entries = new ExtendedList<LeveledItemEntry>
                {
                    new() { Data = new LeveledItemEntryData { Count = 2, Level = 1, Reference = ItemRef1 } },
                    new() { Data = new LeveledItemEntryData { Count = 1, Level = 1, Reference = ItemRef3 } }
                }
            };
            var updateVersion2 = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                EditorID = "REQ_CLI_LeveledListForMerging",
                Entries = new ExtendedList<LeveledItemEntry>
                {
                    new() { Data = new LeveledItemEntryData { Count = 3, Level = 1, Reference = ItemRef1 } }
                }
            };

            var cache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            cache.Setup(c =>
                    c.ResolveAllContexts<LeveledItem, ILeveledItemGetter>(updateVersion2.FormKey,
                        ResolveTarget.Winner))
                .Returns(new List<IModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>>
                {
                    new ModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>(Patch2,
                        updateVersion2, null!, null!),
                    new ModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>(Patch1,
                        updateVersion1, null!, null!),
                    new ModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>(Requiem,
                        baseVersion, null!, null!)
                });

            cache.SetupGet(c => c.ListedOrder).Returns(ImmutableList.Create(ModRequiem, ModPatch1, ModPatch2));
            var transformer =
                new LeveledListMerging<LeveledItem, ILeveledItem, ILeveledItemGetter, ILeveledItemEntryGetter>(true,
                    cache.Object, ModsWithRequiemAsMaster,
                    new CompactLeveledItemUnroller(ImmutableHashSet<ModKey>.Empty.Add(Requiem)),
                    new LeveledItemMerger());

            var result = transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(updateVersion2));
            result.Should().BeOfType<Modified<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(updateVersion2, _mask).Should().BeTrue();
            var expectedItems = new List<LeveledItemEntry>
            {
                new() { Data = new LeveledItemEntryData { Count = 1, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 1, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 1, Level = 1, Reference = ItemRef1 } },
                new() { Data = new LeveledItemEntryData { Count = 1, Level = 1, Reference = ItemRef3 } }
            };
            result.Record().Entries.Should().BeEquivalentTo(expectedItems);
        }

        [Fact]
        public void Should_not_merge_modifications_and_deletions_if_not_all_merge_candidates_apply_them()
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
            f.SetupStandardBehaviorCacheMock();

            var result = f.Transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
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
        public void Should_not_change_a_record_if_requiem_does_not_change_it()
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
            f.Cache.Setup(c =>
                    c.ResolveAllContexts<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2.FormKey,
                        ResolveTarget.Winner))
                .Returns(new List<IModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>>
                {
                    new ModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>(Patch2,
                        f.UpdateVersion2, null!, null!),
                    new ModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>(Patch1,
                        f.UpdateVersion1, null!, null!)
                });

            var result = f.Transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
            result.Should().BeOfType<UnChanged<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(f.UpdateVersion2).Should().BeTrue();
        }

        [Fact]
        public void Should_not_change_a_record_if_there_are_not_enough_merge_candidates()
        {
            var updateVersion2 = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE);

            var f = new Fixture(updateVersion2);
            f.Cache.Setup(c =>
                    c.ResolveAllContexts<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2.FormKey,
                        ResolveTarget.Winner))
                .Returns(new List<IModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>>
                {
                    new ModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>(Patch2,
                        f.UpdateVersion2, null!, null!),
                    new ModContext<ISkyrimMod, ISkyrimModGetter, LeveledItem, ILeveledItemGetter>(Requiem,
                        f.BaseVersion, null!, null!)
                });

            var result = f.Transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
            result.Should().BeOfType<UnChanged<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(f.UpdateVersion2).Should().BeTrue();
        }

        [Fact]
        public void Should_not_change_a_record_if_merging_has_been_disabled_by_the_user()
        {
            var updateVersion2 = new LeveledItem(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE);

            var f = new Fixture(updateVersion2);
            f.SetupStandardBehaviorCacheMock();

            var transformer =
                new LeveledListMerging<LeveledItem, ILeveledItem, ILeveledItemGetter, ILeveledItemEntryGetter>(false,
                    f.Cache.Object, ModsWithRequiemAsMaster,
                    new CompactLeveledItemUnroller(ImmutableHashSet<ModKey>.Empty), new LeveledItemMerger());

            var result = transformer.Process(new UnChanged<LeveledItem, ILeveledItemGetter>(f.UpdateVersion2));
            result.Should().BeOfType<UnChanged<LeveledItem, ILeveledItemGetter>>();
            result.Record().Equals(f.UpdateVersion2).Should().BeTrue();
        }
    }
}