using System.Collections.Generic;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator.Transformers.Armors
{
    internal class ArmorRatingScaling : TransformerV2<Armor, IArmorGetter>
    {
        private readonly Dictionary<IFormLinkGetter<IKeywordGetter>, float> _lightArmorThresholds;
        private readonly Dictionary<IFormLinkGetter<IKeywordGetter>, float> _heavyArmorThresholds;

        public ArmorRatingScaling(ArmorPatchingConfiguration armorConfiguration)
        {
            _lightArmorThresholds = new Dictionary<IFormLinkGetter<IKeywordGetter>, float>
            {
                {Keywords.ArmorBody, armorConfiguration.LightArmorRatingThresholds.Body},
                {Keywords.ArmorFeet, armorConfiguration.LightArmorRatingThresholds.Feet},
                {Keywords.ArmorHands, armorConfiguration.LightArmorRatingThresholds.Hands},
                {Keywords.ArmorHead, armorConfiguration.LightArmorRatingThresholds.Head},
                {Keywords.ArmorShield, armorConfiguration.LightArmorRatingThresholds.Shield}
            };
            _heavyArmorThresholds = new Dictionary<IFormLinkGetter<IKeywordGetter>, float>
            {
                {Keywords.ArmorBody, armorConfiguration.HeavyArmorRatingThresholds.Body},
                {Keywords.ArmorFeet, armorConfiguration.HeavyArmorRatingThresholds.Feet},
                {Keywords.ArmorHands, armorConfiguration.HeavyArmorRatingThresholds.Hands},
                {Keywords.ArmorHead, armorConfiguration.HeavyArmorRatingThresholds.Head},
                {Keywords.ArmorShield, armorConfiguration.HeavyArmorRatingThresholds.Shield}
            };
        }

        public override TransformationResult<Armor, IArmorGetter> Process(
            TransformationResult<Armor, IArmorGetter> input)
        {
            if ((input.Record().Keywords?.Contains(Keywords.AlreadyReqtified.FormKey) ?? false) ||
                (input.Record().Keywords?.Contains(Keywords.NoArmorRatingRescaling.FormKey) ?? false) ||
                !input.Record().TemplateArmor.IsNull
                ) return input;

            var armorType = input.Record().BodyTemplate?.ArmorType;
            var partKeyword = GetArmorPartKeyword(input.Record());
            float? threshold = (armorType, partKeyword) switch
            {
                (null, _) => null,
                (_, null) => null,
                (ArmorType.Clothing, _) => null,
                (ArmorType.HeavyArmor, var keyword) => _heavyArmorThresholds[keyword],
                (ArmorType.LightArmor, var keyword) => _lightArmorThresholds[keyword],
                _ => null
            };

            if (threshold is not null && input.Record().ArmorRating < threshold)
            {
                var offset = Equals(partKeyword, Keywords.ArmorBody) ? 66.0f : 18.0f;
                var factor = Equals(armorType, ArmorType.HeavyArmor) ? 6.6f : 3.3f;
                return input.Modify(record =>
                {
                    record.ArmorRating = record.ArmorRating * factor + offset;
                    Log.Debug("adjusted armor rating");
                });
            }

            return input;
        }

        private IFormLinkGetter<IKeywordGetter>? GetArmorPartKeyword(IArmorGetter record)
        {
            return _lightArmorThresholds.Keys.FirstOrDefault(x => record.Keywords?.Contains(x) ?? false);
        }
    }
}