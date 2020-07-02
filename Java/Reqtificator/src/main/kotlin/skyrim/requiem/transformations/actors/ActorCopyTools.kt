package skyrim.requiem.transformations.actors

import skyproc.NPC_
import skyproc.RGB
import skyproc.RGBA
import skyrim.requiem.Actor

fun Actor.copyAttackdata(from: Actor): Actor {
    set(NPC_.TemplateFlag.USE_ATTACK_DATA, false)
    attackDataRace = from.attackDataRace
    clearAttackPackages()
    for (pack in from.attackPackages) {
        addAttackPackage(pack)
    }
    return this
}

fun Actor.copyTraits(from: Actor): Actor {
    set(NPC_.TemplateFlag.USE_TRAITS, false)
    // Traits tab
    race = from.race
    set(NPC_.NPCFlag.Female, from.get(NPC_.NPCFlag.Female))
    skin = from.skin
    height = from.height
    weight = from.weight
    farAwayModelSkin = from.farAwayModelSkin
    farAwayModelDistance = from.farAwayModelDistance
    voiceType = from.voiceType
    set(
        NPC_.NPCStat.DISPOSITION_BASE,
        from.get(NPC_.NPCStat.DISPOSITION_BASE)
    )
    deathItem = from.deathItem
    set(
        NPC_.NPCFlag.OppositeGenderAnims,
        from.get(NPC_.NPCFlag.OppositeGenderAnims)
    )
    // Sounds tab
    soundVolume = from.soundVolume
    audioTemplate = from.audioTemplate
    clearSoundPackages()
    for (pack in from.sounds) {
        addSoundPackage(pack)
    }
    // Character Gen Parts tab
    featureSet = from.featureSet
    clearHeadParts()
    hairColor = from.hairColor
    for (part in from.headParts) {
        addHeadPart(part)
    }
    clearTinting()
    for (tint in from.tinting) {
        val clone = NPC_.TintLayer(tint.index)
        for (color in RGBA.values()) {
            clone.setColor(color, tint.getColor(color))
        }
        clone.interpolation = tint.interpolation
        clone.preset = tint.preset
        addTinting(clone)
    }
    for (color in RGB.values()) {
        setFaceTint(color, from.getFaceTint(color))
    }
    // Character Gen Morphs tab
    eyePreset = from.eyePreset
    nosePreset = from.nosePreset
    mouthPreset = from.mouthPreset
    for (part in NPC_.FacePart.values()) {
        setFaceValue(part, from.getFaceValue(part))
    }
    return this
}