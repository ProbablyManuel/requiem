const selected = zedit.GetSelectedRecords("ARMO");
// const selected = xelib.GetRecords(xelib.FileByName("RRO-760.esp"), "ARMO", true);

const {showProgress, logMessage, progressTitle, addProgress, progressMessage} = zedit.progressService;
showProgress({
	determinate: true,
	title: "Requiem Armor Patcher",
	message : "",
	canClose: true,
	current: 0,
	max: selected.length
});

class ArmorValues {
	constructor(armorRating, weight, gold) {
		this.armorRating = armorRating;
		this.weight = weight;
		this.gold = gold;
	}
}

const setValuesMap = buildArmorSetValues();
const offsetValuesMap = buildArmorOffsetValues();

const armorRegex = /^[^_]+(?:_(Ench|NP|Var))?_(Heavy|Light)_([^_]+)_([^_]+)(?:_(.+))?$/
const artifactRegex = /^[^_]+_Artifact_(.+)$/
const clothingRegex = /^[^_]+(?:_(Ench|NP|Var))?_Cloth_([^_]+)_([^_]+)(?:_(.+))?$/
const creatureRegex = /^[^_]+_Creature_(.+)$/
const jewelryRegex = /^[^_]+(?:_(Ench|NP|Var))?_(Amulet|Circlet|Ring)_([^_]+)(?:_(.+))?$/
const nullifiedRegex = /^[^_]+_NULL_.+$/
const saddleRegex = /^[^_]+_Saddle_([^_]+)(?:_(.+))?$/

for (const armor of selected) {
	const editorID = xelib.EditorID(armor);
	const armorMatch = editorID.match(armorRegex);
	const artifactMatch = editorID.match(artifactRegex);
	if (armorMatch) {
		const variant = armorMatch[1];
		const type = armorMatch[2];
		const set = armorMatch[3];
		const part = armorMatch[4];
		let suffix = armorMatch[5];
		if (variant === "Ench") {
			suffix = discardEnchantmentFromSuffix(suffix);			
		}
		const armorValues = getArmorValues(type, set, part, suffix);
		if (armorValues) {
			setArmorValues(armor, armorValues);
		}
		else {
			logMessage(`Type ${type} and set ${set} is not recognized`);
		}
	}
	else if (artifactMatch) {
		const artifact = artifactMatch[1];
		const armorValues = getArtifactValues(artifact);
		if (armorValues) {
			setArmorValues(armor, armorValues);
		}
		else if (xelib.GetArmorType(armor) !== "Clothing") {
			logMessage(`Artifact ${artifact} is not recognized`);
		}
	}
	else {
		const clothingMatch = editorID.match(clothingRegex);
		const creatureMatch = editorID.match(creatureRegex);
		const jewelryMatch = editorID.match(jewelryRegex);
		const nullifiedMatch = editorID.match(nullifiedRegex);
		const saddleMatch = editorID.match(saddleRegex);
		if (!clothingMatch && !creatureMatch && !jewelryMatch && !nullifiedMatch && !saddleMatch) {
			logMessage(`EditorID ${editorID} is not recognized`);
		}
	}
	addProgress(1);
}


/**
 * Builds armor values for the offsets of all items in base Requiem relative to their set.
 * @returns {Map<String, ArmorValues>} map - The keys are type + set + part + suffix separated by '_'.
 */
function buildArmorOffsetValues() {
	const map = new Map();
	map.set("Heavy_Guard_Body_TheReach", new ArmorValues(50, 5, 150));
	map.set("Heavy_Guard_Body_Hjaalmarch", new ArmorValues(-50, -5, -100));
	map.set("Heavy_Guard_Body_ThePale", new ArmorValues(-50, -5, -100));
	map.set("Heavy_Guard_Body_TheRift", new ArmorValues(-50, -5, -100));
	map.set("Heavy_Guard_Body_Winterhold", new ArmorValues(-50, -5, -100));
	map.set("Heavy_Iron_Body_Pauldrons", new ArmorValues(30, 2.5, 75));
	map.set("Heavy_Iron_Shield_Banded", new ArmorValues(10, 2, 40));
	map.set("Light_Fur_Body_Kilt", new ArmorValues(-50, -2, -20));
	map.set("Light_Fur_Body_KiltWithMantle", new ArmorValues(-50, -2, -20));
	map.set("Light_Hide_Body_Studded", new ArmorValues(25, 1, 25));
	map.set("Light_Imperial_Body_Studded", new ArmorValues(25, 1, 25));
	map.set("Light_Imperial_Body_Tullius", new ArmorValues(50, 2, 3000));
	map.set("Light_Vampire_Body_Harkon", new ArmorValues(50, 2, 500));
	map.set("Light_Vampire_Body_Royal", new ArmorValues(50, 2, 500));
	map.set("Light_Vampire_Body_Valerica", new ArmorValues(50, 2, 500));
	return map;
}


