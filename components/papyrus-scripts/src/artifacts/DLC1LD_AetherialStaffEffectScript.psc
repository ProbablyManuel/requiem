Scriptname DLC1LD_AetherialStaffEffectScript extends ObjectReference
{Script on the marker 'summoned' by the Aetherial Staff. Determines what is actually summoned for each use.}

Faction Property DLC1LD_AetherialStaffBusyFaction Auto

Spell Property SummonSphereSpell Auto


Event OnInit()
	Actor Player = Game.GetPlayer()
	If !Player.IsInFaction(DLC1LD_AetherialStaffBusyFaction)
		Player.AddToFaction(DLC1LD_AetherialStaffBusyFaction)
		SummonSphereSpell.Cast(Player, Self)
	EndIf
	Disable()
	Delete()
EndEvent
