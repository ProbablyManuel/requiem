Scriptname AA000XarrianLockBreakScriptNormal extends ObjectReference  

PROJECTILE Property OpenLockProjectile  Auto  
{legacy property, still present to suppress "property no longer existing" warnings}

Event OnHit(ObjectReference akAggressor, Form akSource, Projectile akProjectile, bool abPowerAttack, bool abSneakAttack, bool abBashAttack, bool abHitBlocked)
	Actor player = Game.GetPlayer()
	Float power = 2 * player.GetActorValue("Health") + player.GetActorValue("Stamina")
	Int lock = Self.GetLockLevel()
	; only player-executed melee attacks qualify as lockbreaking attacks
	If (akAggressor == player && akProjectile == None && !abBashAttack)
		If (lock <= 1 && power >= 350.0) || (lock <= 25 && power >= 450.0) || (lock <= 50 && power >= 550.0)
			Self.Lock(false)
		EndIf
	EndIf
EndEvent

