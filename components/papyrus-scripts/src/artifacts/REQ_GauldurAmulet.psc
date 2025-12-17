ScriptName REQ_GauldurAmulet Extends ActiveMagicEffect

Spell Property EnchAbility Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	RescaleEnchantment()
	akTarget.AddSpell(EnchAbility, False)
	If akTarget == Game.GetPlayer()
		RegisterForMenu("StatsMenu")
	EndIf
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.RemoveSpell(EnchAbility)
EndEvent

Event OnMenuClose(String Menu)
	Actor Target = GetTargetActor()
	Target.RemoveSpell(EnchAbility)
	RescaleEnchantment()
	Target.AddSpell(EnchAbility, False)
EndEvent

Function RescaleEnchantment()
	Float BaseMagicka = GetTargetActor().GetBaseActorValue("Magicka")
	EnchAbility.SetNthEffectMagnitude(1, BaseMagicka * 0.8)
	EnchAbility.SetNthEffectMagnitude(2, BaseMagicka * 0.05)
	EnchAbility.SetNthEffectMagnitude(3, BaseMagicka * 0.05)
	EnchAbility.SetNthEffectMagnitude(4, BaseMagicka * 0.05)
	EnchAbility.SetNthEffectMagnitude(5, BaseMagicka * 0.05)
	EnchAbility.SetNthEffectMagnitude(6, BaseMagicka * 0.05)
	EnchAbility.SetNthEffectMagnitude(7, BaseMagicka * 0.05)
EndFunction
