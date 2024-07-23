ScriptName REQ_ShowMessageOnEffectStart Extends ActiveMagicEffect

Message Property Msg Auto


Event OnEffectStart(Actor akTarget, Actor akCaster)
	Msg.Show()
EndEvent
