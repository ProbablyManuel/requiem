ScriptName REQ_CastSpellOnEffectStart Extends ActiveMagicEffect
{Cast a spell when this magic effect becomes active}

Spell Property SpellToCast Auto

Event OnEffectStart(Actor akTarget, Actor akCaster)
	SpellToCast.Cast(akTarget)
EndEvent
