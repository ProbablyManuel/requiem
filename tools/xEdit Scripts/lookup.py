import pandas as pd


def _read_form_by_editor_id(path: str) -> dict[str, str]:
    s = pd.read_csv(path, header=None, index_col=0).squeeze()
    s.set_flags(allows_duplicate_labels=False)
    return s


def _read_form_by_full_name(path: str) -> dict[str, str]:
    ignore = [
        "Dragonborn.esm:03CF16",  # Non-playable firewood
        "Skyrim.esm:043E27",  # Wylandriah's Orichalcum Ingot
        "Skyrim.esm:0BFB09",  # Alternate Charcoal
    ]
    s = pd.read_csv(path, header=None, index_col=0).squeeze()
    s = s[~s.isin(ignore)]
    s = s[~s.index.duplicated(keep=False)]
    return s


_form_by_editor_id = _read_form_by_editor_id("patcher_data\\FormsByEditorID.csv")
_form_by_full_name = _read_form_by_full_name("patcher_data\\FormsByFullName.csv")

_load_order = [
    "Skyrim.esm",
    "Update.esm",
    "Dawnguard.esm",
    "HearthFires.esm",
    "Dragonborn.esm",
    "ccasvsse001-almsivi.esm",
    "ccBGSSSE001-Fish.esm",
    "ccbgssse002-exoticarrows.esl",
    "ccbgssse003-zombies.esl",
    "ccbgssse004-ruinsedge.esl",
    "ccbgssse005-goldbrand.esl",
    "ccbgssse006-stendarshammer.esl",
    "ccbgssse007-chrysamere.esl",
    "ccbgssse010-petdwarvenarmoredmudcrab.esl",
    "ccbgssse011-hrsarmrelvn.esl",
    "ccbgssse012-hrsarmrstl.esl",
    "ccbgssse014-spellpack01.esl",
    "ccbgssse019-staffofsheogorath.esl",
    "ccbgssse020-graycowl.esl",
    "ccbgssse021-lordsmail.esl",
    "ccmtysse001-knightsofthenine.esl",
    "ccQDRSSE001-SurvivalMode.esl",
    "cctwbsse001-puzzledungeon.esm",
    "cceejsse001-hstead.esm",
    "ccqdrsse002-firewood.esl",
    "ccbgssse018-shadowrend.esl",
    "ccbgssse035-petnhound.esl",
    "ccfsvsse001-backpacks.esl",
    "cceejsse002-tower.esl",
    "ccedhsse001-norjewel.esl",
    "ccvsvsse002-pets.esl",
    "ccBGSSSE037-Curios.esl",
    "ccbgssse034-mntuni.esl",
    "ccbgssse045-hasedoki.esl",
    "ccbgssse008-wraithguard.esl",
    "ccbgssse036-petbwolf.esl",
    "ccffbsse001-imperialdragon.esl",
    "ccmtysse002-ve.esl",
    "ccbgssse043-crosselv.esl",
    "ccvsvsse001-winter.esl",
    "cceejsse003-hollow.esl",
    "ccbgssse016-umbra.esm",
    "ccbgssse031-advcyrus.esm",
    "ccbgssse038-bowofshadows.esl",
    "ccbgssse040-advobgobs.esl",
    "ccbgssse050-ba_daedric.esl",
    "ccbgssse052-ba_iron.esl",
    "ccbgssse054-ba_orcish.esl",
    "ccbgssse058-ba_steel.esl",
    "ccbgssse059-ba_dragonplate.esl",
    "ccbgssse061-ba_dwarven.esl",
    "ccpewsse002-armsofchaos.esl",
    "ccbgssse041-netchleather.esl",
    "ccedhsse002-splkntset.esl",
    "ccbgssse064-ba_elven.esl",
    "ccbgssse063-ba_ebony.esl",
    "ccbgssse062-ba_dwarvenmail.esl",
    "ccbgssse060-ba_dragonscale.esl",
    "ccbgssse056-ba_silver.esl",
    "ccbgssse055-ba_orcishscaled.esl",
    "ccbgssse053-ba_leather.esl",
    "ccbgssse051-ba_daedricmail.esl",
    "ccbgssse057-ba_stalhrim.esl",
    "ccbgssse066-staves.esl",
    "ccbgssse067-daedinv.esm",
    "ccbgssse068-bloodfall.esl",
    "ccbgssse069-contest.esl",
    "ccvsvsse003-necroarts.esl",
    "ccvsvsse004-beafarmer.esl",
    "ccBGSSSE025-AdvDSGS.esm",
    "ccffbsse002-crossbowpack.esl",
    "ccbgssse013-dawnfang.esl",
    "ccrmssse001-necrohouse.esl",
    "ccedhsse003-redguard.esl",
    "cceejsse004-hall.esl",
    "cceejsse005-cave.esm",
    "cckrtsse001_altar.esl",
    "cccbhsse001-gaunt.esl",
    "ccafdsse001-dwesanctuary.esm",
    "Unofficial Skyrim Special Edition Patch.esp",
    "Unofficial Skyrim Creation Club Content Patch.esl",
    "SkyUI_SE.esp",
    "Requiem.esp",
]


def form_by_editor_id(editor_id: str) -> str:
    return _form_by_editor_id[editor_id]


def form_by_full_name(full_name: str) -> str:
    return _form_by_full_name[full_name]


def form_to_load_order_form_id(form: str) -> str:
    plugin, fixed_form_id = form.split(":")
    return f'{_load_order.index(plugin):02x}{fixed_form_id}'
