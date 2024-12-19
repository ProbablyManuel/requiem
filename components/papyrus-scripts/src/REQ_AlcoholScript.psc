ScriptName REQ_AlcoholScript Extends ActiveMagicEffect  

REQ_AlcoholEffectsPlayer Property Controller Auto

GlobalVariable Property AlcoholLevelStorage Auto

Int mag

Event OnEffectStart(Actor akTarget, Actor akCaster)
	If akTarget == Game.GetPlayer()
        mag = self.GetMagnitude() as Int
		AlcoholLevelStorage.Mod(mag)
		Controller.UpdatePlayerAlcoholPenalties()
	EndIf
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	If akTarget == Game.GetPlayer()
		AlcoholLevelStorage.Mod(-mag)
		Controller.UpdatePlayerAlcoholPenalties()
	EndIf
EndEvent
