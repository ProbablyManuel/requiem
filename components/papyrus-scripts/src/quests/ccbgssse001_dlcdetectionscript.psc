;/ Decompiled by Champollion V1.0.1
Source   : ccBGSSSE001_DLCDetectionScript.psc
Modified : 2022-08-27 01:52:07
Compiled : 2022-08-30 20:02:47
User     : builds
Computer : RKVBGSBUILD11
/;
scriptName ccBGSSSE001_DLCDetectionScript extends ReferenceAlias

;-- Properties --------------------------------------
armor property ccBGSSSE001_ClothesCollegeRobesVagrantConjuration01 auto
potion property ccBGSSSE001_FoodTunaSalmonCooked auto
leveleditem property LItemClothesWork auto
leveleditem property LItemClothesAll auto
formlist property ccBGSSSE001_BYOHHouse3UpgradeList auto
armor property ccBGSSSE001_ClothesFishingHat auto
armor property ccBGSSSE001_ClothesCollegeRobesVagrantConjuration04 auto
location property BYOHHouse2LocationInterior auto
globalvariable property ccBGSSSE001_VersionCurrent auto
{Current version number.}
potion[] property rawFish auto
formlist property HelpManualPC auto
armor property ccBGSSSE001_ClothesFishingClothes auto
globalvariable property ccBGSSSE001_ShowedCatchPromptThisSession auto
ccbgssse001_fishplaquescript[] property House01FishPlaques auto
leveleditem property LItemFoodInnCommon10 auto
leveleditem property LItemFoodFishList auto
armor property ccBGSSSE001_ClothesCollegeHoodVagrant auto
actorbase property ccBGSSSE001_Viriya auto
objectreference property BYOHHouse2HoldingChestRoom12 auto
weapon property ccBGSSSE001_DraugrDagger auto
leveleditem property LItemBook0All auto
leveleditem property ccBGSSSE001_LItemFoodFishBucketFish25 auto
weapon property ccBGSSSE001_DraugrMaceHoned auto
formlist property ccBGSSSE001_BYOHAllFishingSpecificUpgradeMiscItems auto
formlist property HelpManualXBox auto
formlist property BYOHHouseBuildingRoom12MasterList auto
potion property ccBGSSSE001_FoodCrabMeat auto
armor property ccBGSSSE001_MudcrabAmuletUnenchanted auto
message property ccBGSSSE001_HelpFishingLong auto
armor property ccBGSSSE001_ClothesCollegeRobesVagrantConjuration02 auto
leveleditem property LItemFoodInnCommon auto
book property ccBGSSSE001_LineAndLure auto
ccbgssse001_fishplaquealiasscript[] property FishAtHouse03Plaques auto
formlist property RiftenFisheryLockList auto
weapon property ccBGSSSE001_DraugrWarhammer auto
globalvariable property ccBGSSSE001_VersionLastKnown auto
{The last known version number. Used to know which updates to run on game load.}
formlist property ccBGSSSE001_BYOHHouse2UpgradeList auto
ingredient[] property hatcheryFishIngredient auto
leveleditem property DeathItemMudCrab03 auto
leveleditem property LItemDraugr02Weapon2H auto
ccbgssse001_fishplaquealiasscript[] property FishAtHouse02Plaques auto
globalvariable property ccBGSSSE001_ShowedReelPromptThisSession auto
potion[] property hatcheryFishFood auto
ccbgssse001_fishplaquealiasscript[] property FishAtHouse01Plaques auto
ccbgssse001_fishplaquescript[] property ProudspireManorFishPlaques auto
weapon property ccBGSSSE001_DraugrMace auto
leveleditem property BYOHLItemKhajiitCaravans auto
miscobject property BYOHHouseInteriorPart185VampireCoffin01 auto
leveleditem property ccBGSSSE001_LItemFoodFishCarp25 auto
formlist property ccBGSSSE001_BYOHHouse1UpgradeList auto
weapon property ccBGSSSE001_FishingRodImperialWeap auto
weapon property ccBGSSSE001_DraugrDaggerHoned auto
objectreference property BYOHHouse3HoldingChestRoom12 auto
location property BYOHHouse3LocationInterior auto
objectreference property BYOHHouse1HoldingChestRoom12 auto
armor property ccBGSSSE001_ClothesCollegeRobesVagrantConjuration05 auto
location property SolitudeProudspireManorLocation auto
location property BYOHHouse1LocationInterior auto
ccbgssse001_fishplaquescript[] property House03FishPlaques auto
actor property PlayerRef auto
leveleditem property ccBGSSSE001_FishingMapsAll auto
armor property ccBGSSSE001_ClothesCollegeRobesVagrantConjuration03 auto
formlist property BYOHHouseFishContainerList auto
miscobject property BYOHHouseInteriorPart021WallShelvesx2_02 auto
ccbgssse001_fishplaquescript[] property House02FishPlaques auto
leveleditem property LItemRobesConjuration auto
armor property ccBGSSSE001_ClothesCollegeRobesVagrant auto
leveleditem property DeathItemMudCrab01 auto
leveleditem property LItemBookClutter auto
leveleditem property LootToolRandom05 auto
leveleditem property LItemDraugr02Weapon1H auto
potion property ccBGSSSE001_FoodBucketFishCooked auto
ccbgssse001_fishplaquealiasscript[] property FishAtProudspireManorPlaques auto
actorbase property ccBGSSSE001_SwimsInDeepWaters auto
leveleditem property LItemMiscVendorMiscItems75 auto
armor property ccBGSSSE001_ClothesFishingBoots auto
armor property ccBGSSSE001_ClothesCollegeBootsVagrant auto
weapon property ccBGSSSE001_DraugrWarhammerHoned auto
formlist property ccBGSSSE001_BYOHMiscObjectList auto
leveleditem property DeathItemMudCrab02 auto
message property ccBGSSSE001_HelpFishingLongXbox auto
potion property ccBGSSSE001_FoodCuckooCatfishCooked auto

