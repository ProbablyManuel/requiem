let pluginToClean = xelib.FileByName('Requiem.esp');
let perks = xelib.GetRecords(pluginToClean, 'PERK', true);
let npcs = xelib.GetRecords(pluginToClean, 'NPC_', true);

let {showProgress, logMessage, progressTitle, addProgress, progressMessage} = zedit.progressService;
showProgress({
    determinate: true,
    title: 'Remove Perks',
    message : '',
    canClose: true,
    current: 0,
    max: perks.length + npcs.length
});

let nullifiedPerks = new Array();
logMessage('Nullified Perks:')
for (let perk of perks) {
	perk = xelib.GetWinningOverride(perk);
	if (xelib.EditorID(perk).startsWith('REQ_NULL')) {
		nullifiedPerks.push(xelib.GetHexFormID(perk, false, false));
		logMessage(xelib.LongName(perk));
	}
	addProgress(1);
}

let fileName = 'RRO-489.esp';
let newPlugin = xelib.FileByName(fileName);
if (newPlugin) {
	xelib.NukeFile(newPlugin);
}
else {
	newPlugin = xelib.AddFile(fileName);
}

for (let npc of npcs) {
	npc = xelib.GetWinningOverride(npc);
	let inheritsPerks = xelib.HasElement(npc, 'TPLT - Template') && xelib.GetFlag(npc, 'ACBS\\Template Flags', 'Use Spell List');
	if (!inheritsPerks) {
		let copiedToPlugin = false;  // Did we copy the npc to the plugin?
		for (let perk of nullifiedPerks) {
			if (xelib.HasPerk(npc, perk)) {
				if (!copiedToPlugin) {
					xelib.AddRequiredMasters(npc, newPlugin, false);
					npc = xelib.CopyElement(npc, newPlugin, false);
					copiedToPlugin = true;
				}
				xelib.RemovePerk(npc, perk);
			}
		}
	}
	addProgress(1);
}
