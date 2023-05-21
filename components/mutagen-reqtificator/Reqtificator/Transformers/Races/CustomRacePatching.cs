using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator.Transformers.Races
{
    internal class CustomRacePatching : Transformer<Race, IRace, IRaceGetter>
    {
        public override TransformationResult<Race, IRaceGetter> Process(TransformationResult<Race, IRaceGetter> input)
        {
            if ((input.Record().Flags & Race.Flag.Playable) == 0)
            {
                return input;
            }

            if (input.Record().Starting.Values.Aggregate(0f, (x, y) => x + y) > 200f)
            {
                return input;
            }

            return input.Modify(record =>
            {
                record.ActorEffect ??= new ExtendedList<IFormLinkGetter<ISpellRecordGetter>>();
                if (record.ActorEffect.ContainsNot(Spells.MassEffectBase))
                {
                    record.ActorEffect.Add(Spells.MassEffectBase);
                }

                if (record.ActorEffect.ContainsNot(Spells.MassEffectNpc))
                {
                    record.ActorEffect.Add(Spells.MassEffectNpc);
                }

                if (record.ActorEffect.ContainsNot(Spells.NoNaturalHealthRegeneration))
                {
                    record.ActorEffect.Add(Spells.NoNaturalHealthRegeneration);
                }

                record.Starting[BasicStat.Health] += 50f;
                record.Starting[BasicStat.Magicka] += 50f;
                record.Starting[BasicStat.Stamina] += 50f;

                record.Regen[BasicStat.Health] = 0.2f;
                record.Regen[BasicStat.Magicka] = 1.1f;
                record.Regen[BasicStat.Stamina] = 1.6f;

                record.BaseCarryWeight = 110f;
                record.UnarmedDamage = 8f;

                Log.Information("patched basic stats for custom race");
            });
        }
    }
}