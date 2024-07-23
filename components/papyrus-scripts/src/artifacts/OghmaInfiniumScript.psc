ScriptName OghmaInfiniumScript Extends ObjectReference


Quest Property DA04 Auto
Message Property ChoiceMessage Auto
Int Property Advancement Auto
Bool Property HasBeenRead Auto
Book Property MySelf Auto


Event OnEquipped(Actor akActor)
	If akActor == Game.GetPlayer()
		ReadOghmaInfinium(False)
	EndIf
EndEvent


Event OnActivate(ObjectReference akActionRef)
	If akActionRef == Game.GetPlayer() && !IsActivationBlocked()
		ReadOghmaInfinium(True)
	EndIf
EndEvent


Function ReadOghmaInfinium(Bool FromWorld)
	GlobalVariable HasBeenReadGV = Game.GetFormFromFile(0x010009DE, "Update.esm") As GlobalVariable
	If DA04.GetStageDone(200) && HasBeenReadGV.GetValue() == 0.0
		HasBeenReadGV.SetValue(1.0)
		Game.AddPerkPoints(7)
		If FromWorld
			Game.GetPlayer().RemoveItem(MySelf)
		Else
			Delete()
		EndIf
		Utility.WaitMenuMode(2.0)
		ChoiceMessage.Show()
	EndIf
EndFunction
