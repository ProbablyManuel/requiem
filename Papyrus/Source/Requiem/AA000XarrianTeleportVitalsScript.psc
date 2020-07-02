Scriptname AA000XarrianTeleportVitalsScript extends activemagiceffect  

Ingredient Property HumansHeart  Auto  
Race Property dremora Auto

Event OnEffectStart(Actor caster, actor target)
	Caster.KillEssential(Target)
	Utility.Wait(0.5)
	If Caster.IsDead() && Caster.GetRace() != dremora
		Target.AddItem(HumansHeart, 1)
	Endif
EndEvent
