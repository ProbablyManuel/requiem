Scriptname C00JorrvaskrFightNjadaScript extends ReferenceAlias  

; -----

; updated by Enai Siaion

; -----

Event OnHit(ObjectReference akAggressor, Form akSource, Projectile akProjectile, bool abPowerAttack, bool abSneakAttack, bool abBashAttack, bool abHitBlocked)

	If akAggressor == Game.GetPlayer()
		If (akSource as Weapon) || ((akSource as Spell) && (akSource as Spell).IsHostile()) || (akSource as Scroll)
			(GetOwningQuest() as C00JorrvaskrFightSceneScript).PlayerEndedFight = true
			GetOwningQuest().Stop()
		EndIf
	EndIf

EndEvent

; -----