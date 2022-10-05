import pandas as pd


name_to_form = {
    "Amber": "ccBGSSSE025-AdvDSGS.esm:000BC7",
    "Black Soul Gem": "Skyrim.esm:02E500",
    "Bone Meal": "Skyrim.esm:034CDD",
    "Chaurus Chitin": "Skyrim.esm:03AD57",
    "Chitin Plate": "Dragonborn.esm:02B04E",
    "Corundum": "Skyrim.esm:05AD93",
    "Daedra Heart": "Skyrim.esm:03AD5B",
    "Dragon Bone": "Skyrim.esm:03ADA4",
    "Dragon Scale": "Skyrim.esm:03ADA3",
    "Dwarven": "Skyrim.esm:0DB8A2",
    "Ebony Battleaxe": "Skyrim.esm:0139AC",
    "Ebony Battlestaff": "Requiem.esp:06B47E",
    "Ebony Body": "Skyrim.esm:013961",
    "Ebony Bow": "Skyrim.esm:0139AD",
    "Ebony Club": "Skyrim.esm:000000",
    "Ebony Crossbow": "Requiem.esp:8F7F90",
    "Ebony Dagger": "Skyrim.esm:0139AE",
    "Ebony DaiKatana": "Requiem.esp:ADDE11",
    "Ebony Feet": "Skyrim.esm:013960",
    "Ebony Greatsword": "Skyrim.esm:0139AF",
    "Ebony Hands": "Skyrim.esm:013962",
    "Ebony Hatchet": "Skyrim.esm:000000",
    "Ebony Head": "Skyrim.esm:013963",
    "Ebony Katana": "Requiem.esp:ADDE0E",
    "Ebony LongMace": "Skyrim.esm:000000",
    "Ebony Mace": "Skyrim.esm:0139B0",
    "Ebony Maul": "Skyrim.esm:000000",
    "Ebony Quarterstaff": "Skyrim.esm:000000",
    "Ebony Shield": "Skyrim.esm:013964",
    "Ebony Shortsword": "Skyrim.esm:000000",
    "Ebony Sword": "Skyrim.esm:0139B1",
    "Ebony Tanto": "Requiem.esp:ADDE10",
    "Ebony Wakizashi": "Requiem.esp:ADDE0F",
    "Ebony WarAxe": "Skyrim.esm:0139AB",
    "Ebony Warhammer": "Skyrim.esm:0139B2",
    "Ebony": "Skyrim.esm:05AD9D",
    "Ectoplasm": "Skyrim.esm:03AD63",
    "Gold": "Skyrim.esm:05AD9E",
    "Iron": "Skyrim.esm:05ACE4",
    "Leather Strips": "Skyrim.esm:0800E4",
    "Leather": "Skyrim.esm:0DB5D2",
    "Madness": "ccBGSSSE025-AdvDSGS.esm:000BC8",
    "Malachite": "Skyrim.esm:05ADA1",
    "Moonstone": "Skyrim.esm:05AD9F",
    "Netch Jelly": "Dragonborn.esm:01CD72",
    "Netch Leather": "Dragonborn.esm:01CD7C",
    "Orichalcum": "Skyrim.esm:05AD99",
    "Quicksilver": "Skyrim.esm:05ADA0",
    "Silver": "Skyrim.esm:05ACE3",
    "Stalhrim": "Dragonborn.esm:02B06B",
    "Steel": "Skyrim.esm:05ACE5",
    "Void Salts": "Skyrim.esm:03AD60",
    "Wood": "Skyrim.esm:06F993",
}

load_order_index = {
    "Skyrim.esm": "00",
    "Dragonborn.esm": "04",
    "ccBGSSSE025-AdvDSGS.esm": "0C",
    "Requiem.esp": "11",
}


def pair_to_load_order_form_id(pair: str):
    plugin, form_id = pair.split(":")
    return load_order_index[plugin] + form_id


skyforge_sets = ["Heavy_AncientNord", "NordHero", "SkyforgeSteel"]

weapon_materials = pd.read_excel("../Spreadsheet/Weapon.xlsx", sheet_name="CraftingMaterials", index_col=0, keep_default_na=False)
weapon_materials["Temper"].mask(weapon_materials["Temper"] == "", weapon_materials["Primary"], inplace=True)
weapon_quantities = pd.read_excel("../Spreadsheet/Weapon.xlsx", sheet_name="CraftingQuantities", index_col=0)
weapon_artifacts = pd.read_excel("../Spreadsheet/Weapon.xlsx", sheet_name="Artifacts", index_col=0).dropna()