;-- Variables ---------------------------------------

;-- Functions ---------------------------------------

function CheckForVersionUpdates(Int aiLastKnownVersion)

	self.CheckAndApplyFishingPlaquePatch()
endFunction

; Skipped compiler generated GetState

function CheckAndApplyFishingPlaquePatch()

	if !SolitudeProudspireManorLocation
		debug.trace("FISHING: CheckAndApplyFishingPlaquePatch(): Plugin is old. Aborting.", 0)
		return 
	endIf
	if House02FishPlaques[0].PlaqueFishAlias == House01FishPlaques[0].PlaqueFishAlias
		Int i = 0
		while i < House01FishPlaques.length
			objectreference theFish = House01FishPlaques[i].PlaqueFishAlias.GetRef()
			Int plaqueNumber = i + 1
			if theFish
				if theFish.IsInLocation(BYOHHouse2LocationInterior)
					debug.trace("FISHING: Migrating " + theFish as String + " from House 1 plaque " + plaqueNumber as String + " to House 2 plaque " + plaqueNumber as String, 0)
					FishAtHouse01Plaques[i].Clear()
					FishAtHouse02Plaques[i].ForceRefTo(theFish)
				elseIf theFish.IsInLocation(BYOHHouse3LocationInterior)
					debug.trace("FISHING: Migrating " + theFish as String + " from House 1 plaque " + plaqueNumber as String + " to House 3 plaque " + plaqueNumber as String, 0)
					FishAtHouse01Plaques[i].Clear()
					FishAtHouse03Plaques[i].ForceRefTo(theFish)
				elseIf theFish.IsInLocation(SolitudeProudspireManorLocation) && plaqueNumber >= 4 && plaqueNumber <= 6
					debug.trace("FISHING: Migrating " + theFish as String + " from House 1 plaque " + plaqueNumber as String + " to Proudspire Manor plaque " + (plaqueNumber - 3) as String, 0)
					FishAtHouse01Plaques[i].Clear()
					Int proudspireManorPlaqueIndex = i - 3
					FishAtProudspireManorPlaques[proudspireManorPlaqueIndex].ForceRefTo(theFish)
				endIf
			endIf
			i += 1
		endWhile
		self.FishingPlaquePatchReassignAliases(House02FishPlaques, FishAtHouse02Plaques)
		self.FishingPlaquePatchReassignAliases(House03FishPlaques, FishAtHouse03Plaques)
		self.FishingPlaquePatchReassignAliases(ProudspireManorFishPlaques, FishAtProudspireManorPlaques)
		debug.trace("FISHING: CheckAndApplyFishingPlaquePatch(): Patch applied.", 0)
	endIf
endFunction

function RunUpdates()

	debug.trace("FISHING: Checking for updates.", 0)
	self.CheckForVersionUpdates(ccBGSSSE001_VersionLastKnown.GetValueInt())
	ccBGSSSE001_VersionLastKnown.SetValueInt(ccBGSSSE001_VersionCurrent.GetValueInt())
endFunction

