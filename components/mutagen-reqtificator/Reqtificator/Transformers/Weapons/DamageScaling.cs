using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;

namespace Reqtificator.Transformers.Weapons
{
    internal class WeaponDamageScaling : TransformerV2<Weapon, IWeaponGetter>
    {
        public override TransformationResult<Weapon, IWeaponGetter> Process(
            TransformationResult<Weapon, IWeaponGetter> input)
        {
            if (input.Record().HasKeyword(Keywords.AlreadyReqtified) ||
                input.Record().HasKeyword(Keywords.NoDamageRescaling) ||
                input.Record().Data is null ||
                input.Record().BasicStats is null ||
                !input.Record().Template.IsNull)
            {
                return input;
            }

            float? factor = input.Record().Data!.AnimationType switch
            {
                WeaponAnimationType.Bow => 4,
                WeaponAnimationType.Crossbow => 4,
                WeaponAnimationType.OneHandSword => 6,
                WeaponAnimationType.OneHandDagger => 6,
                WeaponAnimationType.OneHandAxe => 6,
                WeaponAnimationType.OneHandMace => 6,
                WeaponAnimationType.TwoHandSword => 6,
                WeaponAnimationType.TwoHandAxe => 6,
                WeaponAnimationType.HandToHand => 6,
                _ => null
            };

            if (factor is null) return input;

            return input.Modify(r => { r.BasicStats!.Damage = (ushort)(r.BasicStats.Damage * factor); });
        }
    }
}