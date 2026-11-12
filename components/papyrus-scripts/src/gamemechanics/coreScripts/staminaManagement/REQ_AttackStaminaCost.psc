ScriptName REQ_AttackStaminaCost Extends ActiveMagicEffect

Spell Property StaminaCost Auto

Actor Target

Event OnEffectStart(Actor akTarget, Actor akCaster)
	Target = akTarget
	RegisterForSingleUpdate(0.1)
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	GoToState("Shutdown")
	Target.RemoveSpell(StaminaCost)
EndEvent

Event OnUpdate()
	If !Target.GetAnimationVariableBool("IsAttacking")
		GoToState("Shutdown")
		Target.RemoveSpell(StaminaCost)
	Else
		RegisterForSingleUpdate(0.1)
	EndIf
EndEvent


State Shutdown
	
	Event OnEffectFinish(Actor akTarget, Actor akCaster)
	EndEvent

	Event OnUpdate()
	EndEvent

EndState
