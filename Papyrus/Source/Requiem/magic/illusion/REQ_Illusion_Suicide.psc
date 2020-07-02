Scriptname REQ_Illusion_Suicide extends activemagiceffect  

MagicEffect Property Dualcast Auto
{Dualcast-Dummyeffect, if present, skip wrist cutting and die instantly}
Idle Property IdleHandCut auto
{Only a form can be used with PlayIdle and we need PlayIdle to know if they cut their wrist}

Event OnEffectStart(Actor Target, Actor Caster)
	Utility.Wait(0.5)
	If (!Target.HasMagicEffect(Dualcast))
		Int i = 1
		While i <= 15 && Target.GetAnimationVariableBool("bAnimationDriven") ; Wait until they are out of any animation
			Utility.Wait(3.0)
			i += 1
		EndWhile
		Target.PlayIdle(IdleHandCut)
		Utility.Wait(0.5)
		If !Target.PlayIdle(IdleHandCut)  ; Don't kill targets that can't or won't cut their wrist
			Return
		EndIf
	EndIf
	Target.SetUnconscious(True)
	Utility.Wait(4.0)
	Target.KillEssential(Target)
	Target.SetUnconscious(False)
EndEvent