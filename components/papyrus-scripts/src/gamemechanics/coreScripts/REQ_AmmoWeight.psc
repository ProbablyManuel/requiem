Scriptname REQ_AmmoWeight extends REQ_CoreScript

Keyword[] Property ammoWeightIdentifiers Auto
{keyword identifier for a weight class, maps to ammoWeightDummies with the same array index}

Weapon[] Property ammoWeightDummies Auto
{hidden weight dummy item for a weight class, maps to ammoWeightIdentifiers with the same array index}

Keyword Property weightlessAmmo Auto
{keyword identifier for ammunition that is treated as weightless}

Int Property defaultWeightIndex Auto
{index from ammoWeightDummies which is used for any ammunition without a known weight keyword}

MiscObject Property displayQuiver Auto
{a quest item added to the player's inventory to display the total ammunition weight}

ObjectReference Property hideQuiverPlace Auto
{container where the quiver can be hidden when the player has no ammunition in their inventory}

Int Function getWeightIndexForObject(Form object)
    If (!object)
        Return -1
    EndIf

    Ammo ammunition = object as Ammo

    If (ammunition.hasKeyword(weightlessAmmo))
        Return -1
    EndIf

    Int i = ammoWeightIdentifiers.length
    While (i)
        i - =1
        If ammunition.hasKeyword(ammoWeightIdentifiers[i])
            Return i
        EndIf
    EndWhile

    ; couldn't find the weight, so
    Return defaultWeightIndex
EndFunction

Event OnItemAdded(Form akBaseItem, int aiItemCount, ObjectReference akItemReference, ObjectReference akSourceContainer)
    If !aiItemCount
        Return
    EndIf

    Int idx = getWeightIndexForObject(akBaseItem)
    If idx == -1
        Return
    EndIf

    Weapon ammunition = ammoWeightDummies[idx]
    If !ammunition
        Return
    EndIf

    player.addItem(ammunition, aiItemCount, true)

    displayQuiver.setWeight(displayQuiver.getWeight() + (ammunition.getWeight() * aiItemCount))

    If !player.getItemCount(displayQuiver)
        player.addItem(displayQuiver, 1, true, hideQuiverPlace)
    EndIf
EndEvent

Event OnItemRemoved(Form akBaseItem, int aiItemCount, ObjectReference akItemReference, ObjectReference akDestContainer)
    If !aiItemCount
        Return
    EndIf

    Int idx = getWeightIndexForObject(akBaseItem)
    If idx == -1
        Return
    EndIf

    Weapon ammunition = ammoWeightDummies[idx]
    If !ammunition
        Return
    EndIf

    player.removeItem(ammunition, aiItemCount, true)

    Float newWeight = displayQuiver.getWeight() - (ammunition.getWeight() * aiItemCount)
    If !newWeight
        player.removeItem(displayQuiver, 1, true, hideQuiverPlace)
    Else
        displayQuiver.setWeight(newWeight)
    EndIf

    If !player.getItemCount(displayQuiver)
        player.addItem(displayQuiver, 1, true, hideQuiverPlace)
    EndIf
EndEvent

Function initScript(Int currentVersion, Int nevVersion)
    ; set the default weight index?
    ; init display quiver?
    ; init the display quiver hide place?
EndFunction

Function shutdownScript(Int currentVersion, Int nevVersion)
    ;
EndFunction
