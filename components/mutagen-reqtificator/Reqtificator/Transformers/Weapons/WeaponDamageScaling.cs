using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator.Transformers.Weapons
{
    internal class WeaponDamageScaling : Transformer<Weapon, IWeapon, IWeaponGetter>
    {
        public override TransformationResult<Weapon, IWeaponGetter> Process(
            TransformationResult<Weapon, IWeaponGetter> input)
        {
            if (input.Record().HasKeyword(Keywords.AlreadyReqtified) ||
                input.Record().HasKeyword(Keywords.NoDamageRescaling) ||
                input.Record().Data is null ||
                input.Record().BasicStats is null ||
                input.Record().Template.IsNotNull())
            {
                return input;
            }

            float? factor = input.Record().Data!.AnimationType switch
            {
                WeaponAnimationType.Bow => 4,
                WeaponAnimationType.Crossbow => 4,
                WeaponAnimationType.HandToHand => 6,
                WeaponAnimationType.OneHandSword => 6,
                WeaponAnimationType.OneHandDagger => 6,
                WeaponAnimationType.OneHandAxe => 6,
                WeaponAnimationType.OneHandMace => 6,
                WeaponAnimationType.TwoHandSword => 6,
                WeaponAnimationType.TwoHandAxe => 6,
                _ => null
            };

            if (factor is null)
            {
                return input;
            }

            return input.Modify(r =>
            {
                r.BasicStats!.Damage = (ushort)(r.BasicStats.Damage * factor);
                Log.Debug("adjusted weapon damage");
            });
        }
    }
}