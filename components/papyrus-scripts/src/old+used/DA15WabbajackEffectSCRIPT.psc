Scriptname DA15WabbajackEffectSCRIPT extends ActiveMagicEffect  

; Xarrian's additions
Potion property cheese02 auto
Potion property cheese auto
ActorBase property RealDOG auto
ActorBase property RealGoat auto
SPELL property Wabbajacka02 auto
SPELL property Wabbajacka03 auto
SPELL property Wabbajacka04 auto
SPELL property Wabbajacka05 auto
SPELL property Wabbajacka06 auto
SPELL property wabbajacka07 auto


; the gold
MISCOBJECT PROPERTY gold AUTO

; the sweet roll
POTION PROPERTY sweetRoll AUTO
MISCOBJECT PROPERTY plate AUTO

; the heal spell
SPELL PROPERTY healSpell AUTO

; invisibilty spell
SPELL PROPERTY invis AUTO

; the fire/frost/storm spell
EXPLOSION PROPERTY fireSpell AUTO
EXPLOSION PROPERTY frostSpell AUTO
EXPLOSION PROPERTY stormSpell AUTO

; a skeever
ACTORBASE PROPERTY skeever AUTO
ACTORBASE PROPERTY wolf AUTO
ACTORBASE PROPERTY bandit AUTO
ACTORBASE PROPERTY draugr AUTO
ACTORBASE PROPERTY falmer AUTO
ACTORBASE PROPERTY dragonPriest AUTO
ACTORBASE PROPERTY atronach AUTO

; ally
FACTION PROPERTY playerFaction AUTO

; //THE STUFF FOR THE EXPLOSION
EXPLOSION PROPERTY forceExplosion AUTO
EXPLOSION PROPERTY visualExplosion AUTO

OBJECTREFERENCE PROPERTY explosionMarker AUTO
EFFECTSHADER PROPERTY wabbaHitEffect AUTO

SPELL PROPERTY ghostSpell AUTO

INT randEffect=0
ACTORBASE ghostNPC
OBJECTREFERENCE npcChanged
OBJECTREFERENCE newCreature

KEYWORD PROPERTY keyWabbajackExcluded Auto
KEYWORD PROPERTY keyGiant AUTO
KEYWORD PROPERTY keyDragon AUTO

; //TRANSMOGRIFY VARIABLES
OBJECTREFERENCE objStore
OBJECTREFERENCE newRefStore
ACTORBASE PROPERTY chicken AUTO
ACTORBASE PROPERTY mudcrab AUTO
ACTORBASE PROPERTY rabbit AUTO
ACTORBASE PROPERTY dremora AUTO
dunTransmogrifyAnimal mainScript

; //WE madwoman
ACTORBASE PROPERTY madWoman AUTO

; //////////
; //ASH PILE VARIABLES
; //////////
float property fDelay = 0.75 auto
{time to wait before Spawning Ash Pile}
float property fDelayEnd = 1.65 auto
{time to wait before Removing Base Actor}
float property ShaderDuration = 0.00 auto
{Duration of Effect Shader.}
Activator property pDefaultAshPileGhost auto
{The object we use as a pile.}
Bool property bSetAlphaZero = True auto
{The Effect Shader we want.}
FormList Property pDisintegrationMainImmunityList auto
{If the target is in this list, they will not be disintegrated.}
EFFECTSHADER PROPERTY pGhostDeathFXShader AUTO
{the shader to play while dying}

race VictimRace
ACTOR victim
bool TargetIsImmune = True

