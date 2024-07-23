ScriptName REQ_KynesToken_KynesPeace Extends ActiveMagicEffect

Faction Property KynesPeaceFaction Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	akTarget.AddToFaction(KynesPeaceFaction)
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.RemoveFromFaction(KynesPeaceFaction)
EndEvent
