Scriptname REQ_ElementalFury extends ActiveMagicEffect 
{applies frost and shock damage based on the effect's magnitude to avoid the issue of multiple object effects not getting scaled magnitudes}

Event OnEffectStart(Actor Target, Actor Caster)
	Float mag = Self.GetMagnitude() / 3.0 ; object effect magnitude correction factor
	Float targetMR = Target.GetActorValue("MagicResist")
	Float firedamage = mag * (1 - Target.GetActorValue("FireResist") / 100) * (1 - targetMR / 100)
	Float frostdamage = mag * (1 - Target.GetActorValue("FrostResist") / 100) * (1 - targetMR / 100)
	Float shockdamage = mag * (1 - Target.GetActorValue("ElectricResist") / 100) * (1 - targetMR / 100)
	Target.DamageActorValue("Health", firedamage)
	Target.DamageActorValue("Health", frostdamage)
	Target.DamageActorValue("Stamina", frostdamage)
	Target.DamageActorValue("Health", shockdamage)
	Target.DamageActorValue("Magicka", shockdamage * 0.5)
EndEvent