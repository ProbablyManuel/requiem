using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Configuration;
using Reqtificator.StaticReferences;

namespace Reqtificator.Transformers.Actors
{
    internal class PlayerChanges : TransformerV2<Npc, INpcGetter>
    {
        private readonly PlayerConfig _config;

        public PlayerChanges(PlayerConfig config)
        {
            _config = config;
        }

        public override TransformationResult<Npc, INpcGetter> Process(TransformationResult<Npc, INpcGetter> input)
        {
            if (input.Record().FormKey != Npcs.Player.FormKey) return input;

            return input.Modify(record =>
            {
                record.Configuration.HealthOffset = (short)_config.HealthOffset;
                record.Configuration.MagickaOffset = (short)_config.MagickaOffset;
                record.Configuration.StaminaOffset = (short)_config.StaminaOffset;
                _config.SpellsToRemove.ForEach(s => record.ActorEffect?.Remove(s));
            });
        }
    }
}