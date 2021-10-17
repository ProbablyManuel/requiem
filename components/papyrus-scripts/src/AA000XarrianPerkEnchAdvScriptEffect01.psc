Scriptname AA000XarrianPerkEnchAdvScriptEffect01 extends activemagiceffect  

WEAPON Property IronDagger  Auto  

SoulGem Property SoulGemGrandFilled  Auto  


		Event OnEffectStart(Actor Target, Actor Caster)

			caster.removeitem(IronDagger, 5)
			caster.removeitem(SoulgemGrandFilled, 3)

			caster.additem(GainedWeapon01, 1)
			caster.additem(GainedWeapon02, 1)
			caster.additem(GainedWeapon03, 1)
			caster.additem(GainedWeapon04, 1)
			caster.additem(GainedWeapon05, 1)

			game.advanceskill("Enchantment" , 750)



		EndEvent
WEAPON Property GainedWeapon01  Auto  
WEAPON Property GainedWeapon02  Auto  
WEAPON Property GainedWeapon03  Auto  
WEAPON Property GainedWeapon04  Auto  
WEAPON Property GainedWeapon05  Auto  