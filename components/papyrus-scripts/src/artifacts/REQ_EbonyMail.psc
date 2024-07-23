ScriptName REQ_EbonyMail Extends ActiveMagicEffect

Actor Property ChampionOfBoethiah Auto

Spell Property EnchAbility Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	RescaleEnchantment()
	akTarget.AddSpell(EnchAbility, False)
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.RemoveSpell(EnchAbility)
EndEvent


Function RescaleEnchantment()
	If GetTargetActor() == ChampionOfBoethiah
		EnchAbility.SetNthEffectMagnitude(0, 20)
		EnchAbility.SetNthEffectMagnitude(1, 60)
	Else
		Int Murders = Game.QueryStat("Murders")
		EnchAbility.SetNthEffectMagnitude(0, Murders * 0.25)
		EnchAbility.SetNthEffectMagnitude(1, Murders * 0.75)
	EndIf
EndFunction