EVENT onEffectStart(Actor akTarget, Actor akCaster)

	victim = akTarget

	IF(akCaster == game.getPlayer())
	
		randEffect = utility.randomInt(1, 100)
		explosionMarker.moveTo(akTarget)
		
		IF(akTarget.isEssential())
			randEffect = 1
		ELSEIF(akTarget.hasKeyword(keyWabbajackExcluded))
			randEffect = 1
		; Xarrian removed
		; ELSEIF(akTarget.hasKeyword(keyGiant))
		;	randEffect = 1
		ELSEIF(akTarget.hasKeyword(keyDragon))
			randEffect = 1
		ELSEIF(akTarget.isCommandedActor())
			randEffect = 1
		ELSEIF(akTarget.getBaseObject() == madwoman)
			; Xarrian left this to 60 but the madwoman must be polymorphed into a rabbit
			; randEffect = 60
			randEffect = 70
		
		; Xarrian adds the following explicit settings to random effects on guards ?	
		elseIf akTarget.isGuard() && randEffect <= 33
			randEffect = 1
		elseIf akTarget.isGuard() && randEffect > 33 && randEffect < 50
			randEffect = 40
		elseIf akTarget.isGuard() && randEffect > 49 && randEffect < 66
			randEffect = 99
		elseIf akTarget.isGuard() && randEffect > 65 && randEffect < 90
			randEffect = 35
		elseIf akTarget.isGuard() && randEffect > 89
			randEffect = 98
		; end Xarrian addition
			
		ENDIF

		; Xarrian replaces the following effects with his own randomness
		; /////////////
		; //CHOOSE WHAT WE'RE RANDOMLY DOING
		; IF(randEffect >= 1 && randEffect < 51)
		; 	elementalEffect(akTarget)
		; 
		; ELSEIF(randEffect >= 51 && randEffect < 58)
		; 	randomCreatureChicken(akTarget)			
		; 	
		; ELSEIF(randEffect >= 58 && randEffect < 65)
		; 	randomCreatureRabbit(akTarget)	
		; 	
		; ELSEIF(randEffect >= 65 && randEffect < 72)
		; 	randomCreatureMudcrab(akTarget)	
		; 
		; ELSEIF(randEffect >= 72 && randEffect < 77)
		; 	castInvis(akTarget)
		; 
		; ELSEIF(randEffect >= 77 && randEffect < 82)
		;	hsmTransfer(akTarget)
		;	
		; ELSEIF(randEffect >= 82 && randEffect < 89)
		;	randomCreatureDremora(akTarget)
		;
		;ELSEIF(randEffect >= 89 && randEffect < 94)
		;	healSpell.cast(akTarget, akTarget)
		;	
		;ELSEIF(randEffect >= 94 && randEffect < 96)
		;	spawnGold(akTarget)
		;		
		;ELSEIF(randEffect >= 96 && randEffect < 98)
		;	spawnSweetRoll(akTarget)
		;	
		;ELSEIF(randEffect >= 98)
		;	akTarget.kill(game.getPlayer())
		;
		;ENDIF
		
		; Xarrian additions
		if randEffect >= 1 && randEffect < 30
			elementalEffect(akTarget)
		elseIf randEffect >= 30 && randEffect < 33
			akTarget.placeAtMe(frostSpell)
			wabbajacka07.cast(akTarget, akTarget)
		elseIf randEffect >= 33 && randEffect < 34
			akTarget.placeAtMe(frostSpell)
			wabbajacka07.cast(akTarget, akTarget)
		elseIf randEffect >= 34 && randEffect < 35 && weather.GetSkyMode() != 0 && weather.GetSkyMode() != 1 && weather.GetSkyMode() != 2
			Wabbajacka06.cast(akTarget, akTarget)
			akTarget.UnEquipAll()
			explosionMarker.setPosition(akTarget.x, akTarget.y, (akTarget.z + 800))
			explosionMarker.placeAtMe(cheese)
			explosionMarker.placeAtMe(cheese02)
			explosionMarker.placeAtMe(sweetRoll)
			explosionMarker.placeAtMe(forceExplosion)
		elseIf randEffect >= 34 && randEffect < 35 && weather.GetSkyMode() != 3
			Wabbajacka06.cast(akTarget, akTarget)
			akTarget.UnEquipAll()
		elseIf randEffect >= 35 && randEffect < 37
			akTarget.placeAtMe(fireSpell)
			akTarget.placeAtMe(RealDOG)
			akTarget.placeAtMe(RealDOG)
			akTarget.placeAtMe(RealDOG)
		elseIf randEffect >= 37 && randEffect < 38
			akTarget.placeAtMe(fireSpell)
			akTarget.placeAtMe(RealGoat)
			akTarget.placeAtMe(RealGoat)
			akTarget.placeAtMe(RealGoat)
			akTarget.placeAtMe(RealGoat)
			akTarget.placeAtMe(RealGoat)
		elseIf randEffect >= 38 && randEffect < 43
			Wabbajacka06.cast(akTarget, akTarget)
			akTarget.UnEquipAll()
		elseIf randEffect >= 43 && randEffect < 48
			Wabbajacka05.cast(akTarget, akTarget)
		elseIf randEffect >= 48 && randEffect < 53
			akTarget.placeAtMe(fireSpell)
			Wabbajacka04.cast(akTarget, akTarget)
		elseIf randEffect >= 53 && randEffect < 58
			Wabbajacka02.cast(akTarget, akTarget)
		elseIf randEffect >= 58 && randEffect < 63
			Wabbajacka03.cast(akTarget, akTarget)
		elseIf randEffect >= 63 && randEffect < 68
			randomCreatureChicken(akTarget)
		elseIf randEffect >= 68 && randEffect < 73
			randomCreatureRabbit(akTarget)
		elseIf randEffect >= 73 && randEffect < 78
			randomCreatureMudcrab(akTarget)
		elseIf randEffect >= 78 && randEffect < 83
			castInvis(akTarget)
		elseIf randEffect >= 83 && randEffect < 88
			hsmTransfer(akTarget)
		elseIf randEffect >= 88 && randEffect < 93
			randomCreatureDremora(akTarget)
		elseIf randEffect >= 93 && randEffect < 98
			healSpell.cast(akTarget, akTarget)
		elseIf randEffect >= 98 && randEffect < 99
			spawnGold(akTarget)
		elseIf randEffect >= 99 && randEffect < 100
			spawnSweetRoll(akTarget)
		elseIf randEffect >= 100
			akTarget.kill(game.getPlayer())
		endIf
		; Xarrian additions end

		
		
		
	ENDIF

