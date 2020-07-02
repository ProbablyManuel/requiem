Scriptname AA000XarrianEnchantNihilScript extends activemagiceffect  


Event OnEffectStart(Actor akTarget, Actor akCaster)

	If Utility.RandomInt(0,100) == 1 && AkTarget.GetActorValue("Health") > 250

		AkTarget.DamageActorValue("Health", 250)

	Endif

	If Utility.RandomInt(0,100) == 1 && AkTarget.GetActorValue("Health") <= 250

		AkTarget.Kill(AkCaster)

	Endif

EndEvent