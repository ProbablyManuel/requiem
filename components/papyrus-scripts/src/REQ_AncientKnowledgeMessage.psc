ScriptName REQ_AncientKnowledgeMessage Extends ActiveMagicEffect

Message Property AncientKnowledgeMessage Auto

Spell Property ThisSpell Auto

Event OnEffectStart(Actor akTarget, Actor akCaster)
	AncientKnowledgeMessage.Show()
	akTarget.RemoveSpell(ThisSpell)
EndEvent
