ScriptName REQ_SearingSun Extends ActiveMagicEffect


Spell Property SearingSunDamage Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	ApplySunDamage()
	RegisterForUpdate(20.0)
EndEvent

Event OnEffectFinish(Actor akTarget, Actor akCaster)
	akTarget.DispelSpell(SearingSunDamage)
EndEvent

Event OnUpdate()
	ApplySunDamage()
EndEvent

Function ApplySunDamage()
	Actor Target = GetTargetActor()
	Float WeatherFactor = CalculateWeatherFactor()
	Float BaseHealth = Target.GetBaseActorValue("Health")
	Float Damage = BaseHealth * GetMagnitude() * WeatherFactor
	SearingSunDamage.SetNthEffectMagnitude(0, Damage)
	SearingSunDamage.SetNthEffectMagnitude(1, Damage)
	SearingSunDamage.SetNthEffectMagnitude(2, Damage)
	SearingSunDamage.Cast(Target, Target)
EndFunction

Float Function CalculateWeatherFactor()
	Weather CurrentWeather = Weather.GetCurrentWeather()
	Float SunDamage = CurrentWeather.GetSunDamage()
	Float SunGlare = CurrentWeather.GetSunGlare()
	Float WeatherFactor = SunDamage * SunGlare

	;Reduce damage at dawn/dusk (5-7:30am and from 5:30pm onwards) by time-dependant increasing/decreasing magnitude.
	;Since the sun is weaker during those hours but the weather parameters don't reflect that.
	Float CurrentGameTime = Utility.GetCurrentGameTime()
	Float TimeOfDay = CurrentGameTime - Math.Floor(CurrentGameTime)
	If TimeOfDay <= 0.33
		WeatherFactor *= TimeOfDay
	ElseIf TimeOfDay >= 0.73
		WeatherFactor *= 1.0 - TimeOfDay
	EndIf
	;Reduce damage by 25% if there's close fog.
	Float Fog = CurrentWeather.GetFogDistance(True, 0)
	If Fog != 0.0
		WeatherFactor *= 0.25
	EndIf
	Debug.Notification("Sun Damage: " + SunDamage + " Glare: " + SunGlare + " Fog: " + Fog + " WeatherFactor: " + WeatherFactor)
	Return WeatherFactor
EndFunction
