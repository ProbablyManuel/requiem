Scriptname ccBGSSSE001_DLCDetectionScript extends ReferenceAlias  

Actor property PlayerRef auto
ActorBase property ccBGSSSE001_SwimsInDeepWaters auto
ActorBase property ccBGSSSE001_Viriya auto

GlobalVariable property ccBGSSSE001_VersionCurrent auto ; constant
{ Current version number. }
GlobalVariable property ccBGSSSE001_VersionLastKnown auto
{ The last known version number. Used to know which updates to run on game load. }

Armor property ccBGSSSE001_ClothesFishingBoots auto
Armor property ccBGSSSE001_ClothesFishingClothes auto
Armor property ccBGSSSE001_ClothesFishingHat auto
Armor property ccBGSSSE001_ClothesCollegeBootsVagrant auto
Armor property ccBGSSSE001_ClothesCollegeHoodVagrant auto
Armor property ccBGSSSE001_ClothesCollegeRobesVagrant auto
Armor property ccBGSSSE001_ClothesCollegeRobesVagrantConjuration01 auto
Armor property ccBGSSSE001_ClothesCollegeRobesVagrantConjuration02 auto
Armor property ccBGSSSE001_ClothesCollegeRobesVagrantConjuration03 auto
Armor property ccBGSSSE001_ClothesCollegeRobesVagrantConjuration04 auto
Armor property ccBGSSSE001_ClothesCollegeRobesVagrantConjuration05 auto
Armor property ccBGSSSE001_MudcrabAmuletUnenchanted auto
Book property ccBGSSSE001_LineAndLure auto
FormList property BYOHHouseFishContainerList auto
FormList property RiftenFisheryLockList auto
FormList property HelpManualPC auto
FormList property HelpManualXBox auto
Ingredient[] property hatcheryFishIngredient auto
LeveledItem property ccBGSSSE001_LItemFoodFishBucketFish25 auto
LeveledItem property ccBGSSSE001_LItemFoodFishCarp25 auto
LeveledItem property ccBGSSSE001_FishingMapsAll auto
LeveledItem property DeathItemMudCrab01 auto
LeveledItem property DeathItemMudCrab02 auto
LeveledItem property DeathItemMudCrab03 auto
LeveledItem property LItemFoodFishList auto
LeveledItem property LItemDraugr02Weapon1H auto
LeveledItem property LItemDraugr02Weapon2H auto
LeveledItem property LItemClothesWork auto
LeveledItem property LItemClothesAll auto
LeveledItem property LootToolRandom05 auto
LeveledItem property LItemMiscVendorMiscItems75 auto
LeveledItem property LItemBook0All auto
LeveledItem property LItemBookClutter auto
LeveledItem property LItemRobesConjuration auto
LeveledItem property BYOHLItemKhajiitCaravans auto
LeveledItem property LItemFoodInnCommon auto
LeveledItem property LItemFoodInnCommon10 auto
Potion property ccBGSSSE001_FoodCrabMeat auto
Potion property ccBGSSSE001_FoodBucketFishCooked auto
Potion property ccBGSSSE001_FoodTunaSalmonCooked auto
Potion property ccBGSSSE001_FoodCuckooCatfishCooked auto
Potion[] property hatcheryFishFood auto
Potion[] property rawFish auto
GlobalVariable property ccBGSSSE001_ShowedReelPromptThisSession auto
GlobalVariable property ccBGSSSE001_ShowedCatchPromptThisSession auto
Message property ccBGSSSE001_HelpFishingLong auto
Message property ccBGSSSE001_HelpFishingLongXbox auto
Weapon property ccBGSSSE001_FishingRodImperialWeap auto
Weapon property ccBGSSSE001_DraugrDagger auto
Weapon property ccBGSSSE001_DraugrDaggerHoned auto
Weapon property ccBGSSSE001_DraugrMace auto
Weapon property ccBGSSSE001_DraugrMaceHoned auto
Weapon property ccBGSSSE001_DraugrWarhammer auto
Weapon property ccBGSSSE001_DraugrWarhammerHoned auto

MiscObject property BYOHHouseInteriorPart021WallShelvesx2_02 auto
MiscObject property BYOHHouseInteriorPart185VampireCoffin01 auto
Location property BYOHHouse1LocationInterior auto
Location property BYOHHouse2LocationInterior auto
Location property BYOHHouse3LocationInterior auto
Location property SolitudeProudspireManorLocation auto
ObjectReference property BYOHHouse1HoldingChestRoom12 auto
ObjectReference property BYOHHouse2HoldingChestRoom12 auto
ObjectReference property BYOHHouse3HoldingChestRoom12 auto
FormList property ccBGSSSE001_BYOHMiscObjectList auto
FormList property ccBGSSSE001_BYOHHouse1UpgradeList auto
FormList property ccBGSSSE001_BYOHHouse2UpgradeList auto
FormList property ccBGSSSE001_BYOHHouse3UpgradeList auto
FormList property BYOHHouseBuildingRoom12MasterList auto
FormList property ccBGSSSE001_BYOHAllFishingSpecificUpgradeMiscItems auto

