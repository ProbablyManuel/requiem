ScriptName REQ_Spellbreaker Extends ActiveMagicEffect

Spell Property EnchAbility Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	RescaleEnchantment()
	akTarget.AddSpell(EnchAbility, False)
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.RemoveSpell(EnchAbility)
EndEvent


Function RescaleEnchantment()
	Int DiseasesContracted = Game.QueryStat("Diseases Contracted")
	EnchAbility.SetNthEffectMagnitude(0, DiseasesContracted * 5)
	EnchAbility.SetNthEffectMagnitude(1, DiseasesContracted * 5)
EndFunction
