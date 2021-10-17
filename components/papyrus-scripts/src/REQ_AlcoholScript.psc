Scriptname REQ_AlcoholScript extends ActiveMagicEffect  
{Keeps track of the current drunk level and applies the visual drunk effect to the player.}

REQ_AlcoholEffectsPlayer Property controller Auto
Faction Property Storage Auto
Int mag

Event OnEffectStart(Actor akTarget, Actor akCaster)
	mag = self.GetMagnitude() as Int
	If mag <= 0
		mag = 1
	EndIf
	akTarget.AddToFaction(Storage)
	If akTarget.GetFactionRank(Storage) + mag < 127
		akTarget.ModFactionRank(Storage, mag)
	Else
		akTarget.SetFactionRank(Storage, 127)
	EndIf

	If (akTarget == Game.GetPlayer())
	    controller.UpdatePlayerAlcoholVisionPenalties()
    EndIf
EndEvent


Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.ModFactionRank(Storage, -mag)
	If (akTarget.GetFactionRank(Storage) <= 0)
		akTarget.RemoveFromFaction(Storage)
	EndIf

    If (akTarget == Game.GetPlayer())
        controller.UpdatePlayerAlcoholVisionPenalties()
    EndIf
EndEvent