const prefix = 'REQ_Ench_';
const allowMultipleEffect = false;

let selected = zedit.GetSelectedRecords('ENCH');
let {showProgress, logMessage, progressTitle, addProgress, progressMessage} = zedit.progressService;
showProgress({
	determinate: true,
	title: 'Name ENCH',
	message : '',
	canClose: true,
	current: 0,
	max: selected.length
});

for (let rec of selected) {
	// We assume simple enchantments, i.e. only a single effect
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
	// Remove rank suffix from the effect's editorID if present
	let rank = '';
	if (effectEditorID.match(/.*\d/g)) {
		rank = effectEditorID.slice(-1);
		effectEditorID = effectEditorID.slice(0, -1);
	}
	// The suffix is "Base" + rank or an integer that denotes the strength
	// If the enchantment has a strength, it should already be in the editorID
	let suffix = xelib.EditorID(rec).slice(-2);
	if (!suffix.match(/\d{2}/g)) {
		suffix = '';
	}
	if (xelib.GetUIntValue(rec, 'ENIT\\Base Enchantment') === 0) {
		suffix = `_Base${rank}`;
	}
	else if (suffix === '') {
		logMessage(`${xelib.LongName(rec)} has no suffix`);
	}
	// The editorID of an enchament is the editorID of its effect and the suffix
	let newEditorID = effectEditorID + suffix;
	// The name of an enchantment is identical to the name of its effect
	let newName = xelib.FullName(effect);
	xelib.SetValue(rec, 'EDID', newEditorID);
	xelib.SetValue(rec, 'FULL', newName);
	addProgress(1);
}
