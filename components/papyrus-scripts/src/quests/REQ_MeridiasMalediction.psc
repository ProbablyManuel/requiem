Scriptname REQ_MeridiasMalediction extends ActiveMagicEffect
{burns vampires who dare to put their hands on Dawnbreaker or Meridia's Beacon,
this effect is used by the vampirism ability to catch vampires who already have
either item before turning into a vampire}

Weapon Property dawnbreaker Auto
MiscObject Property beacon Auto
Spell Property vampirism Auto

Spell Property malediction Auto

Event OnEffectStart(Actor akTarget, Actor akCaster)
    If (akTarget.GetIteMCount(dawnbreaker) > 0 || akTarget.GetItemCount(beacon) > 0)
        akTarget.addSpell(malediction, false)
    EndIf
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
    Utility.Wait(1.0)
    If (!akTarget.HasSpell(vampirism))
        akTarget.removeSpell(malediction)
    EndIf
EndEvent
