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

    internal class ActorVariationsGenerator
    {
        private static readonly Regex ActorVariationsPattern = new Regex("^[^_]+_LChar_(?:Variations|VoiceSpawns)_");

        public IImmutableSet<VariationKey> FindAllActorVariations(IEnumerable<ILeveledNpcGetter> records, ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache)
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


        public (ImmutableList<Npc>, ImmutableDictionary<VariationKey, LeveledNpc>) BuildActorVariationContent(
            ImmutableList<VariationKey> variationsToBuild, ILinkCache<ISkyrimMod, ISkyrimModGetter> foo
        )
        {
            throw new NotImplementedException();
        }

        public ImmutableList<LeveledNpc> UpdateActorVariationLists(
            ImmutableDictionary<VariationKey, LeveledNpc> variations)
        {
            throw new NotImplementedException();
        }
    }
}