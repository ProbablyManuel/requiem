Scriptname UDBP_Retroactive212Script extends Quest  

Quest Property MQ101 Auto
UDBP_VersionTrackingScript Property UDBPTracking Auto

Quest Property DLC2MQ06 Auto

Quest Property DLC2SkaalVillageFreeform2 Auto
ReferenceAlias Property Edla Auto

Quest Property DLC2RR03Intro Auto

LeveledItem Property LItemBanditWeaponArrows Auto
Ammo Property DLC2NordicArrow Auto

Function Process()
	;Abort this if chargen is not sufficiently advanced. We do not have any bugfixes for the cart ride scene in Helgen.
	if( MQ101.GetStage() < 70 )
		UDBPTracking.LastVersion = 212
		;USLEEP 300 - Stop the version tracking for UDBP now.
		UDBPTracking.Stop()
		Stop()
		Return
	EndIf
	
	;Bug #19144 - Defeting Miraak doesn't add to quests completed.
	if( DLC2MQ06.GetStage() >= 600 )
		Game.IncrementStat( "Questlines Completed" )
	EndIf

	;Bug #19087 - Edla does not advance relationship level.
	if( DLC2SkaalVillageFreeform2.GetStageDone(200) == 1 )
		Edla.GetActorReference().SetRelationshipRank(Game.GetPlayer(), 1)
	EndIf

	;Bug #19055 - DLC2RR03Intro stuck objective
	if( DLC2RR03Intro.GetStageDone(25) == 1 )
		if( DLC2RR03Intro.IsObjectiveDisplayed(20) )
			DLC2RR03Intro.SetObjectiveDisplayed(20,0)
		EndIf
	EndIf

	debug.trace( "UDBP 2.1.2 Retroactive Updates Complete" )
	UDBPTracking.LastVersion = 212

	;USLEEP 300 - Stop the version tracking for UDBP now.
	UDBPTracking.Stop()
	Stop()
EndFunction
