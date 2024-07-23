ScriptName REQ_Volendrung Extends ActiveMagicEffect

Enchantment Property VolendrungEnch Auto

Spell Property EnchAbility Auto

REQ_ReapplyNonPersistentChanges Property ReapplyNonPersistentChanges Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	RescaleEnchantment()
	akTarget.AddSpell(EnchAbility, False)
	ReapplyNonPersistentChanges.VolendrungScript = Self
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.RemoveSpell(EnchAbility)
	ReapplyNonPersistentChanges.VolendrungScript = None
EndEvent


Function ReapplyNonPersistentChanges()
	Actor Target = GetTargetActor()
	Target.RemoveSpell(EnchAbility)
	RescaleEnchantment()
	Target.AddSpell(EnchAbility, False)
EndFunction

Function RescaleEnchantment()
	Float EffectiveBaseHealth = Game.GetPlayer().GetBaseActorValue("Health") - 100
	If EffectiveBaseHealth < 0
		EffectiveBaseHealth = 0
	EndIf
	VolendrungEnch.SetNthEffectMagnitude(0, EffectiveBaseHealth * 0.3)
	VolendrungEnch.SetNthEffectMagnitude(2, EffectiveBaseHealth * 0.3)
	EnchAbility.SetNthEffectMagnitude(0, EffectiveBaseHealth * 0.3)
EndFunction
