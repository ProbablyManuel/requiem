using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.LeveledLists;
using Serilog;

namespace Reqtificator.Transformers.LeveledItems
{
    internal class LeveledItemMerging : TransformerV2<LeveledItem, ILeveledItemGetter>
    {
        private enum Operation
        {
            Addition,
            Deletion,
            Modification
        }

        private record Change(Operation Type, ILeveledItemEntryGetter Reference, int Count);

        private record EntryKey(IFormLinkGetter<IItemGetter> Reference, int Level, int Count);

        private readonly bool _mergeEnabled;
        private readonly ILinkCache<ISkyrimMod, ISkyrimModGetter> _cache;
        private readonly IImmutableSet<ModKey> _modsEligibleForMerging;
        private readonly ICompactLeveledListUnroller<LeveledItem, ILeveledItemGetter, ILeveledItemEntryGetter> _unroller;
        private static readonly ModKey Requiem = new ModKey("Requiem", ModType.Plugin);

        private static readonly LeveledItem.TranslationMask CopyMask = new(defaultOn: false) { Entries = true };

        public LeveledItemMerging(bool mergeEnabled, ILinkCache<ISkyrimMod, ISkyrimModGetter> cache,
            IImmutableSet<ModKey> modsWithRequiemAsMaster, ICompactLeveledListUnroller<LeveledItem, ILeveledItemGetter, ILeveledItemEntryGetter> compactLeveledItemUnroller)
        {
            _mergeEnabled = mergeEnabled;
            _cache = cache;
            _modsEligibleForMerging = modsWithRequiemAsMaster.Add(Requiem);
            _unroller = compactLeveledItemUnroller;
        }

        public override TransformationResult<LeveledItem, ILeveledItemGetter> Process(
            TransformationResult<LeveledItem, ILeveledItemGetter> input)
        {
            if (!_mergeEnabled) return input;

            var toMerge = _cache.ResolveAllContexts<ILeveledItem, ILeveledItemGetter>(input.Record().FormKey)
                .Where(x => _modsEligibleForMerging.Contains(x.ModKey)).ToList();
            var baseVersion = toMerge.Find(x => x.ModKey == Requiem)?.Record;

            if (baseVersion == null || toMerge.Count < 3) return input;

            var unrolledBaseEntries = _unroller.GetUnrolledEntries(baseVersion);

            var updates = toMerge.Where(x => x.ModKey != Requiem)
                .Select(x =>
                    GetDifferences(unrolledBaseEntries, _unroller.GetUnrolledEntries(x.Record))
                ).ToImmutableList();

            return input.Modify(record =>
            {
                record.DeepCopyIn(baseVersion, out _, CopyMask);
                record.Entries = unrolledBaseEntries.Select(r => r.DeepCopy()).ToExtendedList();

                updates.SelectMany(cs => cs.Where(c => c.Type == Operation.Addition))
                    .ForEach(c =>
                        Enumerable.Range(1, c.Count).ForEach(_ => record.Entries.Add(c.Reference.DeepCopy())));

                var modifications =
                    updates.Select(cs => cs.Where(c => c.Type == Operation.Modification).ToImmutableList())
                        .ToImmutableList();
                modifications.SelectMany(cs => cs).Distinct()
                    .Where(c => modifications.All(m => m.Contains(c)))
                    .ForEach(c =>
                        Enumerable.Range(1, Math.Abs(c.Count)).ForEach(_ =>
                        {
                            if (c.Count > 0) record.Entries.Add(c.Reference.DeepCopy());
                            else record.Entries.Remove(c.Reference.DeepCopy());
                        }));

                var deletions =
                    updates.Select(cs => cs.Where(c => c.Type == Operation.Deletion).ToImmutableList())
                        .ToImmutableList();
                deletions.SelectMany(cs => cs).Distinct()
                    .Where(c => deletions.All(m => m.Contains(c)))
                    .ForEach(c => record.Entries.RemoveAll(x => x.Equals(c.Reference.DeepCopy())));
                Log.Information($"merged leveled lists from {updates.Count} patches");
            });
        }

        private static IImmutableList<Change> GetDifferences(IReadOnlyList<ILeveledItemEntryGetter> baseVersion,
            IReadOnlyList<ILeveledItemEntryGetter> newVersion)
        {
            var baseEntries = (baseVersion)
                .Where(x => x.Data != null)
                .GroupBy(x => new EntryKey(x.Data!.Reference, x.Data.Level, x.Data.Count), x => x, (_, xs) =>
                {
                    var leveledItemEntryGetters = xs as ILeveledItemEntryGetter[] ?? xs.ToArray();
                    return KeyValuePair.Create(leveledItemEntryGetters.First(), leveledItemEntryGetters.Length);
                }).ToImmutableDictionary();

            var otherEntries = (newVersion)
                .Where(x => x.Data != null)
                .GroupBy(x => new EntryKey(x.Data!.Reference, x.Data.Level, x.Data.Count), x => x, (_, xs) =>
                {
                    var leveledItemEntryGetters = xs as ILeveledItemEntryGetter[] ?? xs.ToArray();
                    return KeyValuePair.Create(leveledItemEntryGetters.First(), leveledItemEntryGetters.Length);
                }).ToImmutableDictionary();

            var additions = otherEntries.Where(e => !baseEntries.ContainsKey(e.Key))
                .Select(e => new Change(Operation.Addition, e.Key, e.Value));

            var modifications = otherEntries.Where(e => baseEntries.ContainsKey(e.Key) && baseEntries[e.Key] != e.Value)
                .Select(e =>
                    new Change(Operation.Modification, e.Key, e.Value - baseEntries[e.Key]));

            var deletions = baseEntries.Where(e => !otherEntries.ContainsKey(e.Key))
                .Select(e => new Change(Operation.Deletion, e.Key, -e.Value));

            return additions.Concat(modifications).Concat(deletions).ToImmutableList();
        }
    }
}