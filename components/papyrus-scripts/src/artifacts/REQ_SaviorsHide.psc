ScriptName REQ_SaviorsHide Extends ActiveMagicEffect

ActorBase Property EarthMother Auto
ActorBase Property GiantSlaughterfish Auto
ActorBase Property GiganticMudcrab Auto
ActorBase Property Gorak Auto
ActorBase Property Kruul Auto
ActorBase Property Ragnok Auto
ActorBase Property Snow Auto
ActorBase Property Ulik Auto

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
	Int GreatBeastsSlain = 0
	If GiantSlaughterfish.GetDeadCount() >= 1
		GreatBeastsSlain += 1
	EndIf
	If GiganticMudcrab.GetDeadCount() >= 1
		GreatBeastsSlain += 1
	EndIf
	If Gorak.GetDeadCount() >= 1
		GreatBeastsSlain += 1
	EndIf
	If Kruul.GetDeadCount() >= 1
		GreatBeastsSlain += 1
	EndIf
	If Ragnok.GetDeadCount() >= 1
		GreatBeastsSlain += 1
	EndIf
	If Snow.GetDeadCount() >= 1
		GreatBeastsSlain += 1
	EndIf
	If EarthMother.GetDeadCount() >= 1
		GreatBeastsSlain += 1
	EndIf
	If Ulik.GetDeadCount() >= 1
		GreatBeastsSlain += 1
	EndIf
	Float MagicResist = 10.0 + GreatBeastsSlain * 3.0
	Float PosionResist = 30.0 + GreatBeastsSlain * 9.0
	Float DiseaseResist = 30.0 + GreatBeastsSlain * 9.0
	EnchAbility.SetNthEffectMagnitude(0, MagicResist)
	EnchAbility.SetNthEffectMagnitude(1, PosionResist)
	EnchAbility.SetNthEffectMagnitude(2, DiseaseResist)
EndFunction
