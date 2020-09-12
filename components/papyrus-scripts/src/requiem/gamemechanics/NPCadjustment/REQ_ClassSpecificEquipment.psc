Scriptname REQ_ClassSpecificEquipment extends Actor
{this script can be attached to templated actors which require a specific outfit, but inherit their inventory}

Outfit[] Property OutfitList Auto
{real outfits, equipped if actor has the "Switchperk" with same index}
Perk[] Property Switchperks Auto

GlobalVariable WaitTimer
Armor Dummy
Spell Camouflage

Bool Initdone = False
Int index = -1

Event OnLoad()
	WaitTimer = Game.GetFormFromFile(0x0080F0CA, "Requiem.esp") as GlobalVariable
	Dummy = Game.GetFormFromFile(0x0006A0A9, "Skyrim.esm") as Armor
	Camouflage = Game.GetFormFromFile(0x008079FB, "Requiem.esp") as Spell
	; Debug.Trace("Actor " + Self + "has been loaded")
	If Self != None && !Initdone
		RegisterForSingleUpdate(0.1)
		; ArmorUpdate(Self, !Initdone)
	EndIf
EndEvent

Event OnCellAttach()
	; Debug.Trace("Actor " + Self + "has been attached")
	If Initdone && 	!IsDead()
		RegisterForSingleUpdate(0.1)
		; ArmorUpdate(Self, False)
	EndIf
EndEvent

Event OnAttachedToCell()
	; Debug.Trace("Actor " + Self + "has entered the attached cell")
	If Initdone && 	!IsDead()
		RegisterForSingleUpdate(0.1)
		; ArmorUpdate(Self, False)
	EndIf
EndEvent

Event OnUpdate()
	If Is3DLoaded()
		ArmorUpdate(Self, !Initdone)
	Else
		RegisterForSingleUpdate(0.1)
	EndIf
EndEvent

; Event OnUnload()
	; Debug.Trace("Actor " + Self + "has been unloaded")
; EndEvent

; Event OnCellDetach()
	; Debug.Trace("Actor " + Self + "has been detached")
; EndEvent

; Event OnDetachedFromCell()
	; Debug.Trace("Actor " + Self + "has been detached from cell")
; EndEvent

Function ArmorUpdate(Actor target, Bool Init=False)
	; Float waitduration = WaitTimer.GetValue()
	; Debug.Trace("Armor Update for " + target + "started (INIT= " + Init + ")")
	; target.AddSpell(Camouflage)
	; target.EnableAI(False)
	; target.SetGhost(True)
	; Utility.Wait(waitduration)
	; target.SetPlayerTeammate(True)
	; Utility.Wait(waitduration)
	; Int count = 0
	If Init && Outfitlist[0] != None
		While (index+1 < Switchperks.Length && !Initdone)
			index += 1
			If Switchperks[index] == None || target.Hasperk(Switchperks[index])
				; While count < OutfitList[index].GetNumParts()
					; target.Additem(OutfitList[index].GetNthPart(count))
					; count += 1
				; EndWhile
				Target.SetOutfit(OutfitList[index])
				Initdone = True
				(target as REQ_NPCData).TE_outfit_added = True
			EndIf
		EndWhile
	ElseIf Init
		(target as REQ_NPCData).TE_outfit_added = True
		Initdone = True
	EndIf
	; target.AddItem(Dummy, 1)
	; Utility.Wait(waitduration)
	; target.RemoveItem(Dummy, 1)
	; Utility.Wait(waitduration)
	; target.SetPlayerTeammate(False)
	; Utility.Wait(waitduration)
	; target.SetGhost(False)
	; target.EnableAI(True)
	; target.RemoveSpell(Camouflage)
	; target.QueueNiNodeUpdate()
	; Debug.Trace("Armor Update for " + target + "finished!")
EndFunction
