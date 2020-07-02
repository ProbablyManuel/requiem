Scriptname REQ_Installation extends Quest
{helper script which makes the necessary preparations for installing Requiem upon game start}

REQ_SetupValidation Property validator Auto

GlobalVariable Property VersionSavegame Auto

REQ_VersionController Property coreScripts Auto

Spell[] Property startingSpells Auto

Quest Property NewGameCheck Auto
{Update.esm quest 1.4 is used for new game check; if Requiem has just started but this quest was stopped, it means this isn't a new game}

Event OnInit()
    If VersionSavegame.Value == -1
        If !NewGameCheck.IsStopped()
            ; it's a new game
            VersionSavegame.SetValue(0)
        Else
            ; it's not a new game and the follow-up validation will fail
            VersionSavegame.SetValue(-2)
        EndIf

        RegisterforMenu("InventoryMenu")
        RegisterforMenu("MagicMenu")
    EndIf
EndEvent

Event OnMenuClose(String Menu)
    unregisterForUpdate()
    unregisterForAllMenus()

    ; one time changes before installation
    Game.AddPerkPoints(3)
    int count = 0
    While (count < startingSpells.length)
        Game.GetPlayer().removeSpell(startingSpells[count])
        count += 1
    EndWhile

    coreScripts.upgradeVersion()
    setStage(10)
EndEvent

Event OnUpdate()
    ; don't spam the player while he cannot access menus in the Vanilla Helgen start
    If (Game.IsMenuControlsEnabled())
        setObjectiveDisplayed(0, true, true)
    EndIf
EndEvent