objectreference function GetObjectForLocationAndItem(location akLocation, form akObjectToBuild)

	Int idx = ccBGSSSE001_BYOHMiscObjectList.Find(akObjectToBuild)
	if akLocation == BYOHHouse1LocationInterior
		return ccBGSSSE001_BYOHHouse1UpgradeList.GetAt(idx) as objectreference
	elseIf akLocation == BYOHHouse2LocationInterior
		return ccBGSSSE001_BYOHHouse2UpgradeList.GetAt(idx) as objectreference
	elseIf akLocation == BYOHHouse3LocationInterior
		return ccBGSSSE001_BYOHHouse3UpgradeList.GetAt(idx) as objectreference
	else
		return none
	endIf
endFunction

function FishingPlaquePatchReassignAliases(ccbgssse001_fishplaquescript[] akFishPlaques, ccbgssse001_fishplaquealiasscript[] akFishAtHousePlaqueAliases)

	Int i = 0
	while i < akFishPlaques.length
		akFishPlaques[i].PlaqueFishAlias = akFishAtHousePlaqueAliases[i]
		i += 1
	endWhile
endFunction

; Skipped compiler generated GotoState

function AddHelpArticles()

	if !HelpManualPC.HasForm(ccBGSSSE001_HelpFishingLong as form)
		HelpManualPC.AddForm(ccBGSSSE001_HelpFishingLong as form)
	endIf
	if !HelpManualXBox.HasForm(ccBGSSSE001_HelpFishingLongXbox as form)
		HelpManualXBox.AddForm(ccBGSSSE001_HelpFishingLongXbox as form)
	endIf
endFunction

function BuildBYOHObject(location akCurrentPlayerLocation, location akHouseLocationToBuild, form akObjectToBuild)

	if self.GetCellarHoldingChestForLocation(akHouseLocationToBuild).GetItemCount(akObjectToBuild) > 0 || akCurrentPlayerLocation == akHouseLocationToBuild && PlayerRef.GetItemCount(akObjectToBuild) > 0
		objectreference myObject = self.GetObjectForLocationAndItem(akHouseLocationToBuild, akObjectToBuild)
		if myObject.IsDisabled()
			debug.trace("Player has built " + myObject as String + " for location " + akHouseLocationToBuild as String + ", enabling.", 0)
			myObject.EnableNoWait(false)
		endIf
	endIf
endFunction

function OnPlayerLoadGame()

	ccBGSSSE001_ShowedReelPromptThisSession.SetValueInt(0)
	ccBGSSSE001_ShowedCatchPromptThisSession.SetValueInt(0)
	self.RunDetectDLC()
	self.RunUpdates()
endFunction

objectreference function GetCellarHoldingChestForLocation(location akLocation)

	if akLocation == BYOHHouse1LocationInterior
		return BYOHHouse1HoldingChestRoom12
	elseIf BYOHHouse2LocationInterior
		return BYOHHouse2HoldingChestRoom12
	elseIf BYOHHouse3LocationInterior
		return BYOHHouse3HoldingChestRoom12
	else
		return none
	endIf
endFunction

