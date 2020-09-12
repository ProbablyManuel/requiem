Scriptname VampireSunISMDEffectScript extends ActiveMagicEffect  

imageSpaceModifier Property VampireSunlightISMD01  Auto
imageSpaceModifier Property VampireSunlightISMD02  Auto 
imageSpaceModifier Property VampireSunlightISMD03  Auto 
imageSpaceModifier Property VampireSunlightISMD04  Auto 
Message Property VampireSunMessage Auto

int property VampirismLevel = 1 auto

event OnEffectStart(Actor akTarget, Actor akCaster)
	if aktarget == game.getPlayer()
		VampireSunMessage.Show()
		VampireSunlightISMD04.applyCrossFade(2.0)
		MagVampireSunlight.Play(Game.GetPlayer())
		utility.wait(2.0)
	;	imageSpaceModifier.removeCrossFade()     ; Edited by Xarrian

;		if VampirismLevel == 4
;			VampireSunlightISMD04.applyCrossFade(2.0)
;		elseif VampirismLevel == 3
;			VampireSunlightISMD03.applyCrossFade(2.0)
;		elseif VampirismLevel == 2
;			VampireSunlightISMD02.applyCrossFade(2.0)
;		else	;aka level 1
;			VampireSunlightISMD01.applyCrossFade(2.0)
;		endif
	endif
endEvent


;event OnEffectStop(Actor akTarget, Actor akCaster)
;	if aktarget == game.getPlayer()
;		imageSpaceModifier.removeCrossFade(2.0)
;	endif
;endEvent

Sound Property MagVampireSunlight  Auto  


Event OnEffectStop(Actor akTarget, Actor akCaster) ; Edited by Xarrian, ff

imageSpaceModifier.removeCrossFade()  

EndEvent 

Event OnEffectFinish(Actor akTarget, Actor akCaster) ; Edited by Xarrian, ff

imageSpaceModifier.removeCrossFade()  

EndEvent 



