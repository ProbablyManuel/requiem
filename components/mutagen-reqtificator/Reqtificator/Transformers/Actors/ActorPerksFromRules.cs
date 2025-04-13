using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.Rules;
using Serilog;

namespace Reqtificator.Transformers.Actors
{
    internal class ActorPerksFromRules : Transformer<Npc, INpc, INpcGetter>
    {
        private readonly IImmutableList<AssignmentRule<INpcGetter, IPerkGetter>> _rules;


        public ActorPerksFromRules(IImmutableList<AssignmentRule<INpcGetter, IPerkGetter>> rules)
        {
            _rules = rules;
        }

        public override TransformationResult<Npc, INpcGetter> Process(TransformationResult<Npc, INpcGetter> input)
        {
            if (input.Record().Configuration.TemplateFlags.HasFlag(NpcConfiguration.TemplateFlag.SpellList))
            {
                return input;
            }

            var assignments = _rules.SelectMany(r => r.GetAssignments(input.Record())).ToList();
            if (assignments.Count > 0)
            {
                return input.Modify(record =>
                {
                    record.Perks ??= [];
                    assignments.Where(a => record.Perks.Select(p => p.Perk).ContainsNot(a.ToAssign))
                        .ForEach(a =>
                        {
                            record.Perks.Add(new PerkPlacement { Perk = a.ToAssign.AsSetter(), Rank = 1 });
                            Log.Debug($"applied perk '{a.ToAssign.FormKey}' from rule {a.SourceRule}");
                        });
                });
            }

            return input;
        }
    }
}