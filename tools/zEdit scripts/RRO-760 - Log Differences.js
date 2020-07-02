const plugin = xelib.FileByName("RRO-760.esp");
const selected = xelib.GetRecords(plugin, "ARMO", true);

const {showProgress, logMessage, progressTitle, addProgress, progressMessage} = zedit.progressService;
showProgress({
	determinate: true,
	title: "RRO-760 - Log Differences",
	message : "",
	canClose: true,
	current: 0,
	max: selected.length
});

const armorRegex = /^[^_]+_(Heavy|Light)_([^_]+)_([^_]+)$/
const artifactRegex = /^[^_]+_Artifact_(.+)$/
const clothingRegex = /^[^_]+(?:_(?:Ench|NP|Var))?_Cloth_([^_]+)_([^_]+)(?:_(.+))?$/
const creatureRegex = /^[^_]+_Creature_(.+)$/
const jewelryRegex = /^[^_]+(?:_(?:Ench|NP|Var))?_(Amulet|Circlet|Ring)_([^_]+)(?:_(.+))?$/
const nullifiedRegex = /^[^_]+_NULL_.+$/
const saddleRegex = /^[^_]+_Saddle_([^_]+)(?:_(.+))?$/


const armorSets = new Map();
for (const armor of selected) {
	const editorID = xelib.EditorID(armor);
	const armorMatch = editorID.match(armorRegex);
	if (armorMatch) {
		const type = armorMatch[1]
		const set = armorMatch[2];
		const part = armorMatch[3];
		const key = type + " " + set;
		if (armorSets.has(key)) {
			armorSets.get(key).push(armor);
		}
		else {
			armorSets.set(key, [armor]);
		}
		addProgress(0.5);
	}
	else {
		const clothingMatch = editorID.match(clothingRegex);
		const creatureMatch = editorID.match(creatureRegex);
		const jewelryMatch = editorID.match(jewelryRegex);
		const nullifiedMatch = editorID.match(nullifiedRegex);
		const saddleMatch = editorID.match(saddleRegex);
		if (!clothingMatch && !creatureMatch && !jewelryMatch && !nullifiedMatch && !saddleMatch) {
			logMessage(`Skipping ${editorID}`);
		}
		addProgress(1);
	}
}
logMessage(`|Set|Armor Rating|Weight|Price`);
for (const [set, armors] of armorSets) {
	let armorRatingDiff = 0;
	let weightDiff = 0;
	let priceDiff = 0;
	for (const armor of armors) {
		const prevOverride = xelib.GetPreviousOverride(armor, plugin);
		const currArmorRating = xelib.GetArmorRating(armor);
		const prevArmorRating = xelib.GetArmorRating(prevOverride);
		armorRatingDiff += currArmorRating - prevArmorRating;
		const currWeight = xelib.GetWeight(armor);
		const prevWeight = xelib.GetWeight(prevOverride);
		weightDiff += currWeight - prevWeight;
		const currPrice = xelib.GetGoldValue(armor);
		const prevPrice = xelib.GetGoldValue(prevOverride);
		priceDiff += currPrice - prevPrice;
		addProgress(0.5)
	}
	logMessage(`|${set}|${armorRatingDiff}|${weightDiff}|${priceDiff}`);
}
