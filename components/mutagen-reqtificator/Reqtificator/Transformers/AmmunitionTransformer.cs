using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator.Transformers
{
    internal class AmmunitionTransformer : Transformer<Ammunition, IAmmunition, IAmmunitionGetter>
    {
        public override bool ShouldProcess(IAmmunitionGetter record)
        {
            var keywordCheck = (!record.Keywords?.Contains(Keywords.AlreadyReqtified) ?? true) &&
                               (!record.Keywords?.Contains(Keywords.NoDamageRescaling) ?? true);
            return keywordCheck && record.Damage > 0.0f;
        }

        public override void Process(IAmmunition record)
        {
            record.Damage *= 4;
            Log.Debug("scaled damage by 4");
        }
    }
}