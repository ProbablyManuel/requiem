using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using System.Text.RegularExpressions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Exceptions;
using Reqtificator.Transformers.Rules;

namespace Reqtificator.Transformers.ActorVariations
{
    internal record VariationKey(IFormLinkGetter<INpcGetter> SkillTemplate,
        IFormLinkGetter<ILeveledNpcGetter> LookTemplate);

    internal static class ActorVariationsGenerator
    {
        private static readonly Regex ActorVariationsPattern = new("^[^_]+_LChar_(?:Variations|VoiceSpawns)_");

        private static bool IsActorVariation(ILeveledNpcGetter record, IImmutableSet<ModKey> modsWithActorVariations)
        {
            return modsWithActorVariations.Contains(record.FormKey.ModKey) &&
                   ActorVariationsPattern.IsMatch(record.EditorID ?? "");
        }

        public static IImmutableSet<VariationKey> FindAllActorVariations(IEnumerable<ILeveledNpcGetter> records,
            ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache,
            IImmutableSet<ModKey> modsWithActorVariations)
        {
            return records.Where(r => IsActorVariation(r, modsWithActorVariations))
                .Select(r => ExtractVariations(r, linkCache).Keys.ToImmutableHashSet())
                .Aggregate((a, b) => a.Union(b));
        }


        public static (ImmutableList<Npc>, ImmutableDictionary<VariationKey, LeveledNpc>) BuildActorVariationContent(
            ImmutableList<VariationKey> variationsToBuild,
            ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCacheWithPatchedActors,
            ISkyrimMod targetMod
        )
        {
            var inheritanceGraph = new ActorInheritanceGraphParser(linkCacheWithPatchedActors);

            var uniqueActorsToBuild = variationsToBuild.SelectMany(v =>
            {
                var lookList = v.LookTemplate.Resolve(linkCacheWithPatchedActors);
                return lookList.Entries!.Select(e => (v.SkillTemplate, e.Data!.Reference));
            }).ToHashSet();

            var newActors = uniqueActorsToBuild
                .Select(e =>
                {
                    var skillTemplate = e.SkillTemplate.Resolve(linkCacheWithPatchedActors);
                    var lookTemplate = e.Reference.Resolve<INpcGetter>(linkCacheWithPatchedActors);
                    var newActor = ActorCopyTools.MergeVisualAndSkillTemplates(targetMod,
                        $"RTFI_Actor_Variations_{skillTemplate.FormKey}_{lookTemplate.FormKey}", skillTemplate,
                        lookTemplate,
                        inheritanceGraph);
                    return KeyValuePair.Create((e.SkillTemplate.FormKey, e.Reference.FormKey), newActor);
                })
                .ToImmutableDictionary();


            LeveledNpc GenerateVariations(VariationKey key)
            {
                var lookList = key.LookTemplate.Resolve(linkCacheWithPatchedActors);

                var newLeveledList =
                    targetMod.LeveledNpcs.AddNew($"RTFI_LChar_Variations_{key.SkillTemplate.FormKey}_{lookList.FormKey}");
                newLeveledList.DeepCopyIn(lookList, null, null);
                newLeveledList.EditorID = $"RTFI_LChar_Variations_{key.SkillTemplate.FormKey}_{lookList.FormKey}";
                newLeveledList.Entries!.Clear();
                lookList.Entries!
                    .Select(e => newActors[(key.SkillTemplate.FormKey, e.Data!.Reference.FormKey)])
                    .ForEach(a => newLeveledList.Entries.Add(new LeveledNpcEntry()
                    {
                        Data = new LeveledNpcEntryData()
                        {
                            Count = 1,
                            Level = 1,
                            Reference = a.ToLink()
                        }
                    }));
                return newLeveledList;
            }

            return ([.. newActors.Values], variationsToBuild.Select(v => KeyValuePair.Create(v, GenerateVariations(v))).ToImmutableDictionary());
        }

        [System.Diagnostics.CodeAnalysis.SuppressMessage("Style", "IDE0305:Simplify collection initialization", Justification = "Not simpler")]
        public static ImmutableList<LeveledNpc> UpdateActorVariationLists(
            IEnumerable<ILeveledNpcGetter> records,
            ImmutableDictionary<VariationKey, LeveledNpc> variations,
            ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache,
            IImmutableSet<ModKey> modsWithActorVariations)
        {
            return records.Where(r => IsActorVariation(r, modsWithActorVariations))
                .Select(r =>
                {
                    var updated = r.DeepCopy();
                    var variationsToAdd = ExtractVariations(r, linkCache);
                    if (variationsToAdd.Values.Sum() > 255)
                    {
                        throw new OversizedLeveledListException(r.FormKey, r.EditorID);
                    }

                    updated.Entries!.Clear();
                    variationsToAdd.ForEach(v =>
                    {
                        var newEntry = new LeveledNpcEntry()
                        {
                            Data = new LeveledNpcEntryData()
                            {
                                Count = 1,
                                Level = 1,
                                Reference = variations[v.Key].ToLink()
                            }
                        };
                        updated.Entries.AddRange(Enumerable.Repeat(newEntry, v.Value));
                    });
                    return updated;
                }).ToImmutableList();
        }

        private static ImmutableDictionary<VariationKey, int> ExtractVariations(ILeveledNpcGetter record,
            ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache)
        {
            if (record.Entries is null)
            {
                return ImmutableDictionary<VariationKey, int>.Empty;
            }

            var skillsTemplates = record.Entries
                .Select(r => (r, r.Data!.Reference.TryResolve(linkCache)))
                .Where(r => r.Item2 is not null and INpcGetter)
                .Select(r => (((INpcGetter)r.Item2!).ToLinkGetter(), r.r.Data!.Level))
                .ToList();
            var lookTemplates = record.Entries
                .Select(r => (r, r.Data!.Reference.TryResolve(linkCache)))
                .Where(r => r.Item2 is not null and ILeveledNpcGetter)
                .Select(r => (((ILeveledNpcGetter)r.Item2!).ToLinkGetter(), r.r.Data!.Count))
                .ToList();

            return skillsTemplates.SelectMany(s => lookTemplates.Select(l =>
                    KeyValuePair.Create(new VariationKey(s.Item1, l.Item1), s.Level * l.Count)))
                .ToImmutableDictionary();
        }
    }
}