ccBGSSSE001_FishPlaqueScript[] property House01FishPlaques auto
ccBGSSSE001_FishPlaqueScript[] property House02FishPlaques auto
ccBGSSSE001_FishPlaqueScript[] property House03FishPlaques auto
ccBGSSSE001_FishPlaqueScript[] property ProudspireManorFishPlaques auto

ccBGSSSE001_FishPlaqueAliasScript[] property FishAtHouse01Plaques auto
ccBGSSSE001_FishPlaqueAliasScript[] property FishAtHouse02Plaques auto
ccBGSSSE001_FishPlaqueAliasScript[] property FishAtHouse03Plaques auto
ccBGSSSE001_FishPlaqueAliasScript[] property FishAtProudspireManorPlaques auto


Event OnInit()
	AddHelpArticles()
	RunSetupBaseGame()
	RunDetectDLC()
	RunUpdates()
endEvent

Event OnPlayerLoadGame()
	; Reset play session help messages
	ccBGSSSE001_ShowedReelPromptThisSession.SetValueInt(0)
	ccBGSSSE001_ShowedCatchPromptThisSession.SetValueInt(0)

	RunDetectDLC()
	RunUpdates()
endEvent

function RunUpdates()
	debug.trace("FISHING: Checking for updates.")
	CheckForVersionUpdates(ccBGSSSE001_VersionLastKnown.GetValueInt())
	ccBGSSSE001_VersionLastKnown.SetValueInt(ccBGSSSE001_VersionCurrent.GetValueInt())
endFunction

function CheckForVersionUpdates(int aiLastKnownVersion)
	CheckAndApplyFishingPlaquePatch()
endFunction

