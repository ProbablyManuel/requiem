Scriptname Req_MehrunesRazorScript extends activemagiceffect  

Event OnEffectStart(Actor akTarget, Actor akCaster)

	If Utility.RandomInt(0,100) <= 2 && AkTarget.GetActorValue("Health") > 750

		AkTarget.DamageActorValue("Health", 750)

	Endif

	If Utility.RandomInt(0,100) <= 2 && AkTarget.GetActorValue("Health") <= 750

		AkTarget.Kill(AkCaster)

	Endif

EndEvent