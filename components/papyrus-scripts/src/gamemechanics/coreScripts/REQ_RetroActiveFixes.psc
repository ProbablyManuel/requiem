Scriptname REQ_RetroActiveFixes extends REQ_CoreScript
{this script applies one-time changes after an update to fix bugs from prior versions}

Perk Property HeavyArmorMaterialBonus Auto
Perk Property LightArmorMaterialBonus Auto
Perk Property WindWalker Auto

Spell Property PlayerWerewolfAbility Auto

Function initScript(Int currentVersion, Int nevVersion)
    If currentVersion > 0
        If currentVersion <= 50100 && nevVersion >= 50101
            Apply_5_1_0_to_5_1_1_fixes()
        EndIf
        If currentVersion <= 50101 && nevVersion >= 50200
            Apply_5_1_1_to_5_2_0_fixes()
        EndIf
    EndIf
EndFunction

Function shutdownScript(Int currentVersion, Int nevVersion)
EndFunction

Function Apply_5_1_0_to_5_1_1_fixes()
    If Player.HasPerk(HeavyArmorMaterialBonus)
        Player.RemovePerk(HeavyArmorMaterialBonus)
        Player.AddPerk(HeavyArmorMaterialBonus)
    EndIf
    If Player.HasPerk(LightArmorMaterialBonus)
        Player.RemovePerk(LightArmorMaterialBonus)
        Player.AddPerk(LightArmorMaterialBonus)
    EndIf
EndFunction

Function Apply_5_1_1_to_5_2_0_fixes()
    If Player.HasSpell(PlayerWerewolfAbility)
        Player.RemoveSpell(PlayerWerewolfAbility)
        Player.AddSpell(PlayerWerewolfAbility, False)
    EndIf
    If Player.HasPerk(WindWalker)
        Player.RemovePerk(WindWalker)
        Player.AddPerk(WindWalker)
    EndIf
EndFunction