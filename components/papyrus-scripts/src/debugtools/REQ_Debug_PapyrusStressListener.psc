ScriptName REQ_Debug_PapyrusStressListener Extends ActiveMagicEffect
{Debug tool: deliberately overloads the Papyrus VM with player actions.
The player alias of REQ_Debug_PapyrusStress carries several cloak abilities. Each cloak applies its own effect with this
script to every actor in a large radius, about once per second. Every magic effect applied to such an actor fires
OnMagicEffectApply on every instance of this script on that actor. So with N cloaks, each actor in range produces about
N^2 events per second, and each event runs a batch of actions on the player.
Start: startquest REQ_Debug_PapyrusStress
Stop:  stopquest REQ_Debug_PapyrusStress
Load:  set REQ_Debug_PapyrusStress_Repetitions to <n> (action batches per event, default 1)
NEVER save the game while the stress test is running - the queued script stacks get baked into the save.}

Actor Property PlayerRef Auto
MiscObject Property Token Auto
{weightless dummy item moved in and out of the player's inventory}
GlobalVariable Property Repetitions Auto
{number of action batches executed per received event}


Event OnMagicEffectApply(ObjectReference akCaster, MagicEffect akEffect)
	Int remaining = Repetitions.GetValueInt()
	While (remaining > 0)
		; inventory changes - also trigger OnItemAdded / OnItemRemoved on every script attached to the player
		PlayerRef.AddItem(Token, 1, True)
		PlayerRef.RemoveItem(Token, 1, True)
		; actor value changes - net zero
		PlayerRef.DamageActorValue("Stamina", 0.01)
		PlayerRef.RestoreActorValue("Stamina", 0.01)
		; frame-synced queries on the player
		PlayerRef.GetActorValue("Health")
		PlayerRef.GetItemCount(Token)
		PlayerRef.GetEquippedWeapon()
		PlayerRef.IsSneaking()
		PlayerRef.IsInCombat()
		remaining -= 1
	EndWhile
EndEvent
