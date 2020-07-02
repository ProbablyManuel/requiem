package skyrim.requiem.records

import skyproc.FormID
import skyproc.genenums.ArmorType
import skyrim.requiem.Armor
import skyrim.requiem.StaticReferences
import skyrim.requiem.fptools.Option
import skyrim.requiem.keywords

enum class ArmorPart(val keyword: FormID) {
    Body(StaticReferences.Keywords.armorBody),
    Feet(StaticReferences.Keywords.armorFeet),
    Hands(StaticReferences.Keywords.armorHands),
    Head(StaticReferences.Keywords.armorHead),
    Shield(StaticReferences.Keywords.armorShield);
}

val Armor.armorType: Option<ArmorType>
    get() = Option(this.bodyTemplate.getArmorType(skyproc.BodyTemplate.BodyTemplateType.Biped))

val Armor.armorPart: Option<ArmorPart>
    get() = Option(ArmorPart.values().find { it.keyword in this.keywords })