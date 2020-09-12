package skyrim.requiem.records

import skyproc.WEAP
import skyrim.requiem.Weapon
import skyrim.requiem.fptools.Option

val Weapon.weaponClass: Option<WEAP.WeaponType>
    get() = Option(this.weaponType)