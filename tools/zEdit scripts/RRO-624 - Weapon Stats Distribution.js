const scriptPath = `${xelib.GetGlobal("ProgramPath")}\\scripts\\`;
const Database = require(`${scriptPath}Database.js`);
const TTLib = require(`${scriptPath}TT Library.js`);

let selected = zedit.GetSelectedRecords("WEAP");

let {showProgress, logMessage, progressTitle, addProgress, progressMessage} = zedit.progressService;
showProgress({
	determinate: true,
	title: "Reqiem Weapon Patcher",
	message : "",
	canClose: true,
	current: 0,
	max: selected.length
});

for (let weapon of selected) {
	let isBow = xelib.GetValue(weapon, "DNAM\\Animation Type") === "Bow";
	let isCrossbow = xelib.GetValue(weapon, "DNAM\\Animation Type") === "Crossbow";
	let isStaff = xelib.GetValue(weapon, "DNAM\\Animation Type") === "Staff";
	if (!isBow && !isCrossbow && !isStaff) {
		let editorID = xelib.EditorID(weapon)
		let regex = /[^_]+(?:_(?:Creature|Ench|NonPlayable|Variant))?_([^_]+)_([^_]+)(?:_(.+))?/
		let match = editorID.match(regex);
		if (match) {
			let set = match[1];
			let type = match[2];
			let suffix = match[3];
			let goldOffset = 0;
			let goldMult = 1.0;
			let weightOffset = 0.0;
			let weightMult = 1.0;
			let damageOffset = 0;
			let damageMult = 1.0;
			let speedOffset = 0.0;
			let reachOffset = 0.0;
			let baseSet = "Steel";

			if (set === "Blades") {
				damageOffset += 3;
				weightOffset += 0.0;
				goldMult *= 7.0;
			}
			else if (set === "Bound") {
				damageOffset += 1;
				weightMult = 0.0;
				goldMult *= 0.0;
			}
			else if (set === "BoundMystic") {
				damageOffset += 6;
				weightMult = 0.0;
				goldMult *= 0.0;
			}
			else if (set === "Daedric") {
				damageOffset += 7;
				weightOffset += 7.0;
				goldMult *= 100.0;
			}
			else if (set === "Dawnguard") {
				damageOffset += 2;
				weightOffset += 1.0;
				goldMult *= 6.0;
			}
			else if (set === "Dragonbone") {
				damageOffset += 6;
				weightOffset += 6.0;
				goldMult *= 80.0;
			}
			else if (set === "Draugr") {
				damageOffset += 0;
				weightOffset += 1.0;
				goldMult *= 1 / 3;
			}
			else if (set === "DraugrHoned") {
				damageOffset += 3;
				weightOffset += 1.0;
				goldMult *= 2 / 3;
			}
			else if (set === "Dwarven") {
				damageOffset += 3;
				weightOffset += 4.0;
				goldMult *= 5.0;
			}
			else if (set === "Ebony") {
				damageOffset += 5;
				weightOffset += 5.0;
				goldMult *= 40.0;
			}
			else if (set === "Elven") {
				damageOffset += 1;
				weightOffset += -2.0;
				goldMult *= 3.0;
			}
			else if (set === "Falmer") {
				damageOffset += -1;
				weightOffset += 0.0;
				goldMult *= 0.25;
			}
			else if (set === "FalmerHoned") {
				damageOffset += 2;
				weightOffset += 0.0;
				goldMult *= 0.5;
			}
			else if (set === "Forsworn") {
				damageOffset += -2;
				weightOffset += -2.0;
				goldMult *= 0.2;
			}
			else if (set === "Glass") {
				damageOffset += 4;
				weightOffset += -3.0;
				goldMult *= 25.0;
			}
			else if (set === "Imperial") {
				damageOffset += 0;
				weightOffset += 0.0;
				goldMult *= 4 / 3;
			}
			else if (set === "Iron") {
				damageOffset += -1;
				weightOffset += -1.0;
				goldMult *= 0.55;
			}
			else if (set === "NordHero") {
				damageOffset += 3;
				weightOffset += -1.0;
				goldMult *= 6.0;
			}
			else if (set === "Nordic") {
				damageOffset += 2;
				weightOffset += 0.0;
				goldMult *= 4.0;
			}
			else if (set === "Orcish") {
				damageOffset += 2;
				weightOffset += 2.0;
				goldMult *= 4.0;
			}
			else if (set === "Redguard") {
				damageOffset += 0;
				weightOffset += 0.0;
				goldMult *= 1.5;
			}
			else if (set === "Silver") {
				damageOffset += 0;
				weightOffset += 0.0;
				goldMult *= 2.0;
			}
			else if (set === "SkyforgeSteel") {
				damageOffset += 3;
				weightOffset += 0.0;
				goldMult *= 7.0;
			}
			else if (set === "SpectralDraugr") {
				damageOffset += 3;
				weightMult = 0.0;
				goldMult *= 0.0;
			}
			else if (set === "Stalhrim") {
				damageOffset += 5;
				weightOffset += 4.0;
				goldMult *= 40.0;
			}
			else if (set === "Steel") {
				damageOffset += 0;
				weightOffset += 0.0;
				goldMult *= 1.0;
			}
			else if (set === "Wood") {
				damageOffset += -6;
				weightOffset += -7.0;
				goldMult *= 0.25;
			}
			else {
				baseSet = "";
			}
			// Derive stats of new weapon types from base weapons
			let baseType = type;
			if (type === "Club") {
				baseType = "Mace";
				damageOffset -= 1.0;
				weightOffset -= 1.0;
				speedOffset += 0.05;
			}
			else if (type === "Longsword") {
				baseType = "Sword";
				damageOffset += 1;
				weightOffset += 1.0;
				speedOffset -= 0.05;
				reachOffset += 0.1;
			}
			else if (type === "Saber") {
				baseType = "Sword";
				damageOffset -= 1.0;
				weightOffset -= 1.0;
				speedOffset += 0.1;
			}
			else if (type === "Scimitar") {
				baseType = "Sword";
				damageOffset -= 0.5;
				weightOffset -= 0.5;
				speedOffset += 0.05;
			}
			else if (type === "Shortsword") {
				baseType = "Sword";
				damageOffset -= 1;
				weightOffset -= 1.0;
				speedOffset += 0.15;
				reachOffset -= 0.1;
			}
			// Speed of greatswords is too low in Skyrim.esm
			if (baseType === "Greatsword") {
				speedOffset += 0.05;
			}
			else if (baseType === "Dagger") {
				weightOffset /= 2;
			}

			let baseWeapon = Database.WeaponBySetAndType(baseSet, baseType);
			if (baseWeapon) {
				let alreadyReqtified = !Number.isInteger(damageOffset);
				// Gold
				newVal = xelib.GetGoldValue(baseWeapon) * goldMult + goldOffset;
				newVal = Math.round(newVal / 5) * 5;
				if (newVal !== xelib.GetGoldValue(weapon)) {
					xelib.SetGoldValue(weapon, newVal);
				}
				// Weight
				newVal = xelib.GetWeight(baseWeapon) * weightMult + weightOffset;
				if (Math.abs(newVal - xelib.GetWeight(weapon)) > 0.001) {
					xelib.SetWeight(weapon, newVal);
				}
				// Damage
				newVal = xelib.GetDamage(baseWeapon) * damageMult + damageOffset;
				if (alreadyReqtified) {
					newVal *= 6;
				}
				newVal = Math.trunc(newVal);
				if (newVal !== xelib.GetDamage(weapon))  {
					xelib.SetDamage(weapon, newVal);
				}
				// Speed
				newVal = TTLib.GetWeaponSpeed(baseWeapon) + speedOffset;
				if (Math.abs(newVal - TTLib.GetWeaponSpeed(weapon)) > 0.001) {
					TTLib.SetWeaponSpeed(weapon, newVal);
				}
				// Reach
				newVal = TTLib.GetWeaponReach(baseWeapon) + reachOffset;
				if (alreadyReqtified) {
					newVal *= 0.7;
				}
				if (Math.abs(newVal - TTLib.GetWeaponReach(weapon)) > 0.001) {
					TTLib.SetWeaponReach(weapon, newVal);
				}
				// REQ_KW_AlreadyReqtified
				if (alreadyReqtified) {
					if (!xelib.HasKeyword(weapon, "REQ_KW_AlreadyReqtified")) {
						xelib.AddKeyword(weapon, "REQ_KW_AlreadyReqtified");
					}
				}
				else {
					if (xelib.HasKeyword(weapon, "REQ_KW_AlreadyReqtified")) {
						xelib.RemoveKeyword(weapon, "REQ_KW_AlreadyReqtified");
					}
				}
			}
			else if (set !== "NULL") {
				logMessage(`${xelib.LongName(weapon)} doesn't have a base weapon`);
			}
		}
		else {
			logMessage(`${xelib.LongName(weapon)} doesn't have a matching EditorID`);
		}
	}
	addProgress(1);
}
