ScriptName REQ_AlcoholScript Extends ActiveMagicEffect  

REQ_AlcoholEffectsPlayer Property Controller Auto

GlobalVariable Property AlcoholLevelStorage Auto

Event OnEffectStart(Actor akTarget, Actor akCaster)
	If akTarget == Game.GetPlayer()
		AlcoholLevelStorage.Mod(GetMagnitude())
		Controller.UpdatePlayerAlcoholPenalties()
	EndIf
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	If akTarget == Game.GetPlayer()
		AlcoholLevelStorage.Mod(-GetMagnitude())
		Controller.UpdatePlayerAlcoholPenalties()
	EndIf
EndEvent
