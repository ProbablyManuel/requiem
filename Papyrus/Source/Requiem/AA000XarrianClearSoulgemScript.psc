Scriptname AA000XarrianClearSoulgemScript extends activemagiceffect  

SoulGem Property SoulgemPity  Auto  
SoulGem Property SoulGemLesser  Auto  
SoulGem Property SoulGemCommon  Auto  
SoulGem Property SoulGemGreater  Auto  
SoulGem Property SoulGemGrand  Auto  
SoulGem Property SoulGemBlack  Auto  
SoulGem Property SoulgemBlackTomato  Auto  
SoulGem Property SoulgemAzuraWhite  Auto  
SoulGem Property SoulgemAzuraBlack  Auto  

Int Property IntSoulgemPity  Auto  
Int Property IntSoulGemLesser  Auto  
Int Property IntSoulGemCommon  Auto  
Int Property IntSoulGemGreater  Auto  
Int Property IntSoulGemGrand  Auto  
Int Property IntSoulGemBlack  Auto  
Int Property IntSoulgemBlackTomato  Auto  
Int Property IntSoulgemAzuraWhite  Auto  
Int Property IntSoulgemAzuraBlack  Auto  


Event OnEffectStart(Actor akTarget, Actor akCaster)

IntSoulgemPity                 = akCaster.GetItemCount(SoulgemPity)
IntSoulGemLesser            = akCaster.GetItemCount(SoulgemLesser)
IntSoulGemCommon         = akCaster.GetItemCount(SoulgemCommon)
IntSoulGemGreater          = akCaster.GetItemCount(SoulgemGreater)
IntSoulGemGrand            = akCaster.GetItemCount(SoulgemGrand)
IntSoulGemBlack              = akCaster.GetItemCount(SoulgemBlack)
IntSoulgemBlackTomato  = akCaster.GetItemCount(SoulgemBlacktomato)
IntSoulgemAzuraWhite   = akCaster.GetItemCount(SoulgemAzuraWhite)
IntSoulgemAzuraBlack    = akCaster.GetItemCount(SoulgemAzuraBlack)


	akCaster.RemoveItem(SoulgemPity, IntSoulgemPity, true)
	akCaster.RemoveItem(SoulgemLesser, IntSoulgemLesser, true)
	akCaster.RemoveItem(SoulgemCommon, IntSoulgemCommon, true)
	akCaster.RemoveItem(SoulgemGreater, IntSoulgemGreater, true)
	akCaster.RemoveItem(SoulgemGrand, IntSoulgemGrand, true)
	akCaster.RemoveItem(SoulgemBlack, IntSoulgemBlack, true)
	akCaster.RemoveItem(SoulgemBlackTomato, IntSoulgemBlackTomato, true)
	akCaster.RemoveItem(SoulgemAzuraWhite, IntSoulgemAzuraWhite, true)
	akCaster.RemoveItem(SoulgemAzuraBlack, IntSoulgemAzuraBlack, true)

	akCaster.AddItem(SoulgemPity, IntSoulgemPity, true)
	akCaster.AddItem(SoulgemLesser, IntSoulgemLesser, true)
	akCaster.AddItem(SoulgemCommon, IntSoulgemCommon, true)
	akCaster.AddItem(SoulgemGreater, IntSoulgemGreater, true)
	akCaster.AddItem(SoulgemGrand, IntSoulgemGrand, true)
	akCaster.AddItem(SoulgemBlack, IntSoulgemBlack, true)
	akCaster.AddItem(SoulgemBlackTomato, IntSoulgemBlackTomato, true)
	akCaster.AddItem(SoulgemAzuraWhite, IntSoulgemAzuraWhite, true)
	akCaster.AddItem(SoulgemAzuraBlack, IntSoulgemAzuraBlack, true)




EndEvent
