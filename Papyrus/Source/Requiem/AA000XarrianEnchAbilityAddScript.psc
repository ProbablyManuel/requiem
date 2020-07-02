Scriptname AA000XarrianEnchAbilityAddScript extends activemagiceffect  

SPELL Property Ability  Auto  


Event OnEffectStart(Actor Caster, Actor Target)

	Target.AddSpell(Ability, false)

EndEvent


Event OnEffectFinish(Actor Caster, Actor Target)

	Target.RemoveSpell(Ability)
       Target.DispelSpell(LightPower)

EndEvent
SPELL Property LightPower  Auto  
