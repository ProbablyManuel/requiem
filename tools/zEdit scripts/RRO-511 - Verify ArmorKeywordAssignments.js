const scriptPath = `${xelib.GetGlobal("ProgramPath")}\\scripts\\`;
const TTLib = require(`${scriptPath}TT Library.js`);

const armors = xelib.GetRecords(0, "ARMO", false);

const {showProgress, logMessage, progressTitle, addProgress, progressMessage} = zedit.progressService;
showProgress({
    determinate: true,
    title: "Test ArmorKeywordAssignments",
    message : "",
    canClose: true,
    current: 0,
    max: armors.length
});

const bluntKeywords = ["REQ_KW_Armor_Resistance_Blunt_None",
                       "REQ_KW_Armor_Resistance_Blunt_Tier1",
                       "REQ_KW_Armor_Resistance_Blunt_Tier2",
                       "REQ_KW_Armor_Resistance_Blunt_Tier3",
                       "REQ_KW_Armor_Resistance_Blunt_Tier4",
                       "REQ_KW_Armor_Resistance_Blunt_Tier5"];
const pierceKeywords = ["REQ_KW_Armor_Resistance_Pierce_None",
                        "REQ_KW_Armor_Resistance_Pierce_Tier1",
                        "REQ_KW_Armor_Resistance_Pierce_Tier2",
                        "REQ_KW_Armor_Resistance_Pierce_Tier3",
                        "REQ_KW_Armor_Resistance_Pierce_Tier4",
                        "REQ_KW_Armor_Resistance_Pierce_Tier5"];
const rangedKeywords = ["REQ_KW_Armor_Resistance_Ranged_None",
                        "REQ_KW_Armor_Resistance_Ranged_Tier1",
                        "REQ_KW_Armor_Resistance_Ranged_Tier2",
                        "REQ_KW_Armor_Resistance_Ranged_Tier3",
                        "REQ_KW_Armor_Resistance_Ranged_Tier4",
                        "REQ_KW_Armor_Resistance_Ranged_Tier5"];
const slashKeywords = ["REQ_KW_Armor_Resistance_Slash_None",
                       "REQ_KW_Armor_Resistance_Slash_Tier1",
                       "REQ_KW_Armor_Resistance_Slash_Tier2",
                       "REQ_KW_Armor_Resistance_Slash_Tier3",
                       "REQ_KW_Armor_Resistance_Slash_Tier4",
                       "REQ_KW_Armor_Resistance_Slash_Tier5"];
const temperingKeywords = ["REQ_Tempering_AdvancedBlacksmithing",
                           "REQ_Tempering_AdvancedLightArmors",
                           "REQ_Tempering_Craftsmanship",
                           "REQ_Tempering_DaedricSmithing",
                           "REQ_Tempering_DraconicSmithing",
                           "REQ_Tempering_DwarvenSmithing",
                           "REQ_Tempering_EbonySmithing",
                           "REQ_Tempering_ElvenSmithing",
                           "REQ_Tempering_GlassSmithing",
                           "REQ_Tempering_OrcishSmithing"];

for (let armor of armors) {
	armor = xelib.GetWinningOverride(armor);
	const hasTemplate = xelib.HasElement(armor, "TNAM");
	if (!hasTemplate) {
		if (xelib.HasKeyword(armor, "ArmorCuirass")) {
			const bluntCount = getKeywordCount(armor, bluntKeywords);
			if (bluntCount !== 1) {
				logMessage(`${xelib.LongName(armor)} has ${bluntCount} blunt resistance keywords`);
			}
			const pierceCount = getKeywordCount(armor, pierceKeywords);
			if (pierceCount !== 1) {
				logMessage(`${xelib.LongName(armor)} has ${pierceCount} pierce resistance keywords`);
			}
			const rangedCount = getKeywordCount(armor, rangedKeywords);
			if (rangedCount !== 1) {
				logMessage(`${xelib.LongName(armor)} has ${rangedCount} ranged resistance keywords`);
			}
			const slashCount = getKeywordCount(armor, slashKeywords);
			if (slashCount !== 1) {
				logMessage(`${xelib.LongName(armor)} has ${slashCount} slash resistance keywords`);
			}
		}
		if (xelib.GetArmorType(armor) !== "Clothing" && TTLib.IsPlayable(armor)) {
			const temperingCount = getKeywordCount(armor, temperingKeywords);
			if (temperingCount !== 1) {
				logMessage(`${xelib.LongName(armor)} has ${temperingCount} tempering keywords`);
			}
		}
	}
	addProgress(1);
}


function getKeywordCount(armor, keywords) {
	let count = 0;
	for (const keyword of keywords)
	{
		if (xelib.HasKeyword(armor, keyword)) {
			count++;
		}
	}
	return count;
}