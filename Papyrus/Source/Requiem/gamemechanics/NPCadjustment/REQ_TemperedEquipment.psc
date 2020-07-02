Scriptname REQ_TemperedEquipment extends ActiveMagicEffect
{This script will temper the armor of the affected actor when it is first initialized}

Keyword[] Property Keywords Auto
{the keywords that should be tempered}
String[] Property quality Auto
{the quality tier that should be used for the keyword with the same index, as defined in datastorage}
REQ_TemperingData Property datastorage Auto
{the quest alias that contains all relevant data for tempering}
GlobalVariable Property Version_Installed Auto
{the global containing the version stamp of the esp}
GlobalVariable Property Version_Active Auto
{the global containing the version stamp of the savegame}

Event OnEffectStart(actor Target, actor Caster)
	REQ_NPCData npcdata = Target as REQ_NPCData
	If npcdata == None
		Debug.Trace("[REQ] ERROR: Actor " + Target + " has a tempered equipment script, but no NPC_Data container!")
	Else
		int timeout = 10
		While !Target.Is3DLoaded()
			Utility.Wait(0.5)
			timeout = timeout - 1
			If timeout == 0
				return
			EndIf
		EndWhile
		While (Version_Active.GetValueInt() != Version_Installed.GetValueInt() )
			Utility.Wait(0.5)
		EndWhile
		REQ_ClassSpecificEquipment scriptdata = Target as REQ_ClassSpecificEquipment
		While ( scriptdata != None && !npcdata.TE_outfit_added )
			Utility.Wait(0.5)
		EndWhile
		If npcdata.TE_already_tempered == False
			npcdata.TE_already_tempered = True
			ProcessItems(Target)
		EndIf
	EndIf
EndEvent

Function ProcessItems(Actor npc)
	int slotsChecked = 0x0
    int thisSlot = 0x01
	Armor current
    while (thisSlot < 0x80000000)
		current = npc.GetWornForm(thisSlot) as Armor
        If (Math.LogicalAnd(slotsChecked, thisSlot) != thisSlot)	
			if current != None
				TemperItem(npc, thisSlot, current)
				slotsChecked += current.GetSlotMask()
			else
				slotsChecked += thisSlot
			EndIf
		EndIf
		thisslot *= 2
	EndWhile
EndFunction

Function TemperItem(Actor npc, int slotmask, Armor item)
	Int i = 0
	Int j = -1
	Float[] sample
	While i < Keywords.length
		If item.HasKeyword(Keywords[i])
			sample = datastorage.GetRange(quality[i])
			j = Utility.RandomInt(0, sample.length - 1)
			WornObject.SetItemHealthPercent(npc, 1, slotmask , sample[j])
			i = Keywords.length
		EndIf
		i += 1
	EndWhile
EndFunction
