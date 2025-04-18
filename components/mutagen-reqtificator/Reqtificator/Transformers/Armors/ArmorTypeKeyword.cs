using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.StaticReferences;
using Serilog;
using ArmorTransformationResult = Reqtificator.Transformers.TransformationResult<Mutagen.Bethesda.Skyrim.Armor, Mutagen.Bethesda.Skyrim.IArmorGetter>;

namespace Reqtificator.Transformers.Armors
{
    internal class ArmorTypeKeyword : Transformer<Armor, IArmor, IArmorGetter>
    {
        public override ArmorTransformationResult Process(ArmorTransformationResult input)
        {
            if (input.Record().Keywords is null ||
                !input.Record().Keywords!.Contains(Keywords.ArmorShield.FormKey) ||
                input.Record().Keywords!.Contains(Keywords.AlreadyReqtified.FormKey) ||
                input.Record().Keywords!.Contains(Keywords.NoArmorTypeKeyword.FormKey) ||
                !input.Record().TemplateArmor.IsNull
            )
            {
                return input;
            }
            return input.Record().BodyTemplate?.ArmorType switch
            {
                ArmorType.HeavyArmor => AddKeywordIfMissing(input, Keywords.ArmorHeavy.FormKey),
                ArmorType.LightArmor => AddKeywordIfMissing(input, Keywords.ArmorLight.FormKey),
                _ => input
            };
        }

        private static ArmorTransformationResult AddKeywordIfMissing(ArmorTransformationResult input, FormKey keyword)
        {
            if (input.Record().Keywords?.ContainsNot(keyword) ?? true)
            {
                return input.Modify(armor =>
                {
                    armor.Keywords ??= [];
                    armor.Keywords.Add(keyword);
                    Log.Debug("added missing armor type keyword");
                });
            }

            return input;
        }
    }
}