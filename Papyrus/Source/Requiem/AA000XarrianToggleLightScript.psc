Scriptname AA000XarrianToggleLightScript extends activemagiceffect  

MagicEffect Property LightSelf01  Auto  

SPELL Property LightSpell01  Auto  
SPELL Property LightSpell02  Auto  




Event OnEffectStart(actor caster, actor target)

	Target.DispelSpell(LightSpell01)
	Target.DispelSpell(LightSpell02)


EndEvent


