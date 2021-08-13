using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Serilog;

namespace Reqtificator.Transformers.Actors
{
    internal class ActorGlobalPerks : TransformerV2<Npc, INpcGetter>
    {
        private readonly IImmutableSet<IFormLinkGetter<IPerkGetter>> _globalPerks;

        public ActorGlobalPerks(IImmutableSet<IFormLinkGetter<IPerkGetter>> globalPerks)
        {
            _globalPerks = globalPerks;
        }

        public override TransformationResult<Npc, INpcGetter> Process(TransformationResult<Npc, INpcGetter> input)
        {
            if (_globalPerks.Count == 0 ||
                (input.Record().Template.IsNotNull() && input.Record().Configuration
                .TemplateFlags.HasFlag(NpcConfiguration.TemplateFlag.SpellList)))
                return input;

            return input.Modify(record =>
            {
                record.Perks ??= new ExtendedList<PerkPlacement>();
                var presentPerks = record.Perks.Select(p => p.Perk.AsGetter()).ToImmutableHashSet();
                _globalPerks.Where(p => presentPerks.ContainsNot(p))
                    .ForEach(p => record.Perks.Add(new PerkPlacement { Perk = p.AsSetter(), Rank = 1 }));
                Log.Debug("added global perks to actor");
            });
        }
    }
}