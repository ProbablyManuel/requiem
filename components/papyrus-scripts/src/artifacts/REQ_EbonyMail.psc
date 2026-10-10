ScriptName REQ_EbonyMail Extends ActiveMagicEffect

Actor Property ChampionOfBoethiah Auto

Spell Property EnchAbility Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	RescaleEnchantment()
	akTarget.AddSpell(EnchAbility, False)
	RegisterForUpdate(300)
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.RemoveSpell(EnchAbility)
EndEvent

Event OnUpdate()
	Actor Target = GetTargetActor()
	If !Target.IsInCombat()
		Target.RemoveSpell(EnchAbility)
		RescaleEnchantment()
		Target.AddSpell(EnchAbility)
	EndIf
EndEvent


Function RescaleEnchantment()
	If GetTargetActor() == ChampionOfBoethiah
		EnchAbility.SetNthEffectMagnitude(0, 20)
		EnchAbility.SetNthEffectMagnitude(1, 60)
	Else
		Int Murders = Game.QueryStat("Murders")
		Float MagicResist = Min(10.0 + Murders * 0.25, 30.0)
		Float FireResist = Min(30.0 + Murders * 0.75, 90.0)
		EnchAbility.SetNthEffectMagnitude(0, MagicResist)
		EnchAbility.SetNthEffectMagnitude(1, FireResist)
	EndIf
EndFunction

Float Function Min(Float a, Float b)
	If a < b
		Return a
	Else
		Return b
	EndIf
EndFunction