function RunSetupBaseGame()

	RiftenFisheryLockList.AddForm(ccBGSSSE001_SwimsInDeepWaters as form)
	RiftenFisheryLockList.AddForm(ccBGSSSE001_Viriya as form)
	LItemFoodFishList.AddForm(ccBGSSSE001_LItemFoodFishBucketFish25 as form, 1, 1)
	LItemFoodFishList.AddForm(ccBGSSSE001_LItemFoodFishCarp25 as form, 1, 1)
	DeathItemMudCrab01.AddForm(ccBGSSSE001_FoodCrabMeat as form, 1, 1)
	DeathItemMudCrab02.AddForm(ccBGSSSE001_FoodCrabMeat as form, 1, 1)
	DeathItemMudCrab03.AddForm(ccBGSSSE001_FoodCrabMeat as form, 1, 2)
	LItemFoodInnCommon.AddForm(ccBGSSSE001_FoodBucketFishCooked as form, 1, 1)
	LItemFoodInnCommon.AddForm(ccBGSSSE001_FoodTunaSalmonCooked as form, 1, 1)
	LItemFoodInnCommon.AddForm(ccBGSSSE001_FoodCuckooCatfishCooked as form, 1, 1)
	LItemFoodInnCommon10.AddForm(ccBGSSSE001_FoodBucketFishCooked as form, 1, 1)
	LItemFoodInnCommon10.AddForm(ccBGSSSE001_FoodTunaSalmonCooked as form, 1, 1)
	LItemFoodInnCommon10.AddForm(ccBGSSSE001_FoodCuckooCatfishCooked as form, 1, 1)
	LItemClothesWork.AddForm(ccBGSSSE001_ClothesFishingBoots as form, 1, 1)
	LItemClothesWork.AddForm(ccBGSSSE001_ClothesFishingClothes as form, 1, 1)
	LItemClothesWork.AddForm(ccBGSSSE001_ClothesFishingHat as form, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesFishingBoots as form, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesFishingClothes as form, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesFishingHat as form, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_MudcrabAmuletUnenchanted as form, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesCollegeBootsVagrant as form, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesCollegeHoodVagrant as form, 1, 1)
	LItemClothesAll.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrant as form, 1, 1)
	LItemRobesConjuration.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrantConjuration01 as form, 1, 1)
	LItemRobesConjuration.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrantConjuration02 as form, 10, 1)
	LItemRobesConjuration.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrantConjuration03 as form, 20, 1)
	LItemRobesConjuration.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrantConjuration04 as form, 30, 1)
	LItemRobesConjuration.AddForm(ccBGSSSE001_ClothesCollegeRobesVagrantConjuration05 as form, 40, 1)
	LootToolRandom05.AddForm(ccBGSSSE001_FishingRodImperialWeap as form, 1, 1)
	LItemMiscVendorMiscItems75.AddForm(ccBGSSSE001_FishingRodImperialWeap as form, 1, 1)
	BYOHLItemKhajiitCaravans.AddForm(ccBGSSSE001_FishingMapsAll as form, 1, 3)
	LItemBook0All.AddForm(ccBGSSSE001_LineAndLure as form, 1, 1)
	LItemBookClutter.AddForm(ccBGSSSE001_LineAndLure as form, 1, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMace as form, 1, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMace as form, 1, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMace as form, 1, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMace as form, 1, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned as form, 12, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned as form, 13, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned as form, 14, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned as form, 15, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned as form, 15, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned as form, 21, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned as form, 22, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned as form, 23, 1)
	LItemDraugr02Weapon1H.AddForm(ccBGSSSE001_DraugrMaceHoned as form, 24, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammer as form, 1, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammer as form, 1, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammer as form, 1, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammer as form, 1, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned as form, 12, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned as form, 13, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned as form, 14, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned as form, 15, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned as form, 15, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned as form, 21, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned as form, 22, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned as form, 23, 1)
	LItemDraugr02Weapon2H.AddForm(ccBGSSSE001_DraugrWarhammerHoned as form, 24, 1)
	Int upgradeListLength = ccBGSSSE001_BYOHAllFishingSpecificUpgradeMiscItems.GetSize()
	Int i = 0
	while i < upgradeListLength
		BYOHHouseBuildingRoom12MasterList.AddForm(ccBGSSSE001_BYOHAllFishingSpecificUpgradeMiscItems.GetAt(i))
		i += 1
	endWhile
	location playerLoc = PlayerRef.GetCurrentLocation()
	self.BuildBYOHObject(playerLoc, BYOHHouse1LocationInterior, BYOHHouseInteriorPart021WallShelvesx2_02 as form)
	self.BuildBYOHObject(playerLoc, BYOHHouse1LocationInterior, BYOHHouseInteriorPart185VampireCoffin01 as form)
	self.BuildBYOHObject(playerLoc, BYOHHouse2LocationInterior, BYOHHouseInteriorPart021WallShelvesx2_02 as form)
	self.BuildBYOHObject(playerLoc, BYOHHouse2LocationInterior, BYOHHouseInteriorPart185VampireCoffin01 as form)
	self.BuildBYOHObject(playerLoc, BYOHHouse3LocationInterior, BYOHHouseInteriorPart021WallShelvesx2_02 as form)
	self.BuildBYOHObject(playerLoc, BYOHHouse3LocationInterior, BYOHHouseInteriorPart185VampireCoffin01 as form)
	i = 0
	while i < hatcheryFishFood.length
		BYOHHouseFishContainerList.AddForm(hatcheryFishFood[i] as form)
		i += 1
	endWhile
	i = 0
	while i < hatcheryFishIngredient.length
		BYOHHouseFishContainerList.AddForm(hatcheryFishIngredient[i] as form)
		i += 1
	endWhile
endFunction

function OnInit()

	self.AddHelpArticles()
	self.RunSetupBaseGame()
	self.RunDetectDLC()
	self.RunUpdates()
endFunction

function RunDetectDLC()

	formlist survivalRawMeatList = game.GetFormFromFile(2224, "ccQDRSSE001-SurvivalMode.esl") as formlist
	if survivalRawMeatList
		Int i = 0
		while i < rawFish.length
			if !survivalRawMeatList.HasForm(rawFish[i] as form)
				survivalRawMeatList.AddForm(rawFish[i] as form)
			endIf
			i += 1
		endWhile
	endIf
endFunction
