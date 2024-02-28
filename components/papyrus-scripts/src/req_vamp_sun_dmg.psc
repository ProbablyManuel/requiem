scriptName req_vamp_sun_dmg extends ActiveMagicEffect

Actor target
Weather currentw
int update_time
float base_health
float base_stamina
float base_magicka
float dmg
float sundmg
float sunstrength
float fog
float magnitude

Event OnEffectStart(actor trgt, actor caster)
	self.RegisterForUpdate(1.5)
	magnitude = self.GetMagnitude()
	target = trgt
	;Sun dmg visual effect and message notification can be added here, and conditioned to trigger when there's actual sun damage being applied
	;I didn't touch it because it is started from the vampire script and I don't want to edit that.
EndEvent

Event OnEffectFinish(actor trgt, actor caster)
	self.UnregisterForUpdate()
	target.RestoreActorValue("HealRateMult", 2147483647.0)
	target.RestoreActorValue("StaminaRateMult", 2147483647.0)
	target.RestoreActorValue("MagickaRateMult", 2147483647.0)
EndEvent

Event OnUpdate()
	if (!update_time)
		updateWeatherParams()
		update_time = 20
	EndIf
	update_time -= 1
	target.DamageActorValue("Health", dmg)
	target.DamageActorValue("Magicka",dmg)
	target.DamageActorValue("Stamina", dmg)
EndEvent

Function updateWeatherParams()
	currentw = Weather.GetCurrentWeather()
	sundmg = currentw.GetSunDamage()
	sunstrength = currentw.GetSunGlare()
	base_health = target.GetBaseActorValue("Health")
	base_stamina = target.GetBaseActorValue("Stamina")
	base_magicka = target.GetBaseActorValue("Magicka")
	dmg = base_health * magnitude * sundmg * sunstrength
	Float time = utility.GetCurrentGameTime()
	
	;Reduce damage at dawn/dusk (5-7:30am and from 5:30pm onwards) by time-dependant increasing/decreasing magnitude.
	;Since the sun is weaker during those hours but the weather parameters don't reflect that.
	time -= time as int
	if (time <= 0.33)
		dmg *= time
	elseif (time >= 0.73)
		dmg *= 1.0 - time
	endif
	;Reduce damage by 25% if there's close fog.
	fog = currentw.GetFogDistance(true, 0)
	if (dmg && fog)
		dmg *= 0.25
	endif
	target.DamageActorValue("HealRateMult", dmg * 10)
	target.DamageActorValue("StaminaRateMult", dmg * 10)
	target.DamageActorValue("MagickaRateMult", dmg * 10)
	ConsoleUtil.PrintMessage("Sun Damage: " + sundmg + " Glare: " + sunstrength + " fog: " + fog + " mag: " + magnitude + " dmg: " + dmg)
EndFunction