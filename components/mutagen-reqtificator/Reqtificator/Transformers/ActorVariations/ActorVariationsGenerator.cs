using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using System.Text.RegularExpressions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.ActorVariations
{
    internal record VariationKey(IFormLinkGetter<INpcGetter> SkillTemplate,
        IFormLinkGetter<ILeveledNpcGetter> LookTemplate);

    internal static class ActorVariationsGenerator
    {
        private static readonly Regex ActorVariationsPattern = new Regex("^[^_]+_LChar_(?:Variations|VoiceSpawns)_");

        public static IImmutableSet<VariationKey> FindAllActorVariations(IEnumerable<ILeveledNpcGetter> records,
            ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache)
        {
            ImmutableHashSet<VariationKey> ExtractVariations(ILeveledNpcGetter record)
            {
                if (record.Entries is null) return ImmutableHashSet<VariationKey>.Empty;

                var skillsTemplates = record.Entries
                    .Select(r => r.Data!.Reference.TryResolve(linkCache))
                    .Where(r => r is not null && r is INpcGetter)
                    .Select(r => ((INpcGetter)r!).AsLinkGetter())
                    .ToList();
                var lookTemplates = record.Entries
                    .Select(r => r.Data!.Reference.TryResolve(linkCache))
                    .Where(r => r is not null && r is ILeveledNpcGetter)
                    .Select(r => ((ILeveledNpcGetter)r!).AsLinkGetter())
                    .ToList();

                return skillsTemplates.SelectMany(s => lookTemplates.Select(l => new VariationKey(s, l)))
                    .ToImmutableHashSet();
            }

            return records.Where(r => ActorVariationsPattern.IsMatch(r.EditorID ?? ""))
                .Select(ExtractVariations)
                .Aggregate((a, b) => a.Union(b));
        }


        public static (ImmutableList<Npc>, ImmutableDictionary<VariationKey, LeveledNpc>) BuildActorVariationContent(
            ImmutableList<VariationKey> variationsToBuild,
            ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCacheWithPatchedActors,
            ISkyrimMod targetMod
        )
        {
            (ImmutableHashSet<Npc>, LeveledNpc) GenerateVariations(VariationKey key)
            {
                var skillTemplate = key.SkillTemplate.Resolve(linkCacheWithPatchedActors);
                var lookList = key.LookTemplate.Resolve(linkCacheWithPatchedActors);

                var newActors = lookList.Entries!
                    .Select(e => e.Data!.Reference.Resolve<INpcGetter>(linkCacheWithPatchedActors))
                    .Select(lt => ActorCopyTools.MergeVisualAndSkillTemplates(targetMod,
                        $"RTFI_Actor_Variations_{skillTemplate.FormKey}_{lt.FormKey}", skillTemplate, lt))
                    .ToImmutableList();

                var newLeveledList =
                    targetMod.LeveledNpcs.AddNew($"RTFI_LChar_Variations_{skillTemplate.FormKey}_{lookList.FormKey}");
                newLeveledList.DeepCopyIn(lookList, null, null);
                newLeveledList.EditorID = $"RTFI_LChar_Variations_{skillTemplate.FormKey}_{lookList.FormKey}";
                newLeveledList.Entries!.Clear();
                newActors.ForEach(a => newLeveledList.Entries.Add(new LeveledNpcEntry()
                {
                    Data = new LeveledNpcEntryData()
                    {
                        Count = 1,
                        Level = 1,
                        Reference = a.AsLink()
                    }
                }));
                return (newActors.ToImmutableHashSet(), newLeveledList);
            }

            var (actors, variations) = variationsToBuild.Select(v => (v, GenerateVariations(v)))
                .Aggregate((ImmutableHashSet<Npc>.Empty, ImmutableDictionary<VariationKey, LeveledNpc>.Empty),
                    (agg, elem) =>
                        (agg.Item1.Union(elem.Item2.Item1), agg.Item2.Add(elem.v, elem.Item2.Item2)));

            return (actors.ToImmutableList(), variations);
        }

        public static ImmutableList<LeveledNpc> UpdateActorVariationLists(
            ImmutableDictionary<VariationKey, LeveledNpc> variations)
        {
            throw new NotImplementedException();
        }
    }
}