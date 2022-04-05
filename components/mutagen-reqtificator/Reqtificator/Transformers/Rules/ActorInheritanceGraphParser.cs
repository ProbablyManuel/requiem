using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Plugins.Exceptions;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Exceptions;

namespace Reqtificator.Transformers.Rules
{
    internal interface IActorInheritanceGraphParser
    {
        public abstract ILinkCache<ISkyrimMod, ISkyrimModGetter> Cache { get; }

        public IEnumerable<IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>> FindAllTemplates(
            INpcGetter record,
            NpcConfiguration.TemplateFlag flagToFollow, params NpcConfiguration.TemplateFlag[] additionalFlagsToFollow);
    }

    internal class ActorInheritanceGraphParser : IActorInheritanceGraphParser
    {
        public ILinkCache<ISkyrimMod, ISkyrimModGetter> Cache { get; }

        public ActorInheritanceGraphParser(ILinkCache<ISkyrimMod, ISkyrimModGetter> cache)
        {
            Cache = cache;
        }

        public IEnumerable<IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>> FindAllTemplates(
            INpcGetter record,
            NpcConfiguration.TemplateFlag flagToFollow, params NpcConfiguration.TemplateFlag[] additionalFlagsToFollow)
        {
            var allFlags = additionalFlagsToFollow.ToImmutableHashSet().Add(flagToFollow);
            return RecurseThroughTemplates(Cache.ResolveContext<INpcSpawn, INpcSpawnGetter>(record.FormKey),
                allFlags.ToImmutableList(),
                ImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>.Empty,
                ImmutableList<(ModKey, INpcSpawnGetter)>.Empty);
        }

        private IEnumerable<IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter>> RecurseThroughTemplates(
            IModContext<ISkyrimMod, ISkyrimModGetter, INpcSpawn, INpcSpawnGetter> contextRecord,
            IImmutableList<NpcConfiguration.TemplateFlag> flagsToFollow,
            IImmutableDictionary<NpcConfiguration.TemplateFlag, INpcGetter> dataProvidedByParents,
            IImmutableList<(ModKey, INpcSpawnGetter)> inheritanceChain)
        {
            if (inheritanceChain.Any(e => e.Item2.FormKey == contextRecord.Record.FormKey))
            {
                throw new CircularInheritanceException(contextRecord.Record, inheritanceChain);
            }

            var updatedInheritanceChain = inheritanceChain.Add((contextRecord.ModKey, contextRecord.Record));

            if (contextRecord.Record is ILeveledNpcGetter lChar)
            {
                //TODO: define a null-handling strategy for such essentially broken records
                foreach (var entry in lChar.Entries!)
                {
                    foreach (var result in RecurseThroughTemplates(
                        GetContext(entry.Data!.Reference.FormKey, updatedInheritanceChain), flagsToFollow,
                        dataProvidedByParents, updatedInheritanceChain))
                    {
                        yield return result;
                    }
                }
            }
            else if (contextRecord.Record is INpcGetter actor)
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
                        GetContext(actor.Template.FormKey, updatedInheritanceChain), flagsToResolve, fromThisRecord,
                        updatedInheritanceChain))
                    {
                        yield return result;
                    }
                }
            }
        }

        private IModContext<ISkyrimMod, ISkyrimModGetter, INpcSpawn, INpcSpawnGetter> GetContext(FormKey formKey,
            IImmutableList<(ModKey, INpcSpawnGetter)> inheritanceChain)
        {
            try
            {
                return Cache.ResolveContext<INpcSpawn, INpcSpawnGetter>(formKey);
            }
            catch (MissingRecordException)
            {
                throw new MissingTemplateException(new FormLinkNullable<INpcSpawnGetter>(formKey), inheritanceChain);
            }
        }
    }
}