function RunSetupBaseGame()
	; ===== Base Game

	; Add NPCs to Lock Lists
	RiftenFisheryLockList.AddForm(ccBGSSSE001_SwimsInDeepWaters)
	RiftenFisheryLockList.AddForm(ccBGSSSE001_Viriya)

	; Add fish to lists (for Fish Barrels, etc)
	LItemFoodFishList.AddForm(ccBGSSSE001_LItemFoodFishBucketFish25, 1, 1)
	LItemFoodFishList.AddForm(ccBGSSSE001_LItemFoodFishCarp25, 1, 1)

	; Add crab meat to leveled lists
	DeathItemMudCrab01.AddForm(ccBGSSSE001_FoodCrabMeat, 1, 1)
	DeathItemMudCrab02.AddForm(ccBGSSSE001_FoodCrabMeat, 1, 1)
	DeathItemMudCrab03.AddForm(ccBGSSSE001_FoodCrabMeat, 1, 2)

	; Add cooked fish to leveled lists
	LItemFoodInnCommon.AddForm(ccBGSSSE001_FoodBucketFishCooked, 1, 1)
	LItemFoodInnCommon.AddForm(ccBGSSSE001_FoodTunaSalmonCooked, 1, 1)
	LItemFoodInnCommon.AddForm(ccBGSSSE001_FoodCuckooCatfishCooked, 1, 1)
	LItemFoodInnCommon10.AddForm(ccBGSSSE001_FoodBucketFishCooked, 1, 1)
	LItemFoodInnCommon10.AddForm(ccBGSSSE001_FoodTunaSalmonCooked, 1, 1)
	LItemFoodInnCommon10.AddForm(ccBGSSSE001_FoodCuckooCatfishCooked, 1, 1)

	; Add Fishing Clothes to leveled lists
	LItemClothesWork.AddForm(ccBGSSSE001_ClothesFishingBoots, 1, 1)
	LItemClothesWork.AddForm(ccBGSSSE001_ClothesFishingClothes, 1, 1)
	LItemClothesWork.AddForm(ccBGSSSE001_ClothesFishingHat, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesFishingBoots, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesFishingClothes, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesFishingHat, 1, 1)

	; Add Crabber's Charm to leveled lists
	LItemClothesAll.AddForm(ccBGSSSE001_MudcrabAmuletUnenchanted, 1, 1)

	; Add Vagrant Robes to leveled lists
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesCollegeBootsVagrant, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesCollegeHoodVagrant, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrant, 1, 1)
	LItemRobesConjuration.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrantConjuration01, 1, 1)
	LItemRobesConjuration.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrantConjuration02, 10, 1)
	LItemRobesConjuration.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrantConjuration03, 20, 1)
	LItemRobesConjuration.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrantConjuration04, 30, 1)
	LItemRobesConjuration.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrantConjuration05, 40, 1)

	; Add Fishing Rods to leveled lists
	LootToolRandom05.AddForm(ccBGSSSE001_FishingRodImperialWeap, 1, 1)
	LItemMiscVendorMiscItems75.AddForm(ccBGSSSE001_FishingRodImperialWeap, 1, 1)

	; Add fishing maps to leveled lists and vendors
	BYOHLItemKhajiitCaravans.AddForm(ccBGSSSE001_FishingMapsAll, 1, 3)

	; Add books to leveled lists
	LItemBook0All.AddForm(ccBGSSSE001_LineAndLure, 1, 1)
	LItemBookClutter.AddForm(ccBGSSSE001_LineAndLure, 1, 1)

	; Mace
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMace, 1, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMace, 1, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMace, 1, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMace, 1, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned, 12, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned, 13, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned, 14, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned, 15, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned, 15, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned, 21, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned, 22, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned, 23, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned, 24, 1)

	; Warhammer
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammer, 1, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammer, 1, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammer, 1, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammer, 1, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned, 12, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned, 13, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned, 14, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned, 15, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned, 15, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned, 21, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned, 22, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned, 23, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned, 24, 1)

	; ===== Base Game (Hearthfire)
	; Add our new upgrade Misc Items to the master list
	int upgradeListLength = ccBGSSSE001_BYOHAllFishingSpecificUpgradeMiscItems.GetSize()
	int i = 0
	while i < upgradeListLength
		BYOHHouseBuildingRoom12MasterList.AddForm(ccBGSSSE001_BYOHAllFishingSpecificUpgradeMiscItems.GetAt(i))
		i += 1
	endWhile

	; Move Wall Shelves and Coffin upgrade to new room
	Location playerLoc = PlayerRef.GetCurrentLocation()

	; --- Falkreath
	; Does the player have the wall shelf or coffin upgrade?
	BuildBYOHObject(playerLoc, BYOHHouse1LocationInterior, BYOHHouseInteriorPart021WallShelvesx2_02)
	BuildBYOHObject(playerLoc, BYOHHouse1LocationInterior, BYOHHouseInteriorPart185VampireCoffin01)

	; --- Hjaalmarch
	; Does the player have the wall shelf or coffin upgrade?
	BuildBYOHObject(playerLoc, BYOHHouse2LocationInterior, BYOHHouseInteriorPart021WallShelvesx2_02)
	BuildBYOHObject(playerLoc, BYOHHouse2LocationInterior, BYOHHouseInteriorPart185VampireCoffin01)

	; --- Pale
	; Does the player have the wall shelf or coffin upgrade?
	BuildBYOHObject(playerLoc, BYOHHouse3LocationInterior, BYOHHouseInteriorPart021WallShelvesx2_02)
	BuildBYOHObject(playerLoc, BYOHHouse3LocationInterior, BYOHHouseInteriorPart185VampireCoffin01)

	; Base Game (Hearthfire) - Add fish to Fish Hatchery Inventory Filter List
	i = 0
	while i < hatcheryFishFood.length
		BYOHHouseFishContainerList.AddForm(hatcheryFishFood[i])
		i += 1
	endWhile

	i = 0
	while i < hatcheryFishIngredient.length
		BYOHHouseFishContainerList.AddForm(hatcheryFishIngredient[i])
		i += 1
	endWhile
endFunction

function AddHelpArticles()
	if !HelpManualPC.HasForm(ccBGSSSE001_HelpFishingLong)
		HelpManualPC.AddForm(ccBGSSSE001_HelpFishingLong)
	endif
	if !HelpManualXBox.HasForm(ccBGSSSE001_HelpFishingLongXbox)
		HelpManualXBox.AddForm(ccBGSSSE001_HelpFishingLongXbox)
	endif
endFunction

function RunDetectDLC()
	; Survival Mode - Add raw fish to the Raw Meat list
	FormList survivalRawMeatList = Game.GetFormFromFile(0x0008B0, "ccQDRSSE001-SurvivalMode.esl") as FormList

	if survivalRawMeatList
		int i = 0

		while i < rawFish.length
			if !survivalRawMeatList.HasForm(rawFish[i])
				survivalRawMeatList.AddForm(rawFish[i])
			endif
			i += 1
		endWhile
	endif
endFunction

