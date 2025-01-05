ScriptName REQ_AlcoholScript Extends ActiveMagicEffect  

REQ_AlcoholEffectsPlayer Property Controller Auto

GlobalVariable Property AlcoholLevelStorage Auto

Float Mag

Event OnEffectStart(Actor akTarget, Actor akCaster)
	If akTarget == Game.GetPlayer()
		Mag = GetMagnitude()
		AlcoholLevelStorage.Mod(Mag)
		Controller.UpdatePlayerAlcoholPenalties()
	EndIf
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	If akTarget == Game.GetPlayer()
		AlcoholLevelStorage.Mod(-Mag)
		Controller.UpdatePlayerAlcoholPenalties()
	EndIf
EndEvent
