module.exports = {
	BodyPartToArmorPart: function(str) {
		str = str.toLowerCase();
		switch (str) {
			case "head": return "Helmet"; 
			case "body": return "Cuirass"; 
			case "hand": return "Gauntlets"; 
			case "feet": return "Boots"; 
			default: return str;
		}
	},

	GetSetFromEditorID: function(str) {
		return GetTextBetween(str, '_');
	},

	GetBodyPartFromEditorID: function(str) {
		str = str.slice(str.indexOf('_') + 1);
		bodyPart = GetTextBetween(str, '_');
		if (bodyPart === "") {
			return str.slice(str.indexOf('_') + 1);
		}
		return bodyPart;
	},

	GetArmorPart: function(armor) {
		if (xelib.HasKeyword(armor, "ArmorBoots")) {
			return "Boots";
		}
		else if (xelib.HasKeyword(armor, "ArmorCuirass")) {
			return "Cuirass";
		}
		else if (xelib.HasKeyword(armor, "ArmorGauntlets")) {
			return "Gauntlets";
		}
		else if (xelib.HasKeyword(armor, "ArmorHelmet")) {
			return "Helmet";
		}
		else if (xelib.HasKeyword(armor, "ArmorShield")) {
			return "Shield";
		}
		else {
			return "";
		}
	},

	GetWeaponType: function(weapon) {
		if (xelib.HasKeyword(weapon, "WeapTypeBattleaxe")) {
			return "Battleaxe";
		}
		else if (xelib.HasKeyword(weapon, "WeapTypeBow")) {
			return "Bow";
		}
		else if (xelib.HasKeyword(weapon, "WeapTypeDagger")) {
			return "Dagger";
		}
		else if (xelib.HasKeyword(weapon, "WeapTypeGreatsword")) {
			return "Greatsword";
		}
		else if (xelib.HasKeyword(weapon, "WeapTypeMace")) {
			return "Mace";
		}
		else if (xelib.HasKeyword(weapon, "WeapTypeSword")) {
			return "Sword";
		}
		else if (xelib.HasKeyword(weapon, "WeapTypeWarAxe")) {
			return "WarAxe";
		}
		else if (xelib.HasKeyword(weapon, "WeapTypeWarhammer")) {
			return "Warhammer";
		}
		else {
			return "";
		}
	},

	GetCriticalDamage: function(record) {
		return xelib.GetUIntValue(record, "CRDT\\Damage");
	},

	SetCriticalDamage: function(record, value) {
		return xelib.SetUIntValue(record, "CRDT\\Damage", value);
	},

	GetWeaponSpeed: function(record) {
		return xelib.GetFloatValue(record, "DNAM\\Speed");
	},

	SetWeaponSpeed: function(record, value) {
		return xelib.SetFloatValue(record, "DNAM\\Speed", value);
	},

	GetWeaponReach: function(record) {
		return xelib.GetFloatValue(record, "DNAM\\Reach");
	},

	SetWeaponReach: function(record, value) {
		return xelib.SetFloatValue(record, "DNAM\\Reach", value);
	},

	GetActorSkill: function(npc, skill) {
		skill = skill.toLowerCase();

		switch (skill) {
			case "alchemy": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[10]");
			case "alteration": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[12]");
			case "block": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[3]");
			case "conjuration": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[13]");
			case "destruction": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[14]");
			case "enchanting": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[17]");
			case "light armor": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[6]");
			case "heavy armor": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[5]");
			case "illusion": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[15]");
			case "lockpicking": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[8]");
			case "archery": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[2]");
			case "one-handed": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[0]");
			case "pickpocket": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[7]");
			case "restoration": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[16]");
			case "smithing": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[4]");
			case "sneak": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[9]");
			case "speech": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[11]");
			case "two-handed": return xelib.GetIntValue(npc, "DNAM\\Skill Values\\[1]");
			default: return -1;
		}
	},

	GetPerkRequiredSkill: function(perk, skill) {
		// Assumes the perk has at most one base actor value condition
		// Should hold for any playable perk because the perk menu makes the same assumption
		const condition = xelib.GetCondition(perk, "GetBaseActorValue");
		if (condition === 0) {
			return 0;
		}
		if (xelib.GetValue(condition, "CTDA\\Actor Value") !== skill) {
			return -1;
		}
		if (xelib.GetValue(condition, "CTDA\\Type") !== "11000000") {
			return -1;
		}
		return xelib.GetIntValue(condition, "CTDA\\Comparison Value");
	},

	InheritsSpellList: function(npc) {
		return xelib.HasElement(npc, "TPLT - Template") && xelib.GetFlag(npc, "ACBS\\Template Flags", "Use Spell List");
	},

	IsPlayable: function(record) {
		if (xelib.Signature(record) === "ARMO") {
			return !xelib.GetRecordFlag(record, "Non-Playable") &&
			       (!xelib.HasElement(record, "BODT\\General Flags") || !xelib.GetFlag(record, "BODT\\General Flags", "(ARMO)Non-Playable")) &&
			       (!xelib.HasElement(record, "BOD2\\General Flags") || !xelib.GetFlag(record, "BOD2\\General Flags", "(ARMO)Non-Playable"));
		}
		else if (xelib.Signature(record === "WEAP")) {
			return !xelib.GetRecordFlag(record, "Non-Playable") && !xelib.GetFlag(record, "DNAM\\Flags", "Non-playable");
		}
		else {
			return !xelib.GetRecordFlag(record, "Non-Playable");
		}
	}
};

function GetTextBetween(str, delimiter) {
	let firstIndex = -1;
	let secondIndex = -1;
	for (let i = 0; i < str.length; i++) {
		if (str.charAt(i) === delimiter) {
			if (firstIndex === -1) {
				firstIndex = i;
			}
			else if (secondIndex === -1) {
				secondIndex = i;
			}
		}
	}
	if (firstIndex === -1 || secondIndex === -1) {
		return "";
	}
	return str.substring(firstIndex + 1, secondIndex);
}