ENDEVENT

FUNCTION basicDamage(ACTOR targ)

ENDFUNCTION

FUNCTION randomCreatureChicken(ACTOR targ)

	targ.disable()
			
	newRefStore = targ.placeAtMe(chicken)
	
	; //setting the master script to be the one with the stored vars
	mainScript = newRefstore AS dunTransmogrifyAnimal
	mainScript.storedActor = targ
	
	targ.stopCombat()
	
ENDFUNCTION

FUNCTION randomCreatureMudcrab(ACTOR targ)

	targ.disable()
			
	newRefStore = targ.placeAtMe(mudcrab)
	
	; //setting the master script to be the one with the stored vars
	mainScript = newRefstore AS dunTransmogrifyAnimal
	mainScript.storedActor = targ
	
	targ.stopCombat()
	
ENDFUNCTION

FUNCTION randomCreatureRabbit(ACTOR targ)

	targ.disable()
			
	newRefStore = targ.placeAtMe(rabbit)
	
	; //setting the master script to be the one with the stored vars
	mainScript = newRefstore AS dunTransmogrifyAnimal
	mainScript.storedActor = targ
	
	targ.stopCombat()
	
ENDFUNCTION

FUNCTION randomCreatureDremora(ACTOR targ)

	targ.disable()
			
	newRefStore = targ.placeAtMe(dremora)
	
	; //setting the master script to be the one with the stored vars
	mainScript = newRefstore AS dunTransmogrifyAnimal
	mainScript.storedActor = targ
	
	targ.stopCombat()
	
ENDFUNCTION

FUNCTION spawnGhostFriend(ACTOR targ)
	
	targ.placeAtMe(visualExplosion)
	
	ACTOR tempNPC = (targ.placeAtMe(targ.getBaseObject()) as ACTOR)
	tempNPC.addSpell(ghostSpell)
	tempNPC.removeFromAllFactions()
	tempNPC.setFactionRank(playerFaction, 5)
	tempNPC.setGhost(TRUE)
			
	utility.wait(10)
	
	explosionMarker.setPosition(tempNPC.x, tempNPC.y, (tempNPC.z + 75))
	tempNPC.placeAtMe(visualExplosion)
	
	utility.wait(0.5)
	
	tempNPC.disable()

ENDFUNCTION

