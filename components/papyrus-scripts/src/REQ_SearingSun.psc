ScriptName REQ_SearingSun Extends ActiveMagicEffect

Spell Property SearingSunDamage Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	ApplySunDamage(akTarget)
	RegisterForUpdate(60.0)
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.DispelSpell(SearingSunDamage)
EndEvent

Event OnUpdate()
	ApplySunDamage(GetTargetActor())
EndEvent

Function ApplySunDamage(Actor akTarget)
	Float Damage = GetMagnitude() * CalculateWeatherFactor()
	SearingSunDamage.SetNthEffectMagnitude(0, Damage)
	SearingSunDamage.SetNthEffectMagnitude(1, Damage)
	SearingSunDamage.SetNthEffectMagnitude(2, Damage)
	SearingSunDamage.Cast(akTarget, akTarget)
EndFunction

Float Function CalculateWeatherFactor()
	Float SunDamage = Weather.GetCurrentWeather().GetSunDamage()
	Float WeatherFactor = SunDamage

	; Reduce damage at dawn/dusk (5:00-7:30 and 17:30-19:00) because the sun
	; is weaker during those hours, but the weather parameters don't reflect that
	Float CurrentGameTime = Utility.GetCurrentGameTime()
	Float GameHour = (CurrentGameTime - Math.Floor(CurrentGameTime)) * 24
	If GameHour < 7.5 || GameHour > 17.5
		WeatherFactor *= 0.5
	EndIf

	Return WeatherFactor
EndFunction
