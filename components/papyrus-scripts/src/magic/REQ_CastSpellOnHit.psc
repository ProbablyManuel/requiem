ScriptName REQ_CastSpellOnHit Extends ActiveMagicEffect
{Cast a spell when this actor enters combat}

Float Property Cooldown Auto

Spell Property SpellToCast Auto


Event OnHit(ObjectReference akAggressor, Form akSource, Projectile akProjectile, bool abPowerAttack, bool abSneakAttack, bool abBashAttack, bool abHitBlocked)
	If akAggressor != None && akSource As Weapon != None
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
