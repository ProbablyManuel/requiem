Scriptname REQ_RetroActiveFixes extends REQ_CoreScript
{this script applies one-time changes after an update to fix bugs from prior versions}

Enchantment Property FortifyLockpickingBase1 Auto
Enchantment Property FortifyLockpickingBase2 Auto
Enchantment Property FortifyLockpickingBase3 Auto

Enchantment Property EnchWeaponShockDamageBase Auto

Perk Property HeavyArmorMaterialBonus Auto
Perk Property LightArmorMaterialBonus Auto

Perk Property WindWalker Auto

Perk Property AlchemicalIntellect Auto
Perk Property FortifiedMuscles Auto
Perk Property Regeneration Auto

Perk Property CheapTricks Auto
Perk Property LocksmithingLore Auto
Perk Property MasterlyLockpicking Auto
Perk Property SkeletonKey Auto

Perk Property MagicalAbsorption Auto

Perk Property QuarterstaffFocus1 Auto
Perk Property QuarterstaffFocus2 Auto
Perk Property QuarterstaffFocus3 Auto

Spell Property PlayerWerewolfAbility Auto

Spell Property AetheriumMaterialBonus Auto
Spell Property OrcishMaterialBonus Auto

Spell Property ThiefStone Auto
Spell Property TowerStone Auto
Spell Property KhajiitLockpicking Auto

Spell Property AncientKnowledge Auto

Spell Property FortitudeDuplicateAbility Auto

Function initScript(Int currentVersion, Int nevVersion)
    If currentVersion > 0
        If currentVersion <= 50100 && nevVersion >= 50101
            Apply_5_1_0_to_5_1_1_fixes()
        EndIf
        If currentVersion <= 50101 && nevVersion >= 50200
            Apply_5_1_1_to_5_2_0_fixes()
        EndIf
        If currentVersion <= 50203 && nevVersion >= 50300
            Apply_5_2_3_to_5_3_0_fixes()
        EndIf
        If currentVersion <= 50301 && nevVersion >= 50400
            Apply_5_3_1_to_5_4_0_fixes()
        EndIf
        If currentVersion <= 50401 && nevVersion >= 50402
            Apply_5_4_1_to_5_4_2_fixes()
        EndIf
        If currentVersion <= 50402 && nevVersion >= 50403
            Apply_5_4_2_to_5_4_3_fixes()
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
    If Player.HasPerk(AlchemicalIntellect)
        Player.RemovePerk(AlchemicalIntellect)
        Player.AddPerk(AlchemicalIntellect)
    EndIf
    If Player.HasPerk(FortifiedMuscles)
        Player.RemovePerk(FortifiedMuscles)
        Player.AddPerk(FortifiedMuscles)
    EndIf
    If Player.HasPerk(Regeneration)
        Player.RemovePerk(Regeneration)
        Player.AddPerk(Regeneration)
    EndIf
EndFunction

Function Apply_5_2_3_to_5_3_0_fixes()
    If Player.HasPerk(HeavyArmorMaterialBonus)
        Player.RemoveSpell(AetheriumMaterialBonus)
        Player.RemoveSpell(OrcishMaterialBonus)
        Player.RemovePerk(HeavyArmorMaterialBonus)
        Player.AddPerk(HeavyArmorMaterialBonus)
    EndIf
    If Player.HasPerk(LightArmorMaterialBonus)
        Player.RemovePerk(LightArmorMaterialBonus)
        Player.AddPerk(LightArmorMaterialBonus)
    EndIf
EndFunction

Function Apply_5_3_1_to_5_4_0_fixes()
    FortifyLockpickingBase1.SetPlayerKnows(False)
    FortifyLockpickingBase2.SetPlayerKnows(False)
    FortifyLockpickingBase3.SetPlayerKnows(False)
    If Player.HasPerk(CheapTricks)
        Player.RemovePerk(CheapTricks)
        Player.AddPerk(CheapTricks)
    EndIf
    If Player.HasPerk(LocksmithingLore)
        Player.RemovePerk(LocksmithingLore)
        Player.AddPerk(LocksmithingLore)
    EndIf
    If Player.HasPerk(MasterlyLockpicking)
        Player.RemovePerk(MasterlyLockpicking)
        Player.AddPerk(MasterlyLockpicking)
    EndIf
    If Player.HasPerk(SkeletonKey)
        Player.RemovePerk(SkeletonKey)
        Player.AddPerk(SkeletonKey)
    EndIf
    If Player.HasSpell(KhajiitLockpicking)
        Player.RemoveSpell(KhajiitLockpicking)
    EndIf
EndFunction

Function Apply_5_4_1_to_5_4_2_fixes()
    If Player.HasSpell(AncientKnowledge)
        Game.AdvanceSkill("Smithing", 3000.0)
    EndIf
    If Player.HasPerk(MagicalAbsorption)
        Player.RemovePerk(MagicalAbsorption)
        Player.AddPerk(MagicalAbsorption)
    EndIf
    If Player.HasPerk(QuarterstaffFocus1)
        Player.RemovePerk(QuarterstaffFocus1)
        Player.AddPerk(QuarterstaffFocus1)
    EndIf
    If Player.HasPerk(QuarterstaffFocus2)
        Player.RemovePerk(QuarterstaffFocus2)
        Player.AddPerk(QuarterstaffFocus2)
    EndIf
    If Player.HasPerk(QuarterstaffFocus3)
        Player.RemovePerk(QuarterstaffFocus3)
        Player.AddPerk(QuarterstaffFocus3)
    EndIf
    If Player.HasSpell(FortitudeDuplicateAbility)
        Player.RemoveSpell(FortitudeDuplicateAbility)
    EndIf
EndFunction

Function Apply_5_4_2_to_5_4_3_fixes()
    EnchWeaponShockDamageBase.SetPlayerKnows(False)
EndFunction
