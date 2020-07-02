const prefix = 'REQ_Alch_';
const allowMultipleEffect = true;

let selected = zedit.GetSelectedRecords('ALCH');
let {showProgress, logMessage, progressTitle, addProgress, progressMessage} = zedit.progressService;
showProgress({
    determinate: true,
    title: 'Name ALCH',
    message : '',
    canClose: true,
    current: 0,
    max: selected.length
});

for (let rec of selected) {
	// We assume simple potions, i.e. only a single effect
	if (!allowMultipleEffect && xelib.HasElement(rec, 'Effects\\[1]')) {
		logMessage(`Skipping ${xelib.LongName(rec)} because it has multiple effects`);
		addProgress(1);
		continue;
	}
	// We only look at effects with a proper editorID
	let effect = xelib.GetWinningOverride(xelib.GetLinksTo(rec, 'Effects\\[0]\\EFID'));
	let effectEditorID = xelib.EditorID(effect);
	if (!effectEditorID.startsWith(prefix)) {
		logMessage(`Skipping ${xelib.LongName(rec)} because prefix doesn't match`);
		addProgress(1);
		continue;
	}
	// The suffix is an integer that denotes the strength of the potion/poison
	let suffix = xelib.EditorID(rec).slice(-2);
	if (!suffix.match(/0[1-5]/g)) {
		suffix = '';
	}
	// The editorID of a potion/poison is the effect's EditorID and the suffix
	// Its prefix is updated to denote whether it is a potion or poison
	let newEditorID = xelib.EditorID(effect) + suffix;
	if (xelib.GetFlag(rec, 'ENIT\\Flags', 'Poison')) {
		newEditorID = newEditorID.replace(prefix, 'REQ_Poison_');
	}
	else {
		newEditorID = newEditorID.replace(prefix, 'REQ_Potion_');
	}
	// The name of the potion/poison consists of the name of the effect and
	// optionally a label that denotes the strength of the potion
	if (xelib.GetFlag(rec, 'ENIT\\Flags', 'Poison')) {
		var newName = 'Poison of ' + xelib.FullName(effect);
	}
	else {
		var newName = 'Potion of ' + xelib.FullName(effect);
	}
	if (newEditorID.endsWith('Complete')) {
		var label = 'Surpassing';
	}
	else {
		var label = getLabel(parseInt(suffix, 10));
	}
	if (label !== '') {
		newName = `${newName} (${label})`;
	}
	xelib.SetValue(rec, 'EDID', newEditorID);
	xelib.SetValue(rec, 'FULL', newName);
	addProgress(1);
}

function getLabel(level) {
	switch (level) {
		case 1:
			return 'Feeble';
		case 2:
			return 'Faint';
		case 3:
			return 'Fair';
		case 4:
			return 'Good';
		case 5:
			return 'Remarkable';
		default:
			return '';
	}
}
