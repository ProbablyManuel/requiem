Scriptname MQ104DragonScript extends ReferenceAlias  
{ideally same script can be used for both MQ104A and B -- need to keep quest stages synched}

DragonActorScript dragonScript


Event OnCombatStateChanged(Actor akTarget,int aeCombatState)
	if aeCombatState != 0 ; 0 = not in combat, so non-zero means we entered combat
		(GetReference() As Actor).DamageActorValue("Health", 2000.0)
		GetOwningQuest().setstage(65)
	endIf
endEvent

Event OnDeath(Actor akKiller)
; 	debug.trace(self + " OnDeath")
	GetOwningQuest().setstage(80)
	dragonScript = GetRef() as DragonActorScript
; 	debug.trace(self + " OnDeath: dragonscript=" + dragonscript)
	RegisterForSingleUpdate(1)
endEvent

Event OnUpdate()
	if GetOwningQuest().GetStageDone(80) == 1 && GetOwningQuest().GetStageDone(85) == 0
		if GetRef().GetDistance(Game.GetPlayer()) < dragonScript.deathFXrange
			UnregisterForUpdate()
			GetOwningQuest().SetStage(85)
		endif
	endif
	if !GetOwningQuest().GetStageDone(85)
		RegisterForSingleUpdate(1)
	endif
endEvent

