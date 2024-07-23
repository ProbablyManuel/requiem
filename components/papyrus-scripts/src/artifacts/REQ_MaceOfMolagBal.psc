ScriptName REQ_MaceOfMolagBal Extends ActiveMagicEffect

Enchantment Property MaceOfMolagBalEnch Auto

GlobalVariable Property SoulTrapDuration Auto

Spell Property Description Auto

REQ_ReapplyNonPersistentChanges Property ReapplyNonPersistentChanges Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	RescaleEnchantment()
	akTarget.AddSpell(Description, False)
	ReapplyNonPersistentChanges.MaceOfMolagBalScript = Self
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.RemoveSpell(Description)
	ReapplyNonPersistentChanges.MaceOfMolagBalScript = None
EndEvent


Function ReapplyNonPersistentChanges()
	Actor Target = GetTargetActor()
	Target.RemoveSpell(Description)
	RescaleEnchantment()
	Target.AddSpell(Description, False)
EndFunction

Function RescaleEnchantment()
	Int SoulsTrapped = Game.QueryStat("Souls Trapped")
	MaceOfMolagBalEnch.SetNthEffectMagnitude(0, SoulsTrapped * 0.1)
	MaceOfMolagBalEnch.SetNthEffectMagnitude(1, SoulsTrapped * 0.1)
	MaceOfMolagBalEnch.SetNthEffectDuration(2, SoulsTrapped / 100)
	Description.SetNthEffectMagnitude(0, SoulsTrapped * 0.1)
	SoulTrapDuration.SetValueInt(SoulsTrapped / 100)
EndFunction