/**
 * Builds armor values for all sets in base Requiem and any set added by you.
 * @returns {Map<String, ArmorValues>} map - The keys are type + set separated by '_'.
 */
function buildArmorSetValues() {
	const map = new Map();
	buildArmorSetValuesBase(map);
	buildArmorSetValuesBaseVariants(map);
	return map;
}


/**
 * Builds armor values for all sets in base Requiem.
 * @param {Map<String, ArmorValues>} map - The keys are type + set separated by '_'.
 */
function buildArmorSetValuesBase(map) {
	map.set("Heavy_Aetherium", new ArmorValues(1000, 80, 50000));
	map.set("Heavy_Ahzidal", new ArmorValues(700, 50, 5000));
	map.set("Heavy_AncientNord", new ArmorValues(600, 50, 300));
	map.set("Heavy_Blades", new ArmorValues(700, 50, 2500));
	map.set("Heavy_Bonemold", new ArmorValues(600, 40, 1000));
	map.set("Heavy_Daedric", new ArmorValues(1200, 100, 50000));
	map.set("Heavy_Dawnguard", new ArmorValues(700, 50, 1000));
	map.set("Heavy_Dragonplate", new ArmorValues(1000, 70, 20000));
	map.set("Heavy_Dwarven", new ArmorValues(900, 70, 2000));
	map.set("Heavy_Ebony", new ArmorValues(1000, 80, 10000));
	map.set("Heavy_FalmerHardened", new ArmorValues(800, 60, 400));
	map.set("Heavy_FalmerHeavy", new ArmorValues(800, 60, 400));
	map.set("Heavy_Guard", new ArmorValues(600, 50, 500));
	map.set("Heavy_Imperial", new ArmorValues(600, 50, 500));
	map.set("Heavy_ImperialLegate", new ArmorValues(700, 50, 1000));
	map.set("Heavy_ImprovedBonemold", new ArmorValues(700, 50, 1500));
	map.set("Heavy_Iron", new ArmorValues(500, 40, 250));
	map.set("Heavy_NordicCarved", new ArmorValues(800, 60, 1000));
	map.set("Heavy_Orcish", new ArmorValues(800, 60, 1500));
	map.set("Heavy_Stalhrim", new ArmorValues(1000, 80, 10000));
	map.set("Heavy_Steel", new ArmorValues(600, 50, 500));
	map.set("Heavy_SteelPlate", new ArmorValues(800, 60, 1000));
	map.set("Heavy_StormcloakOfficer", new ArmorValues(600, 50, 500));
	map.set("Heavy_Vigilant", new ArmorValues(800, 60, 1000));
	map.set("Heavy_Wolf", new ArmorValues(800, 50, 5000));
	map.set("Light_Alikr", new ArmorValues(300, 10, 300));
	map.set("Light_AncientShrouded", new ArmorValues(350, 12, 400));
	map.set("Light_Chitin", new ArmorValues(400, 25, 800));
	map.set("Light_Dawnguard", new ArmorValues(500, 30, 600));
	map.set("Light_Dragonscale", new ArmorValues(600, 30, 16000));
	map.set("Light_Elven", new ArmorValues(400, 15, 800));
	map.set("Light_Falmer", new ArmorValues(250, 15, 100));
	map.set("Light_Forsworn", new ArmorValues(250, 8, 100));
	map.set("Light_Fur", new ArmorValues(250, 8, 100));
	map.set("Light_Glass", new ArmorValues(600, 40, 6000));
	map.set("Light_Guard", new ArmorValues(300, 15, 400));
	map.set("Light_Hide", new ArmorValues(250, 8, 100));
	map.set("Light_Imperial", new ArmorValues(300, 10, 200));
	map.set("Light_Leather", new ArmorValues(300, 10, 200));
	map.set("Light_Linwe", new ArmorValues(300, 10, 200));
	map.set("Light_MoragTong", new ArmorValues(500, 30, 5000));
	map.set("Light_Nightingale", new ArmorValues(350, 12, 10000));
	map.set("Light_PenitusOculatus", new ArmorValues(500, 12, 600));
	map.set("Light_ReinforcedChitin", new ArmorValues(450, 35, 1000));
	map.set("Light_Scaled", new ArmorValues(500, 20, 400));
	map.set("Light_Shrouded", new ArmorValues(300, 10, 200));
	map.set("Light_Skaal", new ArmorValues(300, 10, 400));
	map.set("Light_SnowElf", new ArmorValues(500, 15, 8000));
	map.set("Light_Stalhrim", new ArmorValues(600, 40, 6000));
	map.set("Light_ThievesGuild", new ArmorValues(300, 10, 200));
	map.set("Light_ThievesGuildMaster", new ArmorValues(350, 12, 1000));
	map.set("Light_Ulfric", new ArmorValues(350, 12, 5000));
	map.set("Light_Vampire", new ArmorValues(300, 10, 200));
}


