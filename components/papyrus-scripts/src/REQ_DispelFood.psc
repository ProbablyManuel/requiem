scriptname REQ_DispelFood extends ActiveMagicEffect
{Dispels this active effect if the actor is drunk.}

Faction property REQ_Storage_Alcohol auto


Event OnMagicEffectApply(ObjectReference akCaster, MagicEffect akEffect)
	Utility.Wait(1.0) ;give the engine some time to update the drunk level
	if (GetTargetActor().GetFactionRank(REQ_Storage_Alcohol) >= 25)
		Dispel()
	endif
EndEvent
