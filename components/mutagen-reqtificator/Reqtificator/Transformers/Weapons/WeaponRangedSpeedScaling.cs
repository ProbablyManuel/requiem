using System;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.StaticReferences;

namespace Reqtificator.Transformers.Weapons
{
    internal class WeaponRangedSpeedScaling : TransformerV2<Weapon, IWeaponGetter>
    {
        public override TransformationResult<Weapon, IWeaponGetter> Process(
            TransformationResult<Weapon, IWeaponGetter> input)
        {
            if (input.Record().HasKeyword(Keywords.AlreadyReqtified) ||
                input.Record().HasKeyword(Keywords.NoWeaponSpeedRescaling) ||
                input.Record().Data is null ||
                input.Record().Data!.Flags.HasFlag(WeaponData.Flag.NonPlayable) ||
                input.Record().Template.IsNotNull())
            {
                return input;
            }

            return input.Record().Data!.AnimationType switch
            {
                WeaponAnimationType.Bow => input.Modify(PatchRecord(0.3704f, Keywords.WeaponBowHeavy)),
                WeaponAnimationType.Crossbow => input.Modify(PatchRecord(0.4445f, Keywords.WeaponCrossbowHeavy)),
                _ => input
            };
        }

        private static Action<Weapon> PatchRecord(float speed, IFormLinkGetter<IKeywordGetter> keyword)
        {
            return record =>
            {
                record.Data!.Speed = speed;
                record.Keywords ??= new ExtendedList<IFormLinkGetter<IKeywordGetter>>();
                if (record.Keywords.ContainsNot(keyword)) record.Keywords.Add(keyword);
            };
        }
    }
}