ScriptName REQ_NPCData extends Actor
{this passive script is used as a persistent data container for other game mechanics scripts, which are located on magic effects (mostly abilities) to allow easy version updating. Since the quantities are computational expensive, they are stored in this script to quickly recover the idle state after cell changes without recomputing everything.}

;mass effect related variables and functions
Float Property ME_massmod = 0.0 Auto
Float Property ME_penalty = 0.0 Auto
Float Property ME_ratio_light = 1.0 Auto
Float Property ME_ratio_heavy = 1.0 Auto
Float Property ME_ratio_onehanded = 1.0 Auto
Float Property ME_ratio_twohanded = 1.0 Auto
Float Property ME_ratio_archery = 1.0 Auto
Bool Property ME_ready = False Auto
Int Property ME_version = 0 Auto

;armor tempering state
Bool Property TE_already_tempered = False Auto
Bool Property TE_outfit_added = False Auto

Float LastSeen = 0.0 ; records the last time this NPC was around for the purpose of naturally restoring attributes of unloaded NPCs

Event OnLoad()
	If LastSeen
		LastSeen = Utility.GetCurrentGameTime() - LastSeen
		Self.RestoreActorValue("Health", LastSeen * Self.GetActorValue("Health") / Self.GetActorValuePercentage("Health"))
		Self.RestoreActorValue("Stamina", LastSeen * 3 * Self.GetActorValue("Stamina") / Self.GetActorValuePercentage("Stamina"))
		Self.RestoreActorValue("Magicka", LastSeen * 3 * Self.GetActorValue("Magicka") / Self.GetActorValuePercentage("Magicka"))
		LastSeen = 0.0
	EndIf
EndEvent

Event OnUnload()
	Utility.Wait(5.0)
	If Self.IsDead() || (Self.GetActorValuePercentage("Health") >= 1.0 && Self.GetActorValuePercentage("Stamina") >= 0.8 && Self.GetActorValuePercentage("Magicka") >= 0.8)
		LastSeen = 0.0
	Else
		LastSeen = Utility.GetCurrentGameTime()
	EndIf
EndEvent