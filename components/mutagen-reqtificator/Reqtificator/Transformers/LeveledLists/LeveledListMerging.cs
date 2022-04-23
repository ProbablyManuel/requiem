using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Serilog;

namespace Reqtificator.Transformers.LeveledLists
{
    internal static class LeveledListMerging
    {
        public record LeveledListEntry(FormKey Reference, int Level, int Count);
    }

    internal class LeveledListMerging<T, TI, TGetter, TEntry> : Transformer<T, TI, TGetter> where T : MajorRecord, TI
        where TI : class, IMajorRecord, TGetter
        where TGetter : class, IMajorRecordGetter
    {
        private enum Operation
        {
            Addition,
            Deletion,
            Modification
        }

        private record Change(Operation Type, LeveledListMerging.LeveledListEntry Reference, int Count);

        private readonly bool _mergeEnabled;
        private readonly ILinkCache<ISkyrimMod, ISkyrimModGetter> _cache;
        private readonly IImmutableSet<ModKey> _modsEligibleForMerging;
        private readonly IImmutableDictionary<ModKey, ImmutableHashSet<ModKey>> _invertedMasterMap;
        private readonly ICompactLeveledListUnroller<T, TGetter, TEntry> _unroller;
        private readonly ILeveledListMerger<T, TGetter, TEntry> _merger;
        private static readonly ModKey Requiem = new ModKey("Requiem", ModType.Plugin);

        public LeveledListMerging(bool mergeEnabled, ILinkCache<ISkyrimMod, ISkyrimModGetter> cache,
            IImmutableSet<ModKey> modsWithRequiemAsMaster,
            ICompactLeveledListUnroller<T, TGetter, TEntry> compactLeveledItemUnroller,
            ILeveledListMerger<T, TGetter, TEntry> merger)
        {
            _mergeEnabled = mergeEnabled;
            _cache = cache;
            _modsEligibleForMerging = modsWithRequiemAsMaster.Add(Requiem);
            _unroller = compactLeveledItemUnroller;
            _merger = merger;
            var masterMap = cache.ListedOrder.Where(m => modsWithRequiemAsMaster.Contains(m.ModKey))
                .Select(m => KeyValuePair.Create(m.ModKey,
                    m.MasterReferences.Where(master => modsWithRequiemAsMaster.Contains(master.Master))
                        .Select(r => r.Master)
                        .ToImmutableSortedSet()))
                .ToImmutableDictionary();
            _invertedMasterMap = masterMap.Keys.Select(mod => KeyValuePair.Create(mod,
                masterMap.Where(e => e.Value.Contains(mod))
                    .Select(e => e.Key).ToImmutableHashSet()
            )).ToImmutableDictionary();
        }

        public override TransformationResult<T, TGetter> Process(
            TransformationResult<T, TGetter> input)
        {
            if (!_mergeEnabled) return input;

            var mergeCandidates = _cache.ResolveAllContexts<T, TGetter>(input.Record().FormKey)
                .Where(x => _modsEligibleForMerging.Contains(x.ModKey)).ToList();
            var toMerge = mergeCandidates.Where(c => c.ModKey == Requiem ||
                mergeCandidates.All(other => _invertedMasterMap[c.ModKey].ContainsNot(other.ModKey))
                ).ToList();
            var baseVersion = toMerge.Find(x => x.ModKey == Requiem)?.Record;

            if (baseVersion == null || toMerge.Count < 3) return input;

            var unrolledBaseEntries = _merger.GetEntries(_unroller.GetUnrolledEntries(baseVersion));

            var updates = toMerge.Where(x => x.ModKey != Requiem)
                .Select(x =>
                    GetDifferences(unrolledBaseEntries, _merger.GetEntries(_unroller.GetUnrolledEntries(x.Record)))
                ).ToImmutableList();

            return input.Modify(record =>
            {
                var additions = updates.SelectMany(cs => cs.Where(c => c.Type == Operation.Addition))
                    .SelectMany(c => Enumerable.Range(1, c.Count).Select(_ => c.Reference)).ToImmutableList();

                var modificationsPerMod =
                    updates.Select(cs => cs.Where(c => c.Type == Operation.Modification).ToImmutableList())
                        .ToImmutableList();
                var modificationsAdd = modificationsPerMod.SelectMany(cs => cs).Distinct()
                    .Where(c => c.Count >= 0 && modificationsPerMod.All(m => m.Contains(c)))
                    .SelectMany(c => Enumerable.Range(1, c.Count).Select(_ => c.Reference)).ToImmutableList();
                var modificationsRemove = modificationsPerMod.SelectMany(cs => cs).Distinct()
                    .Where(c => c.Count < 0 && modificationsPerMod.All(m => m.Contains(c)))
                    .SelectMany(c => Enumerable.Range(1, -c.Count).Select(_ => c.Reference)).ToImmutableList();

                var deletionsPerMod =
                    updates.Select(cs => cs.Where(c => c.Type == Operation.Deletion).ToImmutableList())
                        .ToImmutableList();
                var deletions = deletionsPerMod.SelectMany(cs => cs).Distinct()
                    .Where(c => deletionsPerMod.All(m => m.Contains(c)))
                    .SelectMany(c => Enumerable.Range(1, -c.Count).Select(_ => c.Reference)).ToImmutableList();

                var newEntries = unrolledBaseEntries.Concat(additions).Concat(modificationsAdd).ToImmutableList()
                    .RemoveRange(modificationsRemove.Concat(deletions));

                _merger.SetEntries(record, newEntries);
                Log.Information($"merged leveled lists from {updates.Count} patches");
            });
        }

        private static IImmutableList<Change> GetDifferences(
            IReadOnlyList<LeveledListMerging.LeveledListEntry> baseVersion,
            IReadOnlyList<LeveledListMerging.LeveledListEntry> newVersion)
        {
            var baseEntries = baseVersion
                .GroupBy(x => x, x => x, (k, xs) => KeyValuePair.Create(k, xs.Count())
                ).ToImmutableDictionary();

            var otherEntries = newVersion
                .GroupBy(x => x, x => x, (k, xs) => KeyValuePair.Create(k, xs.Count())
                ).ToImmutableDictionary();

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