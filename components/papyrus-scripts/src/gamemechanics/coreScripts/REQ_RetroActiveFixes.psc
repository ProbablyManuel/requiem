Scriptname REQ_RetroActiveFixes extends REQ_CoreScript
{this script applies one-time changes after an update to fix bugs from prior versions}

GlobalVariable Property AlcoholLevelStorage Auto

Spell Property OutOfCombatRegeneration Auto

Function initScript(Int currentVersion, Int nevVersion)
	If currentVersion > 0
		If currentVersion <= 60000 && nevVersion >= 60001
			Apply_6_0_0_to_6_0_1_fixes()
		EndIf
	EndIf
EndFunction

Function shutdownScript(Int currentVersion, Int nevVersion)
EndFunction

Function Apply_6_0_0_to_6_0_1_fixes()
	AlcoholLevelStorage.SetValue(0.0)
	Player.RemoveSpell(OutOfCombatRegeneration)
EndFunction
