ScriptName REQ_CastSpellOnCombatBegin Extends ActiveMagicEffect
{Cast a spell when this actor enters combat}

Float Property Cooldown Auto

Spell Property SpellToCast Auto


Event OnCombatStateChanged(Actor akTarget, Int aiCombatState)
	If aiCombatState == 1
		If Cooldown > 0.0
			GoToState("Cooldown")
			RegisterForSingleUpdateGameTime(Cooldown)
		EndIf
		SpellToCast.Cast(GetTargetActor())
	EndIf
EndEvent

Event OnUpdateGameTime()
	GoToState("")
EndEvent

State Cooldown
	Event OnCombatStateChanged(Actor akTarget, Int aiCombatState)
	EndEvent
EndState
