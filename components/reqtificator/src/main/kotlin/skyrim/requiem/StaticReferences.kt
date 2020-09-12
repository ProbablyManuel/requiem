package skyrim.requiem

import skyproc.FormID
import skyproc.ModListing

object StaticReferences {
    val skyrim = ModListing("Skyrim.esm")
    val requiem = ModListing("Requiem.esp")

    object Keywords {
        val armorBody = FormID("06C0EC", skyrim)
        val armorFeet = FormID("06C0ED", skyrim)
        val armorHands = FormID("06C0EF", skyrim)
        val armorHead = FormID("06C0EE", skyrim)
        val armorShield = FormID("0965B2", skyrim)

        val armorHeavy = FormID("06BBD2", skyrim)
        val armorLight = FormID("06BBD3", skyrim)

        val weaponBowHeavy = FormID("9F9914", requiem)
        val weaponCrossbowHeavy = FormID("899DBE", requiem)

        val noArmorRatingRescaling = FormID("AD3B2B", requiem)
        val noDamageRescaling = FormID("AD3B2D", requiem)
        val noWeaponReachRescaling = FormID("AD3B2E", requiem)
        val noWeaponSpeedRescaling = FormID("AD3B2F", requiem)

        val alreadyReqtified = FormID("8F08B5", requiem)
    }

    object Actors {
        val player = FormID("000007", skyrim)
    }

    object GlobalVariables {
        val versionPlugin = FormID("2F389F", requiem)
        val versionPatch = FormID("973D69", requiem)
    }
}