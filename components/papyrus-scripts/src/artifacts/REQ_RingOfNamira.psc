ScriptName REQ_RingOfNamira Extends ActiveMagicEffect

GlobalVariable Property CorpsesEaten Auto

Spell Property EnchAbility Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	RescaleEnchantment()
	akTarget.AddSpell(EnchAbility, False)
	RegisterForModEvent("Requiem_CannibalFeed", "OnCannibalFeed")
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.RemoveSpell(EnchAbility)
EndEvent

Event OnCannibalFeed(Form akTarget, Form akCannibal)
	Actor Target = GetTargetActor()
	If akCannibal == Target
		Target.RemoveSpell(EnchAbility)
		RescaleEnchantment()
		Target.AddSpell(EnchAbility, False)
	EndIf
EndEvent

Function RescaleEnchantment()
	Float ReflectDamage = Min(CorpsesEaten.GetValue() * 5.0, 500.0)
	EnchAbility.SetNthEffectMagnitude(0, ReflectDamage)
EndFunction

Float Function Min(Float a, Float b)
	If a < b
		Return a
	Else
		Return b
	EndIf
EndFunction
