using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Serilog;

namespace Reqtificator.Transformers
{
    public class AmmunitionTransformer: Transformer<Ammunition, IAmmunition, IAmmunitionGetter>
    {

        public override bool ShouldProcess(IAmmunitionGetter record)
        {
            var keywordCheck = (!record.Keywords?.Contains(StaticReferences.Keywords.AlreadyReqtified) ?? true) &&
                               (!record.Keywords?.Contains(StaticReferences.Keywords.NoDamageRescaling) ?? true);
            return keywordCheck && record.Damage > 0.0f;
        }

        public override void Process(IAmmunition record)
        {
            record.Damage *= 4;
            Log.Debug($"{record.EditorID} scaled damage by 4");
        }
    }
}