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
    internal class ActorSpellsFromRules : Transformer<Npc, INpc, INpcGetter>
    {
        private readonly IImmutableList<AssignmentRule<INpcGetter, ISpellGetter>> _rules;


        public ActorSpellsFromRules(IImmutableList<AssignmentRule<INpcGetter, ISpellGetter>> rules)
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
                    record.ActorEffect ??= new ExtendedList<IFormLinkGetter<ISpellRecordGetter>>();
                    assignments.Where(a => record.ActorEffect.ContainsNot(a.ToAssign))
                        .ForEach(a =>
                        {
                            record.ActorEffect.Add(a.ToAssign.AsSetter());
                            Log.Debug($"applied spell '{a.ToAssign.FormKey}' from rule {a.SourceRule}");
                        });
                });
            }

            return input;
        }
    }
}