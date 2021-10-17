ScriptName REQ_CursedItem Extends ObjectReference
{Adds the Curse of Agony and makes NPC realize that they're wearing a cursed item.}

Form Property myItem Auto
{The item this script is attached to. Self doesn't work on items in containers.}
Keyword Property REQ_KW_CursedItem Auto
Keyword Property ActorTypeDaedra Auto
Spell Property CurseOfAgony Auto


Event OnEquipped(Actor akActor)
	If (!akActor.HasKeyword(ActorTypeDaedra))
		akActor.AddSpell(CurseOfAgony, False)
		If (akActor != Game.GetPlayer())
			Int Timeout = 10
			While (Timeout && akActor.GetActorValue("Health") > 50)
				Utility.Wait(0.5)
				Timeout = Timeout - 1
			EndWhile
			If (!akActor.IsDead())
				; The NPC realizes he's wearing a cursed item and unequips it
				akActor.UnEquipItem(myItem, True)
			EndIf
		EndIf
	EndIf
EndEvent


Event OnUnequipped(Actor akActor)
	If (!akActor.WornHasKeyword(REQ_KW_CursedItem))
		akActor.RemoveSpell(CurseOfAgony)
	EndIf
EndEvent
