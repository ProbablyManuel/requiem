const scriptPath = `${xelib.GetGlobal("ProgramPath")}\\scripts\\`;
const Database = require(`${scriptPath}Database.js`);
const TTLib = require(`${scriptPath}TT Library.js`);

const npcs = zedit.GetSelectedRecords("NPC_");

const {showProgress, logMessage, progressTitle, addProgress, progressMessage} = zedit.progressService;
showProgress({
	determinate: true,
	title: "Add Perks to NPCs",
	message : "",
	canClose: true,
	current: 0,
	max: npcs.length
});

const skills = [
	"Alchemy",
	"Alteration",
	"Block",
	"Conjuration",
	"Destruction",
	"Enchanting",
	"Light Armor",
	"Heavy Armor",
	// "Illusion",
	"Lockpicking",
	"Archery",
	"One-Handed",
	// "Pickpocket",
	"Restoration",
	"Smithing",
	// "Sneak",
	"Speech",
	"Two-Handed"
]

const skyrim = xelib.FileByName("Skyrim.esm");
const requiem = xelib.FileByName("Requiem.esp");
const skyrimIndex = xelib.Hex(xelib.GetFileLoadOrder(skyrim), 2);
const requiemIndex = xelib.Hex(xelib.GetFileLoadOrder(requiem), 2);

const perkBlackList = [
	`${skyrimIndex}0F2CA6`,  // Novice Alteration
	`${skyrimIndex}0C44B7`,  // Apprentice Alteration
	`${skyrimIndex}0C44B8`,  // Adept Alteration
	`${skyrimIndex}0C44B9`,  // Expert Alteration
	`${skyrimIndex}0C44BA`,  // Master Alteration
	`${skyrimIndex}0153CD`,  // Empowered Alterations
	`${skyrimIndex}0F2CA7`,  // Novice Conjuration
	`${skyrimIndex}0C44BB`,  // Apprentice Conjuration
	`${skyrimIndex}0C44BC`,  // Adept Conjuration
	`${skyrimIndex}0C44BD`,  // Expert Conjuration
	`${skyrimIndex}0C44BE`,  // Master Conjuration
	`${skyrimIndex}0153CE`,  // Summoner's Insight
	`${skyrimIndex}0F2CA8`,  // Novice Destruction
	`${skyrimIndex}0C44BF`,  // Apprentice Destruction
	`${skyrimIndex}0C44C0`,  // Adept Destruction
	`${skyrimIndex}0C44C1`,  // Expert Destruction
	`${skyrimIndex}0C44C2`,  // Master Destruction
	`${skyrimIndex}0153CF`,  // Empowered Elements
	`${skyrimIndex}0153D2`,  // Impact
	`${skyrimIndex}0F2CAA`,  // Novice Restoration
	`${skyrimIndex}0C44C7`,  // Apprentice Restoration
	`${skyrimIndex}0C44C8`,  // Adept Restoration
	`${skyrimIndex}0C44C9`,  // Expert Restoration
	`${skyrimIndex}0C44CA`,  // Master Restoration
	`${skyrimIndex}0153D1`,  // Benefactor's Insight
	`${skyrimIndex}0A3F64`,  // Power of Life
	`${requiemIndex}17E062`,  // Essence of Life
	`${requiemIndex}47B5B8`  // Painful Regrets
]

for (const npc of npcs) {
	if (!TTLib.InheritsSpellList(npc)) {
		for (const skill of skills) {
			AddPerks(npc, skill);
		}
	}
	addProgress(1);
}


function AddPerks(npc, skill) {
	const perks = Database.PerksBySkill(skill);
	const skillLevel = TTLib.GetActorSkill(npc, skill);
	if (skillLevel > 5) {
		for (const perk of perks) {
			const requiredSkill = TTLib.GetPerkRequiredSkill(perk, skill);
			if (requiredSkill === -1) {
				logMessage(`Failed to get required skill level for ${xelib.LongName(perk)}`);
			}
			else if (skillLevel >= requiredSkill) {
				const perkHexFormID = xelib.GetHexFormID(perk);
				if (!perkBlackList.includes(perkHexFormID)) {
					if (!xelib.HasPerk(npc, perkHexFormID)) {
						xelib.AddPerk(npc, perkHexFormID,  "1");
					}
				}
			}
		}
	}
}
