using Mutagen.Bethesda.Skyrim;
using Serilog;

namespace Reqtificator.Transformers.Weapons
{
    internal class WeaponCriticalDamageScaling : Transformer<Weapon, IWeapon, IWeaponGetter>
    {
        public override TransformationResult<Weapon, IWeaponGetter> Process(
            TransformationResult<Weapon, IWeaponGetter> input)
        {
            if (input.Record().Critical is null || input.Record().Template.IsNotNull())
            {
                return input;
            }
            return input.Modify(r =>
            {
                r.Critical!.Damage = r.BasicStats!.Damage;
                Log.Debug("adjusted weapon critical damage");
            });
        }
    }
}