/**
 * Builds armor values for all variants of sets in base Requiem (e.g. Worn Shrouded Armor).
 * @param {Map<String, ArmorValues>} map - The keys are type + set separated by '_'.
 */
function buildArmorSetValuesBaseVariants(map) {
	map.set("Heavy_Dremora", map.get("Heavy_Daedric"));
	map.set("Heavy_Shellbug", map.get("Heavy_FalmerHeavy"));
	map.set("Light_Stormcloak", map.get("Light_Guard"));
	map.set("Light_SummersetShadows", map.get("Light_ThievesGuild"));
	map.set("Light_ThievesGuildKarliah", map.get("Light_ThievesGuild"));
	map.set("Light_WornShrouded", map.get("Light_Shrouded"));
	map.set("Light_Blackguard", map.get("Light_ThievesGuild"));
}


/**
 * Discards everything up to and including the first occurence of '_' from a string.
 * @param {String} str - The suffix of an enchanted item.
 * @returns {String} newStr - The new suffix or undefined if the suffix no longer exists.
 */
function discardEnchantmentFromSuffix(str) {
	if (str) {
		const separatorIndex = str.indexOf("_");
		if (separatorIndex >= 0) {
			return str.substring(0, separatorIndex);
		}
	}
	return undefined;
}


/**
 * Returns the final armor values for an item.
 * @param {String} type - Heavy or Light.
 * @param {String} set - Name of the set.
 * @param {String} part - Head, Body, Hands, Feet or Shield.
 * @param {String} suffix - Suffix to carry additional information for ambiguous items.
 * @returns {ArmorValues} armorValues - If the armor values cannot be determined, null is returned instead.
 */
function getArmorValues(type, set, part, suffix) {
	const setValues = setValuesMap.get(`${type}_${set}`);
	if (!setValues) {
		return null;
	}
	const armorValues = new ArmorValues(
		GetArmorRatingForBodyPart(setValues.armorRating, part),
		GetWeightForBodyPart(setValues.weight, part),
		GetGoldForBodyPart(setValues.gold, part)
	);
	let offsetValues = offsetValuesMap.get(`${type}_${set}_${part}_${suffix}`);
	if (!offsetValues) {
		offsetValues = offsetValuesMap.get(`${type}_${set}_${part}`);
	}
	if (offsetValues) {
		armorValues.armorRating += offsetValues.armorRating;
		armorValues.weight += offsetValues.weight;
		armorValues.gold += offsetValues.gold;
	}
	return armorValues;
}


/**
 * Returns the final armor values for an artifact.
 * Armor values are defined for all artifacts in base Requiem.
 * @param {String} artifact - Name of the artifact without whitespaces and apostrophes.
 * @returns {ArmorValues} armorValues - If the armor values cannot be determined, null is returned instead.
 */
