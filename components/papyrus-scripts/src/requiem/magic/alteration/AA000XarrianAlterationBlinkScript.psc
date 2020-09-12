Scriptname AA000XarrianAlterationBlinkScript extends ObjectReference


Event OnLoad()
	
	Screeneffect.Apply()
	Game.DisablePlayerControls()
	utility.wait(0.5)
	Player.MoveTo(Self)	
	self.disable()
	self.delete()
	utility.wait(0.5)
	Screeneffect.Remove()
	Game.EnablePlayerControls()
	


EndEvent



Actor Property Player  Auto  

SPELL Property Darkness  Auto  

ObjectReference Property ActivatorCaster  Auto  

ImageSpaceModifier Property Screeneffect  Auto  

ImageSpaceModifier Property Screeneffect02  Auto  
