Scriptname AA000XarrianAlterationTransAnimScript extends ActiveMagicEffect  

import debug
import FormList

;======================================================================================;
;  PROPERTIES  /
;=============/

float property fDelay = 0.00 auto
									{time to wait before Spawning Ash Pile}
; float property fDelayAlpha = 0.0 auto
; 									{time to wait before Setting alpha to zero.}
float property fDelayEnd = 0.2 auto
									{time to wait before Removing Base Actor}
float property ShaderDuration = 0.15 auto
									{Duration of Effect Shader.}
Activator property AshPileObject auto
									{The object we use as a pile.}
EffectShader property MagicEffectShader auto
									{The Effect Shader we want.}
Bool property bSetAlphaZero = True auto
									{The Effect Shader we want.}
FormList Property ImmunityList auto
									{If the target is in this list, they will not be disintegrated.}
Bool property bSetAlphaToZeroEarly = False Auto
									{Use this if we want to set the acro to invisible somewhere before the effect shader is done.}

;======================================================================================;
;  VARIABLES   /
;=============/


actor Victim
race VictimRace
bool TargetIsImmune = True
; bool DeadAlready = FALSE

;======================================================================================;
;   EVENTS     /
;=============/

Event OnEffectStart(Actor Target, Actor Caster)
	victim = target
	; DeadAlready = Victim.IsDead()
	trace("victim == " + victim + ", is this right?")
	target.additem(ectoplasm, 3)
EndEvent


Event OnDying(Actor Killer)

	if ImmunityList == none
		TargetIsImmune = False
	else
		ActorBase VictimBase = Victim.GetBaseObject() as ActorBase
		VictimRace = VictimBase.GetRace()
		
		if ImmunityList.hasform(VictimRace)
			TargetIsImmune = True
		elseif ImmunityList.hasform(VictimBase)
			TargetIsImmune = True
		else
			TargetIsImmune = False
		endif
	endif

	if TargetIsImmune == False
		trace("victim just died")
		; DeadAlready = true
		victim.SetCriticalStage(Victim.CritStage_DisintegrateStart)
		;victim.SetAlpha (0.99,False)
		if	MagicEffectShader != none
			MagicEffectShader.play(Victim,ShaderDuration)
		endif
		if bSetAlphaToZeroEarly
			victim.SetAlpha (0.0,True)
		endif
		utility.wait(fDelay)     
		Victim.AttachAshPile(AshPileObject)
		; AshPileRef = AshPileObject
		; AshPileRef.SetAngle(0.0,0.0,(Victim.GetAngleZ()))
		utility.wait(fDelayEnd)
		if	MagicEffectShader != none
			MagicEffectShader.stop(Victim)
		endif
		if bSetAlphaZero == True
			victim.SetAlpha (0.0,True)
		endif
		victim.SetCriticalStage(Victim.CritStage_DisintegrateEnd)
	endif
	
EndEvent
Ingredient Property Ectoplasm  Auto  