function getArtifactValues(artifact) {
	if (artifact === "AetherialShield") {
		const armorValues = getArmorValues("Heavy", "Aetherium", "Shield");
		armorValues.gold = 50000;
		return armorValues;
	}
	if (artifact === "AurielsShield") {
		const armorValues = getArmorValues("Light", "SnowElf", "Shield");
		armorValues.armorRating += 50;
		armorValues.gold = 100000;
		return armorValues;
	}
	if (artifact === "DawnguardRuneShield") {
		const armorValues = getArmorValues("Heavy", "Dawnguard", "Shield");
		armorValues.gold = 5000;
		return armorValues;
	}
	if (artifact === "EbonyMail") {
		const armorValues = getArmorValues("Heavy", "Ebony", "Body");
		armorValues.gold = 100000;
		return armorValues;
	}
	if (artifact === "HelmOfYngol") {
		const armorValues = getArmorValues("Heavy", "AncientNord", "Head");
		armorValues.armorRating += 50;
		armorValues.gold = 5000;
		return armorValues;
	}
	if (artifact === "JaggedCrown") {
		const armorValues =  getArmorValues("Heavy", "Dragonplate", "Head");
		armorValues.weight -= 4;
		armorValues.gold = 5000;
		return armorValues;
	}
	if (artifact === "SaviorsHide") {
		const armorValues = getArmorValues("Light", "Scaled", "Body");
		armorValues.gold = 100000;
		return armorValues;
	}
	if (artifact === "ShieldOfSolitude") {
		const armorValues = getArmorValues("Light", "Guard", "Shield");
		armorValues.gold = 5000;
		armorValues.armorRating += 25;
		return armorValues;
	}
	if (artifact === "ShieldOfYsgramor") {
		const armorValues = getArmorValues("Heavy", "Ebony", "Shield");
		armorValues.gold = 50000;
		return armorValues;
	}
	if (artifact === "Spellbreaker") {
		const armorValues = getArmorValues("Heavy", "Dwarven", "Shield");
		armorValues.armorRating += 50;
		armorValues.gold = 100000;
		return armorValues;
	}
	if (artifact === "TargeOfTheBlooded") {
		const armorValues = getArmorValues("Heavy", "Steel", "Shield");
		armorValues.gold = 300;
		return armorValues;
	}
	if (artifact === "VisageOfMzund") {
		const armorValues = getArmorValues("Heavy", "Dwarven", "Head");
		armorValues.gold = 5000;
		return armorValues;
	}
	return null;
}


/**
 * Returns armor rating for a part of a set.
 * @param {Number} setArmorRating - Armor rating of the set.
 * @param {String} part - Head, Body, Hands, Feet or Shield.
 * @returns {Number} armorRating - Armor rating of the part or -1 if the part cannot be determined.
 */
function GetArmorRatingForBodyPart(setArmorRating, part) {
	if (part === "Head") {
		return setArmorRating * 0.2;
	}
	if (part === "Feet") {
		return setArmorRating * 0.15;
	}
	if (part === "Hands") {
		return setArmorRating * 0.15;
	}
	if (part === "Body") {
		return setArmorRating * 0.5;
	}
	if (part === "Shield") {
		return setArmorRating * 0.3;
	}
	return -1;
}


/**
 * Returns price for a part of a set.
 * @param {Number} setGold - Price of the set.
 * @param {String} part - Head, Body, Hands, Feet or Shield.
 * @returns {Number} gold - Price of the part or -1 if the part cannot be determined.
 */
function GetGoldForBodyPart(setGold, part) {
	if (part === "Head") {
		return Math.round(setGold * 0.2);
	}
	if (part === "Feet") {
		return Math.round(setGold * 0.15);
	}
	if (part === "Hands") {
		return Math.round(setGold * 0.15);
	}
	if (part === "Body") {
		return Math.round(setGold * 0.5);
	}
	if (part === "Shield") {
		return Math.round(setGold * 0.25);
	}
	return -1;
}


/**
 * Returns weight for a part of a set.
 * @param {Number} setWeight - Weight of the set.
 * @param {String} part - Head, Body, Hands, Feet or Shield.
 * @returns {Number} weight - Weight of the part or -1 if the part cannot be determined.
 */
function GetWeightForBodyPart(setWeight, part) {
	const headWeight = Math.round(setWeight * 0.2 * 2) / 2;
	if (part === "Head") {
		return headWeight;
	}
	const feetWeight = Math.round(setWeight * 0.15 * 2) / 2;
	if (part === "Feet") {
		return feetWeight;
	}
	const handWeight = Math.round(setWeight * 0.15 * 2) / 2;
	if (part === "Hands") {
		return handWeight;
	}
	const bodyWeight = setWeight - headWeight - feetWeight - handWeight;
	if (part === "Body") {
		return bodyWeight;
	}
	if (part === "Shield") {
		return Math.round(setWeight * 0.25 * 2) / 2;
	}
	return -1;
}


/**
 * Sets the armor values of an item.
 * @param {Object} armor - zEdit handle to the item.
 * @param {ArmorValues} values - The armor values of the item.
 */
function setArmorValues(armor, values) {
	if (values.gold !== xelib.GetGoldValue(armor)) {
		xelib.SetGoldValue(armor, values.gold);
	}
	if (Math.abs(values.weight - xelib.GetWeight(armor)) > 0.001) {
		xelib.SetWeight(armor, values.weight);
	}
	if (Math.abs(values.armorRating - xelib.GetArmorRating(armor)) > 0.001) {
		xelib.SetArmorRating(armor, values.armorRating);
	}
}
