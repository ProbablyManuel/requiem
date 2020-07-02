Scriptname REQ_StaminaConsumption extends REQ_CoreScript
{wait for keypress events signaling player actions like jumping or attacking and emulate stamina usage}

import Input

Spell Property AttackCost Auto
Spell Property BlockCost Auto
Spell Property JumpCost Auto
Spell Property Dodge Auto
MagicEffect Property StaminaDrain Auto
MagicEffect Property DodgeBoost Auto

Spell Property BullRushCone Auto
Spell Property HorseChargeCone Auto

Spell Cone = None

Int attack_right
Int attack_left
Int jump
Int sprint
Int[] move

; bull rush/trample related internal variables
Actor sprinter
Float horsemass = 0.0

Function RegisterKeys(Bool reregister)
	UnregisterForAllKeys()
	UnregisterForAllMenus()
	attack_right = GetMappedKey("Right Attack/Block")
	attack_left = GetMappedKey("Left Attack/Block")
	jump = GetMappedKey("Jump")
	sprint = GetMappedKey("Sprint")
	move[0] = GetMappedKey("Strafe Left")
	move[1] = GetMappedKey("Strafe Right")
	move[2] = GetMappedKey("Back")
	move[3] = GetMappedKey("Forward")
	If (reregister)
		RegisterForKey(attack_right)
		RegisterForKey(attack_left)
		RegisterForKey(jump)
		RegisterForKey(sprint)
		RegisterForMenu("Cursor Menu")
	EndIf
EndFunction

Event OnKeyDown(Int KeyCode)
	If (!Utility.IsInMenuMode())
		If (KeyCode == sprint) ;Dodge moves and charge attacks
			GotoState("Working")
			If (Player.IsSprinting() || Player.IsOnMount())
				UnregisterForUpdate()
				If (Player.IsOnMount())
					Cone = HorseChargeCone
					sprinter = Game.FindClosestActor(Player.X, Player.Y, Player.Z - 200.0, 190.0) ;use the horse as sprinter
				Else
					Cone = BullRushCone
					sprinter = Player
				EndIf
				RegisterForSingleUpdate(2)
			ElseIf (!Player.HasMagicEffect(DodgeBoost) && Player.GetAV("Stamina") >= 15.0)
				Dodge.Cast(Player,Player)
			EndIf
			GotoState("")
		ElseIf (KeyCode == Jump)
			If (!Player.IsSwimming() && !Player.IsOnMount())
				Player.AddSpell(Jumpcost, False)
			EndIf
		ElseIf (KeyCode == attack_right && player.GetEquippedItemType(1) == 7)
			Player.AddSpell(AttackCost, False)
		ElseIf KeyCode == attack_left  && player.GetAnimationVariableBool("Isblocking") ; RRO-207
			Player.AddSpell(BlockCost, False)
		EndIf
	EndIf
EndEvent

State Working
	Event OnKeyDown(Int KeyCode)
	EndEvent
EndState

Event OnUpdate()
	If (sprinter.IsSprinting() && !sprinter.IsGhost())
		Cone.Cast(Player, None)
		RegisterForSingleUpdate(0.1)
	Else
		sprinter = None
		Cone = None
	EndIf
EndEvent

Event OnMenuClose(String MenuName)
	RegisterKeys(True)
EndEvent

Event OnPlayerLoadGame()
    parent.OnPlayerLoadGame()
	RegisterKeys(True)
EndEvent

Function shutdownScript(Int currentVersion, Int nevVersion)
	RegisterKeys(False)
EndFunction

Function initScript(Int currentVersion, Int nevVersion)
	move = new Int[4]
	RegisterKeys(True)
EndFunction
