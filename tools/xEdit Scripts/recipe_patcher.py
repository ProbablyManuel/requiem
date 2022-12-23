import pandas as pd
import sys

from lookup import form_id_pair_by_editor_id, form_id_pair_to_load_order_form_id
from strict_dict import strict_dict


name_to_form = {
    "Amber": "ccBGSSSE025-AdvDSGS.esm:000BC7",
    "Black Soul Gem": "Skyrim.esm:02E500",
    "Bone Meal": "Skyrim.esm:034CDD",
    "Charcoal": "Skyrim.esm:033760",
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
    "Ebony Quarterstaff": "Requiem.esp:ADDF28",
    "Ebony Shield": "Skyrim.esm:013964",
    "Ebony Shortsword": "Requiem.esp:ADDEF6",
    "Ebony Smithing": "Skyrim.esm:0CB412",
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


def ingredients_sort_key(s: str) -> str:
    form_id_pair, quantity = s.split(",")
    return f'{form_id_pair_to_load_order_form_id(form_id_pair)},{quantity}'


def get_smithing_perk(smithing_perk: str):
    editor_id = f'REQ_Smithing_{smithing_perk.replace(" ", "")}'
    return form_id_pair_by_editor_id(editor_id)


def get_conditions(perk: str, editor_id: str) -> str:
    if perk == "Amber Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Glass Smithing")},0,Subject',
            "10000000,1.000000,GetGlobalValue,ccBGSSSE025-AdvDSGS.esm:000C02,00 00 00 00,Subject",
        ]
    elif perk == "Daedric Smithing":
        conditions = [
            "01000000,23.000000,GetCurrentTime,00 00 00 00,00 00 00 00,Subject",
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Daedric Smithing")},0,Subject',
        ]
    elif perk == "Dark Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Daedric Smithing")},0,Subject',
            "10000000,1.000000,GetGlobalValue,ccBGSSSE025-AdvDSGS.esm:00086C,00 00 00 00,Subject",
        ]
    elif perk == "Dragonbone Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Ebony Smithing")},0,Subject',
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Draconic Blacksmithing")},0,Subject',
        ]
    elif perk == "Dragonscale Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Glass Smithing")},0,Subject',
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Draconic Blacksmithing")},0,Subject',
        ]
    elif perk == "Dwarven Smithing":
        if editor_id.startswith("Temper"):
            conditions = [
                f'10010000,1.000000,HasPerk,{form_id_pair_by_editor_id("REQ_Reward_AncientKnowledge_Perk")},0,Subject',
                f'10000000,1.000000,HasPerk,{get_smithing_perk("Dwarven Smithing")},0,Subject',
            ]
        else:
            conditions = [
                f'10000000,1.000000,HasPerk,{get_smithing_perk("Dwarven Smithing")},0,Subject'
            ]
    elif perk == "Golden Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Daedric Smithing")},0,Subject',
            "10000000,1.000000,GetGlobalValue,ccBGSSSE025-AdvDSGS.esm:00086B,00 00 00 00,Subject",
        ]
    elif perk == "Improved Bonemold Smithing":
        conditions = [
            "10000000,1.000000,GetGlobalValue,Dragonborn.esm:03AB27,00 00 00 00,Subject",
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Craftsmanship")},0,Subject',
        ]
    elif perk == "Madness Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Ebony Smithing")},0,Subject',
            "10000000,1.000000,GetGlobalValue,ccBGSSSE025-AdvDSGS.esm:000C03,00 00 00 00,Subject",
        ]
    elif perk == "Morrowind Smithing":
        conditions = [
            "10000000,1.000000,GetStageDone,Dragonborn.esm:017F8E,20,Subject",
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Craftsmanship")},0,Subject',
        ]
    elif perk == "Skyforge Smithing":
        if not editor_id.startswith("Temper"):
            conditions = [
                "10000000,1.000000,GetGlobalValue,Skyrim.esm:0F46D1,00 00 00 00,Subject",
                f'10000000,1.000000,HasPerk,{get_smithing_perk("Craftsmanship")},0,Subject',
            ]
        else:
            conditions = [
                f'10000000,1.000000,HasPerk,{get_smithing_perk("Craftsmanship")},0,Subject'
            ]
    elif perk == "Stalhrim Smithing":
        conditions = [
            "10000000,1.000000,GetStageDone,Dragonborn.esm:01CAF1,200,Subject",
            f'10000000,1.000000,HasPerk,{get_smithing_perk("Ebony Smithing")},0,Subject',
        ]
    else:
        conditions = [
            f'10000000,1.000000,HasPerk,{get_smithing_perk(perk)},0,Subject'
        ]
    if editor_id.endswith("Weapon_Dawnguard_Crossbow"):
        conditions.append(
            "10000000,0.000000,GetGlobalValue,Dawnguard.esm:00398A,00 00 00 00,Subject")
    elif editor_id.endswith("Weapon_DawnguardImproved_Crossbow"):
        conditions.append(
            "10000000,0.000000,GetGlobalValue,Dawnguard.esm:00F19C,00 00 00 00,Subject")
    elif editor_id.endswith("Weapon_DwemerImproved_Crossbow"):
        conditions.append(
            "10000000,0.000000,GetGlobalValue,Dawnguard.esm:00F19D,00 00 00 00,Subject")
    elif editor_id.endswith("Weapon_Dwemer_Crossbow"):
        conditions.append(
            "10000000,0.000000,GetGlobalValue,Dawnguard.esm:00F19A,00 00 00 00,Subject")
    return ",".join(conditions)


