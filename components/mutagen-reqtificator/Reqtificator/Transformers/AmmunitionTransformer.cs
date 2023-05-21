using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator.Transformers
{
    internal class AmmunitionTransformer : Transformer<Ammunition, IAmmunition, IAmmunitionGetter>
    {
        public override TransformationResult<Ammunition, IAmmunitionGetter> Process(
            TransformationResult<Ammunition, IAmmunitionGetter> input)
        {
            var record = input.Record();
            bool exceptionKeywordsFound = (record.Keywords?.Contains(Keywords.AlreadyReqtified.FormKey) ?? false) ||
                   (record.Keywords?.Contains(Keywords.NoDamageRescaling.FormKey) ?? false);
            if (exceptionKeywordsFound || record.Damage <= 0.0f) { return input; }

            var result = input.Modify(record => record.Damage *= 4);
            Log.Debug("scaled damage by 4");
            return result;
        }
    }
}