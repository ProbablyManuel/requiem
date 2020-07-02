Scriptname AA000Xarrianalterationtelegripscript extends activemagiceffect  

Event OnEffectStart(actor Target, actor Caster)

If target.IsInInterior() == 0

	header.moveto(target)
	header.PushActorAway(Target, 75)
	utility.wait(0.5)
	target.killessential(caster)

ElseIf target.IsInInterior() == 1

	header.moveto(target)
	header.PushActorAway(Target, 50)
	utility.wait(0.5)
	target.setactorvalue("Health", 0)
	target.killessential(caster)


EndIf


EndEvent

int Property PushForce  Auto  

ObjectReference Property header  Auto  
