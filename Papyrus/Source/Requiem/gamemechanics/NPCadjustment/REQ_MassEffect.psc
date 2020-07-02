Scriptname REQ_MassEffect extends ActiveMagicEffect
{This script dynamically adds armor-dependent mass modifiers to all humanoid NPCs}

REQ_MassEffect_PC Property Data Auto
{player exclusive variant of the mass effect, used as central data storage}
GlobalVariable Property Version_Installed Auto
{the global containing the version stamp of the esp}
GlobalVariable Property Version_Active Auto
{the global containing the version stamp of the savegame}
Spell Property Ability Auto
{the ability that contains the effect with this script (to dispel from player immediately)}

Actor npc
REQ_NPCData npcdata
Bool working = False
Bool shutdown = False
Bool local = False

;local data, used as storage if the data container is not present
Float ME_massmod = 0.0 
Float ME_penalty = 0.0 
Float ME_ratio_light = 1.0 
Float ME_ratio_heavy = 1.0 
Float ME_ratio_onehanded = 1.0 
Float ME_ratio_twohanded = 1.0 
Float ME_ratio_archery = 1.0 

State inactive
	Event OnObjectEquipped(Form akBaseObject, ObjectReference akReference)
	EndEvent

	Event OnObjectUnEquipped(Form akBaseObject, ObjectReference akReference)
	EndEvent
	
	Event Onload()
		If npc.Is3DLoaded()
			While (Version_Active.GetValueInt() != Version_Installed.GetValueInt() )
				Utility.Wait(0.5)
			EndWhile
			GoToState("StartEvaluation")
		Else
			Debug.Trace("[REQ] ERROR: " + npc.GetName() + npc + "triggered Onload() while Is3DLoaded() returns false!")
		EndIf
	EndEvent
	
	Event OnBeginState()
	EndEvent
EndState

Auto State Initialization
	Event OnObjectEquipped(Form akBaseObject, ObjectReference akReference)
	EndEvent

	Event OnObjectUnEquipped(Form akBaseObject, ObjectReference akReference)
	EndEvent
	
	Event OnEffectStart(actor Target, actor Caster)
		npc = Target
		If npc == Game.GetPlayer()
			npc.removeSpell(Ability)
			return
		EndIf
		npcdata = npc as REQ_NPCData
		If npcdata == None
			local = True
		Else
			If npcdata.ME_version != Version_Installed.GetValueInt()
				npcdata.ME_ready = false
			EndIf
			npcdata.ME_version = Version_Installed.GetValueInt()
		EndIf
		If !npc.Is3DLoaded()
			GotoState("inactive")
			return
		EndIf
		While (Version_Active.GetValueInt() != Version_Installed.GetValueInt() )
			Utility.Wait(0.5)
		EndWhile
		If !local && npcdata.ME_ready
			LoadValues()
			GotoState("")
		Else
			GoToState("StartEvaluation")
		EndIf
	EndEvent
	
	Event OnBeginState()
	EndEvent
EndState

Event OnObjectEquipped(Form akBaseObject, ObjectReference akReference)
	If (akBaseObject as Weapon || akBaseObject as Armor)
		GoToState("StartEvaluation")
	EndIf
EndEvent

Event OnObjectUnEquipped(Form akBaseObject, ObjectReference akReference)
	If (akBaseObject as Weapon || akBaseObject as Armor)
		GoToState("StartEvaluation")
	EndIf
EndEvent

Event OnBeginState()
	If !Shutdown && !local
		npcdata.ME_ready = True
	EndIf
EndEvent

Event OnEffectFinish(actor Target, actor Caster)
	shutdown = True
	If npc
		If npc == Game.GetPlayer()
			return
		ElseIf local
			npc.ModAV("mass",-ME_massmod)
			npc.ModAV("infamy",-ME_penalty)
			npc.ModAV("speedmult",ME_penalty*50)
		Else
			npc.ModAV("mass",-npcdata.ME_massmod)
			npc.ModAV("infamy",-npcdata.ME_penalty)
			npc.ModAV("speedmult",npcdata.ME_penalty*50)
		EndIf
	EndIf
EndEvent

State StartEvaluation

	Event OnObjectUnEquipped(Form akBaseObject, ObjectReference akReference)
	EndEvent
	
	Event OnObjectEquipped(Form akBaseObject, ObjectReference akReference)
	EndEvent

	Event OnUpdate()
		If !working && !shutdown
			GoToState("EvaluateArmor")
		ElseIf !shutdown
			RegisterForSingleUpdate(0.25)
		EndIf
	EndEvent
	
	Event OnBeginState()
		if !shutdown
			RegisterForSingleUpdate(0.25)
		EndIf
	EndEvent

EndState

State EvaluateArmor

	Event OnObjectUnEquipped(Form akBaseObject, ObjectReference akReference)
		If (akBaseObject as Weapon || akBaseObject as Armor)
			GoToState("StartEvaluation")
		EndIf
	EndEvent
	
	Event OnObjectEquipped(Form akBaseObject, ObjectReference akReference)
		If (akBaseObject as Weapon || akBaseObject as Armor)
			GoToState("StartEvaluation")
		EndIf
	EndEvent
	
	Event OnBeginState()
		working = True
		If !local
			npcdata.ME_ready = False
		EndIf
		AdjustMass()
		If ( GetState() == "EvaluateArmor" )
			GoToState("")
		EndIf
		working = False
	EndEvent
	
