Scriptname TempleBlessingScript extends ObjectReference Conditional  

Spell Property TempleBlessing  Auto  

Event OnActivate(ObjectReference akActionRef)
    TempleDispel.Cast(akActionRef, akActionRef)
    Utility.Wait(0.25)
	TempleBlessing.Cast(akActionRef, akActionRef)
	if akActionRef == Game.GetPlayer()
		AltarRemoveMsg.Show()
		BlessingMessage.Show()
	endif

EndEvent

Message Property BlessingMessage  Auto

Message Property AltarRemoveMsg  Auto

Spell Property TempleDispel Auto