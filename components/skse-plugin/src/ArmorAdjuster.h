#pragma once

namespace REQ
{
	void AdjustArmorRating()
	{
		constexpr RE::FormID ArmorCuirass = 0x0006C0EC;
		constexpr RE::FormID ArmorBoots = 0x0006C0ED;
		constexpr RE::FormID ArmorHelmet = 0x0006C0EE;
		constexpr RE::FormID ArmorGauntlets = 0x0006C0EF;
		constexpr RE::FormID ArmorShield = 0x000965B2;
		constexpr RE::FormID ArmorHeavy = 0x0006BBD2;
		constexpr RE::FormID ArmorLight = 0x0006BBD3;
		if (const auto dataHandler = RE::TESDataHandler::GetSingleton(); dataHandler) {
			const auto NoDamageRescale = dataHandler->LookupForm(0xAD3B2D, "Requiem.esp")->GetFormID();
			for (const auto& armor : dataHandler->GetFormArray<RE::TESObjectARMO>()) {
				if (armor && !armor->HasKeyword(NoDamageRescale)) {
					if (armor->HasKeyword(ArmorHeavy)) {
						if (armor->HasKeyword(ArmorCuirass) && armor->armorRating < 7400) {
							armor->armorRating = armor->armorRating * 11 / 2 + 5500;
						} else if (armor->HasKeyword(ArmorBoots) && armor->armorRating < 2700) {
							armor->armorRating = armor->armorRating * 11 / 2 + 1500;
						} else if (armor->HasKeyword(ArmorGauntlets) && armor->armorRating < 2700) {
							armor->armorRating = armor->armorRating * 11 / 2 + 1500;
						} else if (armor->HasKeyword(ArmorHelmet) && armor->armorRating < 3500) {
							armor->armorRating = armor->armorRating * 11 / 2 + 1500;
						} else if (armor->HasKeyword(ArmorShield) && armor->armorRating < 5400) {
							armor->armorRating = armor->armorRating * 11 / 2 + 1500;
						}
					} else if (armor->HasKeyword(ArmorLight)) {
						if (armor->HasKeyword(ArmorCuirass) && armor->armorRating < 6200) {
							armor->armorRating = armor->armorRating * 11 / 4 + 5500;
						} else if (armor->HasKeyword(ArmorBoots) && armor->armorRating < 1800) {
							armor->armorRating = armor->armorRating * 11 / 4 + 1500;
						} else if (armor->HasKeyword(ArmorGauntlets) && armor->armorRating < 1800) {
							armor->armorRating = armor->armorRating * 11 / 4 + 1500;
						} else if (armor->HasKeyword(ArmorHelmet) && armor->armorRating < 2600) {
							armor->armorRating = armor->armorRating * 11 / 4 + 1500;
						} else if (armor->HasKeyword(ArmorShield) && armor->armorRating < 4400) {
							armor->armorRating = armor->armorRating * 11 / 4 + 1500;
						}
					}
				}
			}
		}
	}
}