recipes = {}
for weapon_set, materials in weapon_materials.iterrows():
    for weapon_type, quantities in weapon_quantities.iterrows():
        if materials["Primary"]:
            if weapon_set in skyforge_sets:
                editor_id = f'Skyforge_Weapon_{weapon_set}_{weapon_type}'
            else:
                editor_id = f'Forge_Weapon_{weapon_set}_{weapon_type}'
            if materials["Secondary"]:
                ingredients = [
                    f'{name_to_form[materials["Primary"]]} {quantities["Primary"]}',
                    f'{name_to_form[materials["Secondary"]]} {quantities["Secondary"]}'
                ]
            else:
                ingredients = [
                    f'{name_to_form[materials["Primary"]]} {quantities["Primary"] + quantities["Secondary"]}'
                ]
            ingredients.append(f'{name_to_form["Leather Strips"]} {quantities["Leather"]}')
            ingredients.sort(key=pair_to_load_order_form_id)
            recipes[editor_id] = " ".join(ingredients)
        editor_id = f'Temper_Weapon_{weapon_set}_{weapon_type}'
        recipes[editor_id] = f'{name_to_form[materials["Temper"]]} 1'
    for weapon_type in weapon_quantities.index:
        editor_id = f'Forge_Weapon_Daedric_{weapon_type}'
        ingredients = [
            f'{name_to_form["Ebony " + weapon_type]} 1',
            f'{name_to_form["Daedra Heart"]} 1',
            f'{name_to_form["Black Soul Gem"]} 1',
        ]
        ingredients.sort(key=pair_to_load_order_form_id)
        recipes[editor_id] = " ".join(ingredients)
for artifact, rows in weapon_artifacts.iterrows():
    editor_id = f'Temper_Artifact_{artifact}'
    recipes[editor_id] = recipes[f'Temper_Weapon_{rows["Base"]}']


armor_materials = pd.read_excel("../Spreadsheet/Armor.xlsx", sheet_name="CraftingMaterials", index_col=0, keep_default_na=False)
armor_materials["Temper"].mask(armor_materials["Temper"] == "", armor_materials["Primary"], inplace=True)
armor_quantities = pd.read_excel("../Spreadsheet/Armor.xlsx", sheet_name="CraftingQuantities", index_col=0)
armor_artifacts = pd.read_excel("../Spreadsheet/Armor.xlsx", sheet_name="Artifacts", index_col=0).dropna()

for armor_set, materials in armor_materials.iterrows():
    for armor_type, quantities in armor_quantities.iterrows():
        if materials["Primary"]:
            if armor_set in skyforge_sets:
                editor_id = f'Skyforge_{armor_set}_{armor_type}'
            else:
                editor_id = f'Forge_{armor_set}_{armor_type}'
            if materials["Secondary"]:
                ingredients = [
                    f'{name_to_form[materials["Primary"]]} {quantities["Primary"]}',
                    f'{name_to_form[materials["Secondary"]]} {quantities["Secondary"]}'
                ]
            else:
                ingredients = [
                    f'{name_to_form[materials["Primary"]]} {quantities["Primary"] + quantities["Secondary"]}'
                ]
            if armor_set == "Heavy_ImprovedBonemold":
                ingredients.append(f'{name_to_form["Netch Jelly"]} 1')
                ingredients.append(f'{name_to_form["Stalhrim"]} 1')
                ingredients.append(f'{name_to_form["Void Salts"]} 1')
            ingredients.append(f'{name_to_form[materials["Leather"]]} {quantities["Leather"]}')
            ingredients.sort(key=pair_to_load_order_form_id)
            recipes[editor_id] = " ".join(ingredients)
        editor_id = f'Temper_{armor_set}_{armor_type}'
        recipes[editor_id] = f'{name_to_form[materials["Temper"]]} 1'
    for armor_type in armor_quantities.index:
        editor_id = f'Forge_Heavy_Daedric_{armor_type}'
        ingredients = [
            f'{name_to_form["Ebony " + armor_type]} 1',
            f'{name_to_form["Daedra Heart"]} 1',
            f'{name_to_form["Black Soul Gem"]} 1',
        ]
        ingredients.sort(key=pair_to_load_order_form_id)
        recipes[editor_id] = " ".join(ingredients)
for artifact, rows in armor_artifacts.iterrows():
    editor_id = f'Temper_Artifact_{artifact}'
    recipes[editor_id] = recipes[f'Temper_{rows["Base"]}']

with open("REQ_RecipePatcher.txt", "w") as fh:
    for editor_id, ingredients in sorted(recipes.items()):
        fh.write(f'{editor_id}={ingredients}\n')
