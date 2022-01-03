using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.Rules;
using Serilog;

namespace Reqtificator.Transformers.Armors
{
    internal class ArmorKeywordsFromRules : Transformer<Armor, IArmor, IArmorGetter>
    {
        private readonly IImmutableList<AssignmentRule<IArmorGetter, IKeywordGetter>> _rules;

        public ArmorKeywordsFromRules(IEnumerable<AssignmentRule<IArmorGetter, IKeywordGetter>> rules)
        {
            _rules = rules.ToImmutableList();
        }

        public override TransformationResult<Armor, IArmorGetter> Process(
            TransformationResult<Armor, IArmorGetter> input)
        {
            if (input.Record().TemplateArmor.IsNotNull()) return input;

            var assignments = _rules.SelectMany(r => r.GetAssignments(input.Record())).ToList();
            if (assignments.Count > 0)
            {
                return input.Modify(record =>
                {
                    record.Keywords ??= new ExtendedList<IFormLinkGetter<IKeywordGetter>>();
                    assignments.Where(a => record.Keywords.ContainsNot(a.ToAssign))
                        .ForEach(a =>
                        {
                            record.Keywords.Add(a.ToAssign);
                            Log.Debug($"applied keyword '{a.ToAssign.FormKey}' from rule {a.SourceRule}");
                        });
                });
            }

            return input;
        }
    }
}