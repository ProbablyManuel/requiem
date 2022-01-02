using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator.Transformers.Weapons
{
    internal class WeaponMeleeRangeScaling : Transformer<Weapon, IWeapon, IWeaponGetter>
    {
        private static readonly WeaponAnimationType[] MeleeWeaponTypes =
        {
            WeaponAnimationType.HandToHand,
            WeaponAnimationType.OneHandSword,
            WeaponAnimationType.OneHandDagger,
            WeaponAnimationType.OneHandAxe,
            WeaponAnimationType.OneHandMace,
            WeaponAnimationType.TwoHandSword,
            WeaponAnimationType.TwoHandAxe
        };

        public override TransformationResult<Weapon, IWeaponGetter> Process(TransformationResult<Weapon, IWeaponGetter> input)
        {
            if (input.Record().HasKeyword(Keywords.AlreadyReqtified) ||
                input.Record().HasKeyword(Keywords.NoWeaponReachRescaling) ||
                input.Record().Data is null ||
                input.Record().Template.IsNotNull() ||
                MeleeWeaponTypes.ContainsNot(input.Record().Data!.AnimationType))
            {
                return input;
            }

            return input.Modify(r =>
            {
                r.Data!.Reach *= 0.7f;
                Log.Debug("scaled down melee weapon range");
            });
        }
    }
}