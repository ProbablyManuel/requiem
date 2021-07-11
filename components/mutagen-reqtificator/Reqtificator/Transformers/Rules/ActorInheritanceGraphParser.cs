using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.Rules
{
    public class ActorInheritanceGraphParser
    {
        private readonly ILinkCache<ISkyrimMod, ISkyrimModGetter> _cache;

        public ActorInheritanceGraphParser(ILinkCache<ISkyrimMod, ISkyrimModGetter> cache)
        {
            _cache = cache;
        }

        public IEnumerable<IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>> FindAllTemplates(
            INpcGetter record,
            NpcConfiguration.TemplateFlag flagToFollow, params NpcConfiguration.TemplateFlag[] additionalFlagsToFollow)
        {
            var allFlags = additionalFlagsToFollow.ToImmutableHashSet().Add(flagToFollow);
            return RecurseThroughTemplates(record, allFlags.ToImmutableList(),
                ImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>.Empty);
        }

        private IEnumerable<IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>> RecurseThroughTemplates(
            INpcSpawnGetter record,
            IImmutableList<NpcConfiguration.TemplateFlag> flagsToFollow,
            IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter> dataProvidedByParents)
        {
            if (record is ILeveledNpcGetter lChar)
            {
                //TODO: define a null-handling strategy for such essentially broken records
                foreach (var entry in lChar.Entries!)
                {
                    foreach (var result in RecurseThroughTemplates(
                        _cache.Resolve<INpcSpawnGetter>(entry.Data!.Reference.FormKey), flagsToFollow,
                        dataProvidedByParents))
                    {
                        yield return result;
                    }
                }
            }
            else if (record is INpcGetter actor)
            {
                if (actor.Template.IsNull)
                {
                    yield return flagsToFollow.ToImmutableDictionary(f => f, _ => actor)
                        .AddRange(dataProvidedByParents);
                }
                else
                {
                    var flagsToResolve = flagsToFollow.Where(f => actor.Configuration.TemplateFlags.HasFlag(f))
                        .ToImmutableList();
                    var fromThisRecord = flagsToFollow.Where(f => flagsToResolve.ContainsNot(f)).ToImmutableDictionary(
                        f => f, f => actor).AddRange(dataProvidedByParents);
                    foreach (var result in RecurseThroughTemplates(
                        _cache.Resolve<INpcSpawnGetter>(actor.Template.FormKey),
                        flagsToResolve, fromThisRecord))
                    {
                        yield return result;
                    }
                }
            }
        }
    }
}