ObjectReference function GetCellarHoldingChestForLocation(Location akLocation)
	if akLocation == BYOHHouse1LocationInterior
		return BYOHHouse1HoldingChestRoom12
	elseif BYOHHouse2LocationInterior
		return BYOHHouse2HoldingChestRoom12
	elseif BYOHHouse3LocationInterior
		return BYOHHouse3HoldingChestRoom12
	else
		return None
	endif
endFunction

ObjectReference function GetObjectForLocationAndItem(Location akLocation, Form akObjectToBuild)
	int idx = ccBGSSSE001_BYOHMiscObjectList.Find(akObjectToBuild)

	if akLocation == BYOHHouse1LocationInterior	
		return ccBGSSSE001_BYOHHouse1UpgradeList.GetAt(idx) as ObjectReference
	elseif akLocation == BYOHHouse2LocationInterior
		return ccBGSSSE001_BYOHHouse2UpgradeList.GetAt(idx) as ObjectReference
	elseif akLocation == BYOHHouse3LocationInterior
		return ccBGSSSE001_BYOHHouse3UpgradeList.GetAt(idx) as ObjectReference
	else
		return None
	endif
endFunction



function BuildBYOHObject(Location akCurrentPlayerLocation, Location akHouseLocationToBuild, Form akObjectToBuild)
	if GetCellarHoldingChestForLocation(akHouseLocationToBuild).GetItemCount(akObjectToBuild) > 0 || \
		(akCurrentPlayerLocation == akHouseLocationToBuild && PlayerRef.GetItemCount(akObjectToBuild) > 0)
		ObjectReference myObject = GetObjectForLocationAndItem(akHouseLocationToBuild, akObjectToBuild)
		if myObject.IsDisabled()
			debug.trace("Player has built " + myObject + " for location " + akHouseLocationToBuild + ", enabling.")
			myObject.EnableNoWait()
		endif
	endif
endFunction

function CheckAndApplyFishingPlaquePatch()
	; Do we have the latest plugin? If not, bail.
	if !SolitudeProudspireManorLocation
		debug.trace("FISHING: CheckAndApplyFishingPlaquePatch(): Plugin is old. Aborting.")
		return
	endif

	if House02FishPlaques[0].PlaqueFishAlias == House01FishPlaques[0].PlaqueFishAlias
		; Data is old and should be corrected

		; First, migrate any existing fish references to the correct alias
		int i = 0
		while i < House01FishPlaques.Length
			ObjectReference theFish = House01FishPlaques[i].PlaqueFishAlias.GetRef()
			int plaqueNumber = i + 1
			if theFish
				if theFish.IsInLocation(BYOHHouse2LocationInterior)
					debug.trace("FISHING: Migrating " + theFish + " from House 1 plaque " + plaqueNumber + " to House 2 plaque " + plaqueNumber)
					FishAtHouse01Plaques[i].Clear()
					FishAtHouse02Plaques[i].ForceRefTo(theFish)
				elseif theFish.IsInLocation(BYOHHouse3LocationInterior)
					debug.trace("FISHING: Migrating " + theFish + " from House 1 plaque " + plaqueNumber + " to House 3 plaque " + plaqueNumber)
					FishAtHouse01Plaques[i].Clear()
					FishAtHouse03Plaques[i].ForceRefTo(theFish)
				elseif theFish.IsInLocation(SolitudeProudspireManorLocation) && plaqueNumber >= 4 && plaqueNumber <= 6
					; Proudspire Manor's plaques were set to Alias 4, 5, and 6.
					debug.trace("FISHING: Migrating " + theFish + " from House 1 plaque " + plaqueNumber + " to Proudspire Manor plaque " + (plaqueNumber - 3))
					FishAtHouse01Plaques[i].Clear()
					int proudspireManorPlaqueIndex = i - 3
					FishAtProudspireManorPlaques[proudspireManorPlaqueIndex].ForceRefTo(theFish)
				endif
			endif
			i += 1
		endWhile

		; Next, reassign the alias on the plaque
		FishingPlaquePatchReassignAliases(House02FishPlaques, FishAtHouse02Plaques)
		FishingPlaquePatchReassignAliases(House03FishPlaques, FishAtHouse03Plaques)
		FishingPlaquePatchReassignAliases(ProudspireManorFishPlaques, FishAtProudspireManorPlaques)

		debug.trace("FISHING: CheckAndApplyFishingPlaquePatch(): Patch applied.")
	endif
endFunction

function FishingPlaquePatchReassignAliases(ccBGSSSE001_FishPlaqueScript[] akFishPlaques, ccBGSSSE001_FishPlaqueAliasScript[] akFishAtHousePlaqueAliases)
	int i = 0
	while i < akFishPlaques.Length
		akFishPlaques[i].PlaqueFishAlias = akFishAtHousePlaqueAliases[i]
		i += 1
	endWhile
endFunction