armor_materials = pd.read_excel(
    "patcher_data/Armor.xlsx",
    sheet_name="Armors",
    index_col=0,
    usecols=[
        "Unnamed: 0",
        "Primary",
        "Secondary",
        "Leather",
        "Temper",
        "Perk"]).convert_dtypes().dropna(how="all")
armor_materials["Temper"].mask(
    armor_materials["Temper"].isna(),
    armor_materials["Primary"],
    inplace=True)
armor_quantities = pd.read_excel(
    "patcher_data/Armor.xlsx",
    sheet_name="CraftingQuantities",
    index_col=0).convert_dtypes()
armor_artifacts = pd.read_excel(
    "patcher_data/Armor.xlsx",
    sheet_name="Artifacts",
    index_col=0,
    usecols=[
        "Unnamed: 0",
        "Base",
        "Divine"]).convert_dtypes().dropna()
if len(sys.argv) == 3:
    armor_variants = pd.read_csv(
        sys.argv[1],
        header=None,
        index_col=0).convert_dtypes().squeeze()
else:
    armor_variants = pd.Series(dtype=str)

weapon_materials = pd.read_excel(
    "patcher_data/Weapon.xlsx",
    sheet_name="Weapons",
    index_col=0,
    usecols=[
        "Unnamed: 0",
        "Primary",
        "Secondary",
        "Temper",
        "Perk"]).convert_dtypes().dropna(
            how="all")
weapon_materials["Temper"].mask(
    weapon_materials["Temper"].isna(),
    weapon_materials["Primary"],
    inplace=True)
weapon_quantities = pd.read_excel(
    "patcher_data/Weapon.xlsx",
    sheet_name="CraftingQuantities",
    index_col=0).convert_dtypes()
weapon_artifacts = pd.read_excel(
    "patcher_data/Weapon.xlsx",
    sheet_name="Artifacts",
    index_col=0,
    usecols=[
        "Unnamed: 0",
        "Base",
        "Divine"]).convert_dtypes().dropna()
if len(sys.argv) == 3:
    weapon_variants = pd.read_csv(
        sys.argv[2],
        header=None,
        index_col=0).convert_dtypes().squeeze()
else:
    weapon_variants = pd.Series(dtype=str)


recipes_ingredients = strict_dict()
recipes_conditions = strict_dict()

for armor_set, materials in armor_materials.iterrows():
    for armor_part, quantities in armor_quantities.iterrows():
        if pd.notna(materials["Primary"]):
            if materials["Perk"] == "Skyforge Smithing":
                editor_id = f'Skyforge_{armor_set}_{armor_part}'
            else:
                editor_id = f'Forge_{armor_set}_{armor_part}'
            ingredients = [
                f'{name_to_form[materials["Primary"]]},{quantities["Primary"]}',
                f'{name_to_form[materials["Leather"]]},{quantities["Leather"]}',
            ]
            if pd.notna(materials["Secondary"]):
                ingredients.append(
                    f'{name_to_form[materials["Secondary"]]},{quantities["Secondary"]}')
            if armor_set == "Heavy_ImprovedBonemold":
                ingredients.append(f'{name_to_form["Netch Jelly"]},1')
                ingredients.append(f'{name_to_form["Stalhrim"]},1')
                ingredients.append(f'{name_to_form["Void Salts"]},1')
            ingredients.sort(key=ingredients_sort_key)
            conditions = get_conditions(materials["Perk"], editor_id)
            recipes_ingredients[editor_id] = ",".join(ingredients)
            recipes_conditions[editor_id] = conditions
        editor_id = f'Temper_{armor_set}_{armor_part}'
        conditions = get_conditions(materials["Perk"], editor_id)
        recipes_ingredients[editor_id] = f'{name_to_form[materials["Temper"]]},1'
        recipes_conditions[editor_id] = conditions
