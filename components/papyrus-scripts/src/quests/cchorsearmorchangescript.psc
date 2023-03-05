;/ Decompiled by Champollion V1.0.1
Source   : CCHorseArmorChangeScript.psc
Modified : 2020-02-20 00:48:13
Compiled : 2022-09-05 05:09:24
User     : builds
Computer : RKVBGSBUILD11
/;
scriptName CCHorseArmorChangeScript extends Quest
{Changes horse armors at hostlers and blacksmiths}

;-- Properties --------------------------------------
Quest property Stables auto
globalvariable property CCHorseArmorCost auto
message property CCHorseArmorMessageNotEnoughGold auto
message property CCHorseArmorMessageArmorChanged auto
faction property PlayerHorseFaction auto
armor[] property ArrayUnicornArmors auto
message property CCHorseArmorMessageArmorRemoved auto
actor property PlayerRef auto
spell property CCHorseArmorAbEssentialFlag auto
message property CCHorseArmorMessageNoHorsesOwned auto
actorbase[] property DisallowedHorses auto
message property CCHorseArmorMessageArmorAlreadyEquipped auto
miscobject[] property ArrayHorseArmorsMisc auto
keyword property ccDisallowSaddleSwap auto
globalvariable property CCHorseArmorCostSwapOnly auto
referencealias property StablesPlayersHorse auto
miscobject property Gold001 auto
armor[] property ArrayHorseArmors auto
formlist property CCHorseArmorList auto
message property CCHorseArmorMessageArmorCannotUse auto

;-- Variables ---------------------------------------
armor[] ArrayArmors

;-- Functions ---------------------------------------

function EquipHorseArmor(actor akPlayerHorseRef, armor akArmorToEquip)

	self.SwapArmorForMiscObject(akPlayerHorseRef)
	akPlayerHorseRef.UnequipItemSlot(45)
	akPlayerHorseRef.EquipItem(akArmorToEquip as form, 1 as Bool, false)
	CCHorseArmorMessageArmorChanged.Show(0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000)
	; if !akPlayerHorseRef.IsEssential()
	; 	akPlayerHorseRef.GetActorBase().SetEssential(true)
	; 	akPlayerHorseRef.AddSpell(CCHorseArmorAbEssentialFlag, true)
	; endIf
endFunction

function ChangeHorseArmor(Int ArmorID)

	actor PlayerHorseREF = self.GetPlayerHorse()
	if PlayerHorseREF as Bool && !PlayerHorseREF.IsDead()
		if self.CanChangeHorseArmor(PlayerHorseREF)
			if self.IsUnicorn(PlayerHorseREF)
				ArrayArmors = ArrayUnicornArmors
			else
				ArrayArmors = ArrayHorseArmors
			endIf
			armor ArmorToEquip = ArrayArmors[ArmorID]
			if ArmorToEquip
				if PlayerHorseREF.IsEquipped(ArmorToEquip as form)
					CCHorseArmorMessageArmorAlreadyEquipped.Show(0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000)
				else
					Int costGold
					miscobject inventoryItem = self.GetMiscObjectForArmor(ArmorToEquip)
					Bool doEquipArmor = false
					Bool removeMiscItem = false
					if PlayerRef.GetItemCount(inventoryItem as form)
						costGold = CCHorseArmorCostSwapOnly.GetValueInt()
						removeMiscItem = true
					else
						costGold = CCHorseArmorCost.GetValueInt()
					endIf
					if PlayerRef.GetItemCount(Gold001 as form) >= costGold
						doEquipArmor = true
						PlayerRef.RemoveItem(Gold001 as form, costGold, false, none)
						if removeMiscItem
							PlayerRef.RemoveItem(inventoryItem as form, 1, false, none)
						endIf
					else
						CCHorseArmorMessageNotEnoughGold.Show(0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000)
						doEquipArmor = false
					endIf
					if doEquipArmor
						self.EquipHorseArmor(PlayerHorseREF, ArmorToEquip)
					endIf
				endIf
			endIf
		else
			CCHorseArmorMessageArmorCannotUse.Show(0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000)
		endIf
	else
		CCHorseArmorMessageNoHorsesOwned.Show(0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000)
	endIf
endFunction

armor function GetArmorForMiscObject(miscobject HorseArmorMisc)

	Int arrayPos = ArrayHorseArmorsMisc.find(HorseArmorMisc, 0)
	return ArrayArmors[arrayPos]
endFunction

; Skipped compiler generated GotoState

; Skipped compiler generated GetState

Bool function SwapArmorForMiscObject(actor PlayerHorse)

	Bool RemovedArmor = false
	Int i = 0
	while i < ArrayArmors.length
		if PlayerHorse.IsEquipped(ArrayArmors[i] as form)
			PlayerRef.AddItem(ArrayHorseArmorsMisc[i] as form, 1, false)
			PlayerHorse.RemoveItem(ArrayArmors[i] as form, 1, false, none)
			RemovedArmor = true
		endIf
		i += 1
	endWhile
	return RemovedArmor
endFunction

miscobject function GetMiscObjectForArmor(armor HorseArmor)

	Int arrayPos = ArrayArmors.find(HorseArmor, 0)
	return ArrayHorseArmorsMisc[arrayPos]
endFunction

Bool function IsUnicorn(actor akPlayerHorseRef)

	actorbase Unicorn = game.GetFormFromFile(2052, "ccBGSSSE034-MntUni.esl") as actorbase
	return Unicorn as Bool && akPlayerHorseRef.GetActorBase() == Unicorn
endFunction

Bool function CanChangeHorseArmor(actor PlayerHorse)

	keyword ccBGSSSE034WildHorseKeyword = game.GetFormFromFile(2088, "ccBGSSSE034-MntUni.esl") as keyword
	if DisallowedHorses.find(PlayerHorse.GetActorBase(), 0) != -1 || PlayerHorse.HasKeyword(ccDisallowSaddleSwap)
		return false
	elseIf PlayerHorse.HasKeyword(ccBGSSSE034WildHorseKeyword) && !PlayerHorse.IsInFaction(PlayerHorseFaction)
		return false
	else
		return true
	endIf
endFunction

actor function GetPlayerHorse()

	if game.GetPlayersLastRiddenHorse()
		return game.GetPlayersLastRiddenHorse()
	elseIf StablesPlayersHorse.GetActorReference()
		return StablesPlayersHorse.GetActorReference()
	else
		return none
	endIf
endFunction

function RemoveHorseArmor()

	actor PlayerHorseREF = self.GetPlayerHorse()
	if PlayerHorseREF
		if self.SwapArmorForMiscObject(PlayerHorseREF)
			CCHorseArmorMessageArmorRemoved.Show(0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000)
		endIf
		PlayerHorseREF.RemoveItem(CCHorseArmorList as form, 999, false, none)
		if PlayerHorseREF.HasSpell(CCHorseArmorAbEssentialFlag as form)
			PlayerHorseREF.GetActorBase().SetEssential(false)
			PlayerHorseREF.RemoveSpell(CCHorseArmorAbEssentialFlag)
		endIf
	else
		CCHorseArmorMessageNoHorsesOwned.Show(0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000)
	endIf
endFunction
