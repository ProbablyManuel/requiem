using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;

namespace Reqtificator.Transformers.Weapons
{
    internal class WeaponNpcAmmunitionUsage : Transformer<Weapon, IWeapon, IWeaponGetter>
    {
        private static readonly WeaponAnimationType[] RangedWeaponTypes =
        {
            WeaponAnimationType.Bow,
            WeaponAnimationType.Crossbow
        };

        public override TransformationResult<Weapon, IWeaponGetter> Process(
            TransformationResult<Weapon, IWeaponGetter> input)
        {
            if (input.Record().HasKeyword(Keywords.AlreadyReqtified) ||
                input.Record().Data is null ||
                input.Record().Data!.Flags.HasFlag(WeaponData.Flag.NonPlayable) ||
                input.Record().Data!.Flags.HasFlag(WeaponData.Flag.NPCsUseAmmo) ||
                RangedWeaponTypes.ContainsNot(input.Record().Data!.AnimationType) ||
                input.Record().Template.IsNotNull())
            {
                return input;
            }

            return input.Modify(r => r.Data!.Flags |= WeaponData.Flag.NPCsUseAmmo);
        }
    }
}