Scriptname REQ_AlcoholEffectsPlayer extends REQ_CoreScript
{manages player-specific alcohol effects like blurry vision}

ImageSpaceModifier Property DrunkISM Auto
Faction Property Storage Auto
Keyword Property resistAlcoholBlur Auto

Function initScript(Int currentVersion, Int nevVersion)
    UpdatePlayerAlcoholVisionPenalties()
EndFunction

Function shutdownScript(Int currentVersion, Int nevVersion)
    DrunkISM.Remove()
EndFunction

Function UpdatePlayerAlcoholVisionPenalties()
    Float intensity = player.GetFactionRank(Storage) / 50.0

    DrunkISM.Remove()
    If (player.GetFactionRank(Storage) > 0 && !player.HasMagicEffectWithKeyword(resistAlcoholBlur))
        DrunkISM.Apply(intensity)
    EndIf
EndFunction