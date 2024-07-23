;BEGIN FRAGMENT CODE - Do not edit anything between this and the end comment
;NEXT FRAGMENT INDEX 6
Scriptname PRKF_DA11Cannibalism_000EE5C3 Extends Perk Hidden

;BEGIN FRAGMENT Fragment_0
Function Fragment_0(ObjectReference akTargetRef, Actor akActor)
;BEGIN CODE
akActor.StartCannibal(akTargetRef As Actor)
DA11CannibalismAbility.Cast(akActor)
DA11CannibalismAbility02.Cast(akActor)
CorpsesEaten.Mod(1.0)
NextCorpseEating.SetValue(GameDaysPassed.GetValue() + DaysBetweenCorpseEating)
;END CODE
EndFunction
;END FRAGMENT

;END FRAGMENT CODE - Do not edit anything between this and the begin comment

PlayerVampireQuestScript Property PlayerVampireQuest  Auto  

Spell Property DA11CannibalismAbility  Auto  

Spell Property DA11CannibalismAbility02  Auto  

GlobalVariable Property CorpsesEaten  Auto  

GlobalVariable Property GameDaysPassed  Auto  

GlobalVariable Property NextCorpseEating  Auto  

Float Property DaysBetweenCorpseEating = 0.33  Auto  
