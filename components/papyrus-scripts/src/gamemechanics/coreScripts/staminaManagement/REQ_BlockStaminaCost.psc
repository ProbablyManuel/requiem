ScriptName REQ_BlockStaminaCost Extends ActiveMagicEffect

Spell Property StaminaCost Auto
Spell Property StaminaCostChecker Auto

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
	Target.AddSpell(StaminaCostChecker, False)
	Target.RemoveSpell(StaminaCostChecker)
	RegisterForSingleUpdate(0.1)
EndEvent


State Shutdown
	
	Event OnEffectFinish(Actor akTarget, Actor akCaster)
	EndEvent

	Event OnUpdate()
	EndEvent

EndState
