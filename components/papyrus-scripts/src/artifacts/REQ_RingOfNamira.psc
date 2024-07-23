ScriptName REQ_RingOfNamira Extends ActiveMagicEffect

GlobalVariable Property CorpsesEaten Auto

Spell Property EnchAbility Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	RescaleEnchantment()
	akTarget.AddSpell(EnchAbility, False)
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.RemoveSpell(EnchAbility)
EndEvent


Function RescaleEnchantment()
	EnchAbility.SetNthEffectMagnitude(0, CorpsesEaten.GetValue() * 5.0)
EndFunction
