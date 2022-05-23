Scriptname REQ_RetroActiveFixes extends REQ_CoreScript
{this script applies one-time changes after an update to fix bugs from prior versions}

GlobalVariable Property VersionPlugin Auto
GlobalVariable Property VersionSavegame Auto

Perk Property HeavyArmorMaterialBonus Auto
Perk Property LightArmorMaterialBonus Auto

Function initScript(Int currentVersion, Int nevVersion)
    If VersionSavegame.GetValue() > 0
        Apply_5_1_0_to_5_1_1_fixes()
    EndIf
EndFunction

Function shutdownScript(Int currentVersion, Int nevVersion)
EndFunction

Function Apply_5_1_0_to_5_1_1_fixes()
    If VersionSavegame.GetValue() <= 50100 && VersionPlugin.GetValue() >= 50101
        If Player.HasPerk(HeavyArmorMaterialBonus)
            Player.RemovePerk(HeavyArmorMaterialBonus)
            Player.AddPerk(HeavyArmorMaterialBonus)
        EndIf
        If Player.HasPerk(LightArmorMaterialBonus)
            Player.RemovePerk(LightArmorMaterialBonus)
            Player.AddPerk(LightArmorMaterialBonus)
        EndIf
    EndIf
EndFunction