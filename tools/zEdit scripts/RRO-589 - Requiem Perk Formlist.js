const scriptPath = `${xelib.GetGlobal("ProgramPath")}\\scripts\\`;
const Database = require(`${scriptPath}Database.js`);

const perks = xelib.GetRecords(0, "PERK", false);

const {showProgress, logMessage, progressTitle, addProgress, progressMessage} = zedit.progressService;
showProgress({
	determinate: true,
	title: "Requiem Perk Formlist",
	message : "",
	canClose: true,
	current: 0,
	max: perks.length
});

for (let perk of perks) {
	perk = xelib.GetWinningOverride(perk);
	const editorID = xelib.EditorID(perk);
	const regex = /REQ_([^_]+)_[^_]+/;
	const match = editorID.match(regex);
	if (match) {
		const skill = RequiemSkillToSkyrimSkill(match[1]);
		if (skill) {
			const formlist = Database.PerkFormListBySkill(skill);
			const formlistOverride = xelib.GetWinningOverride(formlist);
			const perkHexFormID = xelib.GetHexFormID(perk);
			if (!xelib.HasFormID(formlistOverride, perkHexFormID)) {
				xelib.AddFormID(formlistOverride, perkHexFormID);
			}
		}
	}
	addProgress(1);
}


function RequiemSkillToSkyrimSkill(skill) {
	skill = skill.toLowerCase();

	switch (skill) {
		case "alchemy" : return "alchemy";
		case "alteration" : return "alteration";
		case "block" : return "block";
		case "conjuration" : return "conjuration";
		case "destruction" : return "destruction";
		case "enchanting" : return "enchanting";
		case "evasion" : return "light armor";
		case "heavyarmor" : return "heavy armor";
		case "illusion" : return "illusion";
		case "lockpicking" : return "lockpicking";
		case "marksmanship" : return "archery";
		case "onehanded" : return "one-handed";
		case "pickpocket" : return "pickpocket";
		case "restoration" : return "restoration";
		case "smithing" : return "smithing";
		case "sneak" : return "sneak";
		case "speech" : return "speech";
		case "twohanded" : return "two-handed";
		default: return 0;
	}
}