EndState

Function LoadValues()
	;load stored data from REQ_NPCData script
	working = True
	npc.ModAV("mass",npcdata.ME_massmod)
	npc.ModAV("infamy",npcdata.ME_penalty)
	npc.ModAV("speedmult",-npcdata.ME_penalty*50)
	working = False
EndFunction

Function AdjustMass()
	Armor[] slots = new Armor[32]
	Armor nextitem
	weapon armed
	Update_Ratios()
	Int i = 61
	Int type
	Int index = 0
	Float oldmassmod = ME_massmod
	Float oldpenalty = ME_penalty
	Float newmassmod = 0.0
	Float newpenalty = 0.0
	If !local
		ME_ratio_light = npcdata.ME_ratio_light
		ME_ratio_heavy = npcdata.ME_ratio_heavy
		ME_ratio_onehanded = npcdata.ME_ratio_onehanded
		ME_ratio_twohanded = npcdata.ME_ratio_twohanded
		ME_ratio_archery = npcdata.ME_ratio_archery
		oldmassmod = npcdata.ME_massmod
		oldpenalty = npcdata.ME_penalty
	EndIf
	While i >= 30
		nextitem = npc.GetWornForm(Armor.GetMaskForSlot(i)) as Armor
		If nextitem != None && slots.rfind(nextitem,index) == -1 
			slots[index] = nextitem
			If nextitem.GetWeight() > 0.0
				type = nextitem.GetWeightClass()
				newmassmod += nextitem.GetWeight()
				If (type == 1)
					newpenalty += ME_ratio_heavy * nextitem.GetWeight()
				Else
					newpenalty += ME_ratio_light * nextitem.GetWeight()
				EndIf
			EndIf
			index += 1
		Endif
		i -= 1
	EndWhile
	armed = npc.GetEquippedWeapon(False)
	If (armed != None)
		type = npc.GetEquippedItemType(1)
		newmassmod += armed.GetWeight()
		If ((type >= 1 && type <= 4) || type == 8)
			newpenalty += ME_ratio_onehanded * armed.GetWeight()
		ElseIf (type == 5 && type == 6)
			newpenalty += ME_ratio_twohanded * armed.GetWeight()
		Else
			newpenalty += ME_ratio_archery * armed.GetWeight()
		EndIf
	EndIf
	if (type < 5 || type == 8 || armed == None)
		armed = npc.GetEquippedWeapon(True)
		If (armed != None)
			type = npc.GetEquippedItemType(0)
			newmassmod += armed.GetWeight()
			If ((type >= 1 && type <= 4))
				newpenalty += ME_ratio_onehanded * armed.GetWeight()
			ElseIf (type == 5 && type == 6)
				newpenalty += ME_ratio_twohanded * armed.GetWeight()
			Else
				newpenalty += ME_ratio_archery * armed.GetWeight()
			EndIf
		Endif
	Endif
	newmassmod /= 100.0
	newpenalty /= 100.0
	If !shutdown
		If local
			ME_massmod = newmassmod
			ME_penalty = newpenalty
		Else
			npcdata.ME_massmod = newmassmod
			npcdata.ME_penalty = newpenalty
		EndIf
		npc.ModAV("mass",newmassmod-oldmassmod)
		npc.ModAV("infamy",newpenalty-oldpenalty)
		npc.ModAV("speedmult",-(newpenalty-oldpenalty)*50)
	EndIf
EndFunction

Function Update_Ratios()
	Int count = 0	
	ME_ratio_light = 1.0
	While count < Data.Perks_light.Length
		ME_ratio_light -= (npc.HasPerk(Data.Perks_light[count]) as Float) *Data.Factors_light[count]
		count += 1
	EndWhile
	count = 0
	ME_ratio_heavy = 1.0
	While count < Data.Perks_heavy.Length
		ME_ratio_heavy -= (npc.HasPerk(Data.Perks_heavy[count]) as Float) * Data.Factors_heavy[count]
		count += 1
	EndWhile
	count = 0
	ME_ratio_onehanded = 1.0
	While count < Data.Perks_onehanded.Length
		ME_ratio_onehanded -= (npc.HasPerk(Data.Perks_onehanded[count]) as Float) * Data.Factors_onehanded[count]
		count += 1
	EndWhile
	count = 0
	ME_ratio_twohanded = 1.0
	While count < Data.Perks_twohanded.Length
		ME_ratio_twohanded -= (npc.HasPerk(Data.Perks_twohanded[count]) as Float) * Data.Factors_twohanded[count]
		count += 1
	EndWhile
	count = 0
	ME_ratio_archery = 1.0
	While count < Data.Perks_archery.Length
		ME_ratio_archery -= (npc.HasPerk(Data.Perks_archery[count]) as Float) * Data.Factors_archery[count]
		count += 1
	EndWhile
	If !local
		npcdata.ME_ratio_light = ME_ratio_light
		npcdata.ME_ratio_heavy = ME_ratio_heavy
		npcdata.ME_ratio_onehanded = ME_ratio_onehanded
		npcdata.ME_ratio_twohanded = ME_ratio_twohanded
		npcdata.ME_ratio_archery = ME_ratio_archery
	EndIf
EndFunction