FUNCTION elementalEffect(ACTOR targ)
	
	int rand = utility.randomInt(1,3)
	explosionMarker.setPosition(targ.x, targ.y, (targ.z + 75))
		
	IF(rand == 1)
		targ.placeAtMe(fireSpell)
		; Xarrian changes damage health to 120
		;targ.damageAV("Health", 60)
		targ.damageAV("Health", 120)
	
	ELSEIF(rand == 2)
		targ.placeAtMe(frostSpell)
		; Xarrian changes damage health to 120
		;targ.damageAV("Health", 60)
		targ.damageAV("Health", 120)
	
	ELSEIF(rand == 3)
		targ.placeAtMe(stormSpell)
		; Xarrian changes damage health to 120
		;targ.damageAV("Health", 60)
		targ.damageAV("Health", 120)
	
	ENDIF

ENDFUNCTION

FUNCTION castInvis(ACTOR targ)

	float baseMagicka = targ.getAV("magicka")
	float baseIllusion = targ.getAV("Illusion")
	
	targ.setAV("magicka", 200)
	targ.setAV("Illusion", 200)
	invis.cast(targ, targ)
	
	utility.wait(0.5)
	
	targ.setAV("magicka", baseMagicka)
	targ.setAV("Illusion", baseIllusion)
		
ENDFUNCTION

FUNCTION hsmTransfer(ACTOR targ)
	
	wabbaHitEffect.play(game.getPlayer())
	
	targ.damageAV("health", (targ.getBaseAV("health") * 0.5))
	game.getPlayer().restoreAV("health", (game.getPlayer().getBaseAV("health") * 0.5))

	targ.damageAV("stamina", (targ.getBaseAV("stamina") * 0.5))
	game.getPlayer().restoreAV("stamina", (game.getPlayer().getBaseAV("stamina") * 0.5))

	targ.damageAV("magicka", (targ.getBaseAV("magicka") * 0.5))
	game.getPlayer().restoreAV("magicka", (game.getPlayer().getBaseAV("magicka") * 0.5))
	
	utility.wait(1.5)
	
	wabbaHitEffect.stop(game.getPlayer())

ENDFUNCTION

FUNCTION spawnGold(ACTOR targ)
	; Xarrian removed
	; targ.placeAtMe(visualExplosion)
	
	createAshPile()	
	explosionMarker.setPosition(targ.x, targ.y, (targ.z + 100))

	utility.wait(0.1)
	
	; Xarrian set gold to 150
	; explosionMarker.placeAtMe(gold, 50)
	explosionMarker.placeAtMe(gold, 150)
	
	explosionMarker.placeAtMe(forceExplosion)

ENDFUNCTION

FUNCTION spawnSweetRoll(ACTOR targ)
	; Xarrian removed
	; targ.placeAtMe(visualExplosion)
	
	utility.wait(0.7)
	explosionMarker.setPosition(targ.x, targ.y, (targ.z + 10))
	utility.wait(0.1)
	createAshPile()
	explosionMarker.placeAtMe(sweetRoll)


ENDFUNCTION

FUNCTION createAshPile()

	; //check to see if the target is in the immunity list
	IF(pDisintegrationMainImmunityList == none)
		TargetIsImmune = False
	ELSE
		ActorBase VictimBase = victim.GetBaseObject() as ActorBase
		VictimRace = VictimBase.GetRace()
		
		IF(pDisintegrationMainImmunityList.hasform(VictimRace) || pDisintegrationMainImmunityList.hasform(VictimBase))
			TargetIsImmune = True
		ELSE
			TargetIsImmune = False
		ENDIF
	ENDIF

	; //if the target is not immune, disintegrate them
	IF(TargetIsImmune == False)
; 		debug.trace("victim just died")

		
		victim.kill(game.getPlayer())
		victim.SetCriticalStage(victim.CritStage_DisintegrateStart)

		IF(pGhostDeathFXShader != none)
			pGhostDeathFXShader.play(victim,ShaderDuration)
		ENDIF
		
		victim.SetAlpha (0.0,True)
		
		; //attach the ash pile
		victim.AttachAshPile(pDefaultAshPileGhost)
		
		utility.wait(fDelayEnd)
		IF(pGhostDeathFXShader != none)
			pGhostDeathFXShader.stop(victim)
		ENDIF
		IF(bSetAlphaZero == True)
			victim.SetAlpha (0.0,True)
		ENDIF
		victim.SetCriticalStage(victim.CritStage_DisintegrateEnd)
	ENDIF
	
endFUNCTION
