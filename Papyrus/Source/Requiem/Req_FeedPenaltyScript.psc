Scriptname Req_FeedPenaltyScript extends activemagiceffect  

SPELL Property Penalty01  Auto  
SPELL Property Penalty02  Auto  

MagicEffect Property Req_FeedPenaltyEffect01  Auto  

Event OnEffectStart(actor aktarget, actor akcaster)

	If aktarget.hasMagicEffect(Req_FeedPenaltyEffect01) == False
		Utility.Wait(3)

			If aktarget.GetActorValue("Health") <= 125
				aktarget.KillEssential()
			EndIf

		Penalty01.cast(aktarget, aktarget)

	ElseIf aktarget.hasMagicEffect(Req_FeedPenaltyEffect01) == True
		Utility.Wait(3)

			If aktarget.GetActorValue("Health") <= 125
				aktarget.KillEssential()
			EndIf

		Penalty02.cast(aktarget, aktarget)
	Endif

EndEvent