for armor_part in armor_quantities.index:
    editor_id = f'Forge_Heavy_Daedric_{armor_part}'
    ingredients = [
        f'{name_to_form["Ebony " + armor_part]},1',
        f'{name_to_form["Daedra Heart"]},1',
        f'{name_to_form["Black Soul Gem"]},1',
    ]
    ingredients.sort(key=ingredients_sort_key)
    conditions = get_conditions("Daedric Smithing", editor_id)
    recipes_ingredients[editor_id] = ",".join(ingredients)
    recipes_conditions[editor_id] = conditions
for artifact, rows in armor_artifacts.iterrows():
    editor_id = f'Temper_Artifact_{artifact}'
    ingredients = recipes_ingredients[f'Temper_{rows["Base"]}']
    if rows["Divine"]:
        conditions = get_conditions("Legendary Blacksmithing", editor_id)
    else:
        conditions = recipes_conditions[f'Temper_{rows["Base"]}']
    recipes_ingredients[editor_id] = ingredients
    recipes_conditions[editor_id] = conditions
for variant, template in armor_variants.items():
    for armor_part in armor_quantities.index:
        for crafting_type in ("Forge", "Temper"):
            editor_id = f'{crafting_type}_{variant}_{armor_part}'
            template_key = f'{crafting_type}_{template}_{armor_part}'
            recipes_ingredients[editor_id] = recipes_ingredients[template_key]
            recipes_conditions[editor_id] = recipes_conditions[template_key]

for weapon_set, materials in weapon_materials.iterrows():
    for weapon_type, quantities in weapon_quantities.iterrows():
        if pd.notna(materials["Primary"]):
            if materials["Perk"] == "Skyforge Smithing":
                editor_id = f'Skyforge_Weapon_{weapon_set}_{weapon_type}'
            else:
                editor_id = f'Forge_Weapon_{weapon_set}_{weapon_type}'
            ingredients = [
                f'{name_to_form[materials["Primary"]]},{quantities["Primary"]}',
                f'{name_to_form["Leather Strips"]},{quantities["Leather"]}',
            ]
            if pd.notna(materials["Secondary"]):
                ingredients.append(
                    f'{name_to_form[materials["Secondary"]]},{quantities["Secondary"]}')
            ingredients.sort(key=ingredients_sort_key)
            conditions = get_conditions(materials["Perk"], editor_id)
            recipes_ingredients[editor_id] = ",".join(ingredients)
            recipes_conditions[editor_id] = conditions
        editor_id = f'Temper_Weapon_{weapon_set}_{weapon_type}'
        conditions = get_conditions(materials["Perk"], editor_id)
        recipes_ingredients[editor_id] = f'{name_to_form[materials["Temper"]]},1'
        recipes_conditions[editor_id] = conditions
for weapon_type in weapon_quantities.index:
    editor_id = f'Forge_Weapon_Daedric_{weapon_type}'
    ingredients = [
        f'{name_to_form["Ebony " + weapon_type]},1',
        f'{name_to_form["Daedra Heart"]},1',
        f'{name_to_form["Black Soul Gem"]},1',
    ]
    ingredients.sort(key=ingredients_sort_key)
    conditions = get_conditions("Daedric Smithing", editor_id)
    recipes_ingredients[editor_id] = ",".join(ingredients)
    recipes_conditions[editor_id] = conditions
for artifact, rows in weapon_artifacts.iterrows():
    editor_id = f'Temper_Artifact_{artifact}'
    ingredients = recipes_ingredients[f'Temper_Weapon_{rows["Base"]}']
    if rows["Divine"]:
        conditions = get_conditions("Legendary Blacksmithing", editor_id)
    else:
        conditions = recipes_conditions[f'Temper_Weapon_{rows["Base"]}']
    recipes_ingredients[editor_id] = ingredients
    recipes_conditions[editor_id] = conditions
for variant, template in weapon_variants.items():
    for armor_part in weapon_quantities.index:
        for crafting_type in ("Forge", "Temper"):
            editor_id = f'{crafting_type}_Weapon_{variant}_{armor_part}'
            template_key = f'{crafting_type}_Weapon_{template}_{armor_part}'
            recipes_ingredients[editor_id] = recipes_ingredients[template_key]
            recipes_conditions[editor_id] = recipes_conditions[template_key]

with open("REQ_RecipePatcherIngredients.txt", "w") as fh:
    for editor_id, ingredients in sorted(recipes_ingredients.items()):
        fh.write(f'{editor_id}={ingredients}\n')
with open("REQ_RecipePatcherConditions.txt", "w") as fh:
    for editor_id, conditions in sorted(recipes_conditions.items()):
        fh.write(f'{editor_id}={conditions}\n')
