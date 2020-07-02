Scriptname ReanimateAshPile extends ActiveMagicEffect  

{Scripted effect for on death ash pile}

import debug
import FormList

;======================================================================================;
;  PROPERTIES  /
;=============/

float property fDelay = 0.75 auto
									{time to wait before Spawning Ash Pile}
float property fDelayEnd = 1.65 auto
									{time to wait before Removing Base Actor}
float property ShaderDuration = 0.00 auto
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
Keyword Property ActorTypeDaedra  Auto  

Keyword Property ActorTypeFamiliar  Auto  

bool Property AshPileCreated  Auto  


;======================================================================================;
;  VARIABLES   /
;=============/


actor Victim
race VictimRace
bool TargetIsImmune = False

;=============
;   FUNCTIONS /
;=============

bool function IsSummoned()
	if Victim.HasKeyword(ActorTypeFamiliar) || Victim.HasKeyword(ActorTypeDaedra)
		return true
; ;		debug.trace("ReanimateAshPile: IsSummoned True")
	else
		return false
; ;		debug.trace("ReanimateAshPile: IsSummoned False")
	endIf
endFunction

function TurnToAsh()
		;trace("victim just died")
		victim.SetCriticalStage(Victim.CritStage_DisintegrateStart)
;		victim.SetAlpha (0.99,False)
		;USKP 2.0.3 - Victim needs to have 3D loaded.
		if( Victim.Is3DLoaded() )
			if	MagicEffectShader != none
				MagicEffectShader.play(Victim,ShaderDuration)
			endif
			if bSetAlphaToZeroEarly
				victim.SetAlpha (0.0,True)
			endif
		EndIf
		utility.wait(fDelay)     
		Victim.AttachAshPile(AshPileObject)
;		AshPileRef = AshPileObject
;		AshPileRef.SetAngle(0.0,0.0,(Victim.GetAngleZ()))
		utility.wait(fDelayEnd)
		;USKP 2.0.3 - Victim needs to have 3D loaded.
		if( Victim.Is3DLoaded() )
			if	MagicEffectShader != none
				MagicEffectShader.stop(Victim)
			endif
			if bSetAlphaZero == True
				victim.SetAlpha (0.0,True)
			EndIf
		EndIf
		victim.SetCriticalStage(Victim.CritStage_DisintegrateEnd)

endFunction

;======================================================================================;
;   EVENTS     /
;=============/

Event OnEffectStart(Actor Target, Actor Caster)
	victim = target
; 	debug.trace("ReanimateAshpile: victim == " + victim + ", is this right?")

	if Victim.IsCommandedActor() == True && IsSummoned() == False
		TargetIsImmune = False
	else
		TargetIsImmune = True
	endIf

EndEvent


Event OnDying(Actor Killer)

; ;	debug.trace("ReanimateAshPile TargetIsImmune:" + TargetIsImmune)

	if TargetIsImmune == False && AshPileCreated == False
		TurnToAsh()
		AshPileCreated = True
	endif
	
EndEvent

Event OnEffectFinish(Actor Target, Actor Caster)

	if TargetIsImmune == False && AshPileCreated == False
		While Target.IsCommandedActor()	; RRO-179 - Don't turn them to ash before the effect is really finished
			Utility.Wait(4.0)
		EndWhile
		TurnToAsh()
		AshPileCreated = True
	endif

EndEvent