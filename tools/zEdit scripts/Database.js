module.exports = {
	ArmorBySetAndPart: function(set, part) {
		set = set.toLowerCase()
		part = part.toLowerCase()
	
		const skyrim = xelib.FileByName("Skyrim.esm");

		switch (set) {
			case "ebony":
				switch (part) {
					case "boots": return GetRecord(skyrim, 0x013960);
					case "cuirass": return GetRecord(skyrim, 0x013961);
					case "gauntlets": return GetRecord(skyrim, 0x013962);
					case "helmet": return GetRecord(skyrim, 0x013963);
					case "shield": return GetRecord(skyrim, 0x013964);
				}
			case "elven":
				switch (part) {
					case "boots": return GetRecord(skyrim, 0x01391A);
					case "cuirass": return GetRecord(skyrim, 0x0896A3);
					case "gauntlets": return GetRecord(skyrim, 0x01391C);
					case "helmet": return GetRecord(skyrim, 0x01391D);
					case "shield": return GetRecord(skyrim, 0x01391E);
				}
			case "glass":
				switch (part) {
					case "boots": return GetRecord(skyrim, 0x013938);
					case "cuirass": return GetRecord(skyrim, 0x013939);
					case "gauntlets": return GetRecord(skyrim, 0x01393A);
					case "helmet": return GetRecord(skyrim, 0x01393B);
					case "shield": return GetRecord(skyrim, 0x01393C);
				}
			case "hide":
				switch (part) {
					case "boots": return GetRecord(skyrim, 0x013910);
					case "cuirass": return GetRecord(skyrim, 0x013911);
					case "gauntlets": return GetRecord(skyrim, 0x013912);
					case "helmet": return GetRecord(skyrim, 0x013913);
					case "shield": return GetRecord(skyrim, 0x013914);
				}
			case "iron":
				switch (part) {
					case "boots": return GetRecord(skyrim, 0x012E4B);
					case "cuirass": return GetRecord(skyrim, 0x012E49);
					case "gauntlets": return GetRecord(skyrim, 0x012E46);
					case "helmet": return GetRecord(skyrim, 0x012E4D);
					case "shield": return GetRecord(skyrim, 0x012EB6);
				}
			case "leather":
				switch (part) {
					case "boots": return GetRecord(skyrim, 0x013920);
					case "cuirass": return GetRecord(skyrim, 0x03619E);
					case "gauntlets": return GetRecord(skyrim, 0x013921);
					case "helmet": return GetRecord(skyrim, 0x013922);
				}
			case "scaled":
				switch (part) {
					case "boots": return GetRecord(skyrim, 0x01B39F);
					case "cuirass": return GetRecord(skyrim, 0x01B3A3);
					case "gauntlets": return GetRecord(skyrim, 0x01B3A0);
					case "helmet": return GetRecord(skyrim, 0x01B3A1);
				}
			case "steel":
				switch (part) {
					case "boots": return GetRecord(skyrim, 0x013951);
					case "cuirass": return GetRecord(skyrim, 0x013952);
					case "gauntlets": return GetRecord(skyrim, 0x013953);
					case "helmet": return GetRecord(skyrim, 0x013954);
					case "shield": return GetRecord(skyrim, 0x013955);
				}
			case "steelplate":
				switch (part) {
					case "boots": return GetRecord(skyrim, 0x01395B);
					case "cuirass": return GetRecord(skyrim, 0x01395C);
					case "gauntlets": return GetRecord(skyrim, 0x01395D);
					case "helmet": return GetRecord(skyrim, 0x01395E);
				}
			// case "":
			// 	switch (part) {
			// 		case "boots": return GetRecord(skyrim, 0x);
			// 		case "cuirass": return GetRecord(skyrim, 0x);
			// 		case "gauntlets": return GetRecord(skyrim, 0x);
			// 		case "helmet": return GetRecord(skyrim, 0x);
			// 		case "shield": return GetRecord(skyrim, 0x);
			// }
			default: return 0;
		}
	},

	WeaponBySetAndType: function(set, type) {
		set = set.toLowerCase()
		type = type.toLowerCase()
	
		const skyrim = xelib.FileByName("Skyrim.esm");
		const dawnguard = xelib.FileByName("Dawnguard.esm");
		const dragonborn = xelib.FileByName("Dragonborn.esm");

		switch (set) {
			case "daedric":
				switch (type) {
					case "battleaxe": return GetRecord(skyrim, 0x0139B4);
					case "bow": return GetRecord(skyrim, 0x0139B5);
					case "dagger": return GetRecord(skyrim, 0x0139B6);
					case "greatsword": return GetRecord(skyrim, 0x0139B7);
					case "mace": return GetRecord(skyrim, 0x0139B8);
					case "sword": return GetRecord(skyrim, 0x0139B9);
					case "waraxe": return GetRecord(skyrim, 0x0139B3);
					case "warhammer": return GetRecord(skyrim, 0x0139BA);
				}
			case "dwarven":
				switch (type) {
					case "battleaxe": return GetRecord(skyrim, 0x013994);
					case "bow": return GetRecord(skyrim, 0x013995);
					case "dagger": return GetRecord(skyrim, 0x013996);
					case "greatsword": return GetRecord(skyrim, 0x013997);
					case "mace": return GetRecord(skyrim, 0x013998);
					case "sword": return GetRecord(skyrim, 0x013999);
					case "waraxe": return GetRecord(skyrim, 0x013993);
					case "warhammer": return GetRecord(skyrim, 0x01399A);
				}
			case "draugr":
				switch (type) {
					case "battleaxe": return GetRecord(skyrim, 0x01CB64);
					case "bow": return GetRecord(skyrim, 0x0302CA);
					case "greatsword": return GetRecord(skyrim, 0x0236A5);
					case "sword": return GetRecord(skyrim, 0x02C66F);
					case "waraxe": return GetRecord(skyrim, 0x02C672);
				}
			case "draugrhoned":
				switch (type) {
					case "battleaxe": return GetRecord(skyrim, 0x05BF12);
					case "bow": return GetRecord(skyrim, 0x05D179);
					case "greatsword": return GetRecord(skyrim, 0x05BF13);
					case "sword": return GetRecord(skyrim, 0x05BF14);
					case "waraxe": return GetRecord(skyrim, 0x05BF15);
				}
			case "ebony":
				switch (type) {
					case "battleaxe": return GetRecord(skyrim, 0x0139AC);
					case "bow": return GetRecord(skyrim, 0x0139AD);
					case "dagger": return GetRecord(skyrim, 0x0139AE);
					case "greatsword": return GetRecord(skyrim, 0x0139AF);
					case "mace": return GetRecord(skyrim, 0x0139B0);
					case "sword": return GetRecord(skyrim, 0x0139B1);
					case "waraxe": return GetRecord(skyrim, 0x0139AB);
					case "warhammer": return GetRecord(skyrim, 0x0139B2);
				}
			case "elven":
				switch (type) {
					case "battleaxe": return GetRecord(skyrim, 0x01399C);
					case "bow": return GetRecord(skyrim, 0x01399D);
					case "dagger": return GetRecord(skyrim, 0x01399E);
					case "greatsword": return GetRecord(skyrim, 0x01399F);
					case "mace": return GetRecord(skyrim, 0x0139A0);
					case "sword": return GetRecord(skyrim, 0x0139A1);
					case "waraxe": return GetRecord(skyrim, 0x01399B);
					case "warhammer": return GetRecord(skyrim, 0x0139A2);
				}
			case "falmer":
				switch (type) {
					case "bow": return GetRecord(skyrim, 0x038340);
					case "sword": return GetRecord(skyrim, 0x02E6D1);
					case "waraxe": return GetRecord(skyrim, 0x0302CD);
				}
			case "falmerhoned":
				switch (type) {
					case "bow": return GetRecord(skyrim, 0x083167);
					case "sword": return GetRecord(skyrim, 0x06F6FF);
					case "waraxe": return GetRecord(skyrim, 0x06F700);
				}
			case "forsworn":
				switch (type) {
					case "waraxe": return GetRecord(skyrim, 0x0CC829);
					case "bow": return GetRecord(skyrim, 0x0CEE9B);
					case "sword": return GetRecord(skyrim, 0x0CADE9);
				}
			case "glass":
				switch (type) {
					case "battleaxe": return GetRecord(skyrim, 0x0139A4);
					case "bow": return GetRecord(skyrim, 0x0139A5);
					case "dagger": return GetRecord(skyrim, 0x0139A6);
					case "greatsword": return GetRecord(skyrim, 0x0139A7);
					case "mace": return GetRecord(skyrim, 0x0139A8);
					case "sword": return GetRecord(skyrim, 0x0139A9);
					case "waraxe": return GetRecord(skyrim, 0x0139A3);
					case "warhammer": return GetRecord(skyrim, 0x0139AA);
				}
			case "imperial":
				switch (type) {
					case "bow": return GetRecord(skyrim, 0x013841);
					case "sword": return GetRecord(skyrim, 0x0135B8);
				}
			case "iron":
				switch (type) {
					case "battleaxe": return GetRecord(skyrim, 0x013980);
					case "bow": return GetRecord(skyrim, 0x03B562);
					case "dagger": return GetRecord(skyrim, 0x01397E);
					case "greatsword": return GetRecord(skyrim, 0x01359D);
					case "mace": return GetRecord(skyrim, 0x013982);
					case "sword": return GetRecord(skyrim, 0x012EB7);
					case "waraxe": return GetRecord(skyrim, 0x013790);
					case "warhammer": return GetRecord(skyrim, 0x013981);
				}
			case "nordhero":
				switch (type) {
					case "battleaxe": return GetRecord(skyrim, 0x068C79);
					case "bow": return GetRecord(skyrim, 0x068C57);
					case "greatsword": return GetRecord(skyrim, 0x0A3115);
					case "sword": return GetRecord(skyrim, 0x068C7B);
					case "waraxe": return GetRecord(skyrim, 0x068C63);
				}
			case "orcish":
				switch (type) {
					case "battleaxe": return GetRecord(skyrim, 0x01398C);
					case "bow": return GetRecord(skyrim, 0x01398D);
					case "dagger": return GetRecord(skyrim, 0x01398E);
					case "greatsword": return GetRecord(skyrim, 0x01398F);
					case "mace": return GetRecord(skyrim, 0x013990);
					case "sword": return GetRecord(skyrim, 0x013991);
					case "waraxe": return GetRecord(skyrim, 0x01398B);
					case "warhammer": return GetRecord(skyrim, 0x013992);
				}
			case "steel":
				switch (type) {
					case "battleaxe": return GetRecord(skyrim, 0x013984);
					case "bow": return GetRecord(skyrim, 0x013985);
					case "dagger": return GetRecord(skyrim, 0x013986);
					case "greatsword": return GetRecord(skyrim, 0x013987);
					case "mace": return GetRecord(skyrim, 0x013988);
					case "sword": return GetRecord(skyrim, 0x013989);
					case "waraxe": return GetRecord(skyrim, 0x013983);
					case "warhammer": return GetRecord(skyrim, 0x01398A);
				}
			case "dragonbone":
				switch (type) {
					case "battleaxe": return GetRecord(dawnguard, 0x014FC3);
					case "bow": return GetRecord(dawnguard, 0x0176F1);
					case "dagger": return GetRecord(dawnguard, 0x014FCB);
					case "greatsword": return GetRecord(dawnguard, 0x014FCC);
					case "mace": return GetRecord(dawnguard, 0x014FCD);
					case "sword": return GetRecord(dawnguard, 0x014FCE);
					case "waraxe": return GetRecord(dawnguard, 0x014FCF);
					case "warhammer": return GetRecord(dawnguard, 0x014FD0);
				}
			case "nordic":
				switch (type) {
					case "battleaxe": return GetRecord(dragonborn, 0x01CDAD);
					case "bow": return GetRecord(dragonborn, 0x026232);
					case "dagger": return GetRecord(dragonborn, 0x01CDAE);
					case "greatsword": return GetRecord(dragonborn, 0x01CDAF);
					case "mace": return GetRecord(dragonborn, 0x01CDB0);
					case "sword": return GetRecord(dragonborn, 0x01CDB1);
					case "waraxe": return GetRecord(dragonborn, 0x01CDB2);
					case "warhammer": return GetRecord(dragonborn, 0x01CDB3);
				}
			case "stalhrim":
				switch (type) {
					case "battleaxe": return GetRecord(dragonborn, 0x01CDB4);
					case "bow": return GetRecord(dragonborn, 0x026231);
					case "dagger": return GetRecord(dragonborn, 0x01CDB5);
					case "greatsword": return GetRecord(dragonborn, 0x01CDB6);
					case "mace": return GetRecord(dragonborn, 0x01CDB7);
					case "sword": return GetRecord(dragonborn, 0x01CDB8);
					case "waraxe": return GetRecord(dragonborn, 0x01CDB9);
					case "warhammer": return GetRecord(dragonborn, 0x01CDBA);
				}
			default: return 0;
		}
	},

	PerkFormListBySkill: function(skill) {
		skill = skill.toLowerCase();

		const dragonborn = xelib.FileByName("Dragonborn.esm");

		switch (skill) {
			case "alchemy": return GetRecord(dragonborn, 0x01FE87);
			case "alteration": return GetRecord(dragonborn, 0x01FEA8);
			case "block": return GetRecord(dragonborn, 0x01FEAC);
			case "conjuration": return GetRecord(dragonborn, 0x01FE95);
			case "destruction": return GetRecord(dragonborn, 0x01FEA6);
			case "enchanting": return GetRecord(dragonborn, 0x01FEA9);
			case "light armor": return GetRecord(dragonborn, 0x01FEB0);
			case "heavy armor": return GetRecord(dragonborn, 0x01FEAB);
			case "illusion": return GetRecord(dragonborn, 0x01FE94);
			case "lockpicking": return GetRecord(dragonborn, 0x01FEB2);
			case "archery": return GetRecord(dragonborn, 0x01FEAF);
			case "one-handed": return GetRecord(dragonborn, 0x01FEAE);
			case "pickpocket": return GetRecord(dragonborn, 0x01FEB3);
			case "restoration": return GetRecord(dragonborn, 0x01FEA7);
			case "smithing": return GetRecord(dragonborn, 0x01FEAA);
			case "sneak": return GetRecord(dragonborn, 0x01FEB1);
			case "speech": return GetRecord(dragonborn, 0x01FEB4);
			case "two-handed": return GetRecord(dragonborn, 0x01FEAD);
			default: return 0;
		}
	},

	PerksBySkill: function(skill) {
		const list = xelib.GetWinningOverride(module.exports.PerkFormListBySkill(skill));
		if (list === 0) {
			return 0;
		}
		const formids = xelib.GetElement(list, "FormIDs");
		let arr = [];
		for (let i = 0; i < xelib.ElementCount(formids); i++) {
			arr.push(xelib.GetWinningOverride(xelib.GetLinksTo(formids, `[${i}]`)));

		}
		return arr;
	}
};


function GetRecord(file, formID) {
	loadOrderFormID = (xelib.GetFileLoadOrder(file) << 24) + formID;
	return xelib.GetRecord(0, loadOrderFormID);
}
