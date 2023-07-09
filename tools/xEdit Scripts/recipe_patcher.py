import pandas as pd

import lookup
from strict_dict import strict_dict


def ingredients_sort_key(s: str) -> str:
    form, quantity = s.split(",")
    return f'{lookup.form_to_load_order_form_id(form)},{quantity}'


def get_conditions(perk: str, editor_id: str) -> str:
    if perk == "Amber Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Glass Smithing")},0,Subject',
            "10000000,1.000000,GetGlobalValue,ccBGSSSE025-AdvDSGS.esm:000C02,00 00 00 00,Subject",
        ]
    elif perk == "Daedric Smithing":
        conditions = [
            "01000000,23.000000,GetCurrentTime,00 00 00 00,00 00 00 00,Subject",
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Daedric Smithing")},0,Subject',
        ]
    elif perk == "Dark Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Daedric Smithing")},0,Subject',
            "10000000,1.000000,GetGlobalValue,ccBGSSSE025-AdvDSGS.esm:00086C,00 00 00 00,Subject",
        ]
    elif perk == "Dragonbone Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Ebony Smithing")},0,Subject',
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Draconic Blacksmithing")},0,Subject',
        ]
    elif perk == "Dragonscale Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Glass Smithing")},0,Subject',
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Draconic Blacksmithing")},0,Subject',
        ]
    elif perk == "Dwarven Smithing":
        if editor_id.startswith("Temper"):
            conditions = [
                f'10010000,1.000000,HasPerk,{lookup.form_by_editor_id("REQ_Reward_AncientKnowledge_Perk")},0,Subject',
                f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Dwarven Smithing")},0,Subject',
            ]
        else:
            conditions = [
                f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Dwarven Smithing")},0,Subject'
            ]
    elif perk == "Golden Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Daedric Smithing")},0,Subject',
            "10000000,1.000000,GetGlobalValue,ccBGSSSE025-AdvDSGS.esm:00086B,00 00 00 00,Subject",
        ]
    elif perk == "Improved Bonemold Smithing":
        conditions = [
            "10000000,1.000000,GetGlobalValue,Dragonborn.esm:03AB27,00 00 00 00,Subject",
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Craftsmanship")},0,Subject',
        ]
    elif perk == "Madness Smithing":
        conditions = [
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Ebony Smithing")},0,Subject',
            "10000000,1.000000,GetGlobalValue,ccBGSSSE025-AdvDSGS.esm:000C03,00 00 00 00,Subject",
        ]
    elif perk == "Morrowind Smithing":
        conditions = [
            "10000000,1.000000,GetStageDone,Dragonborn.esm:017F8E,20,Subject",
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Craftsmanship")},0,Subject',
        ]
    elif perk == "Skyforge Smithing":
        if not editor_id.startswith("Temper"):
            conditions = [
                "10000000,1.000000,GetGlobalValue,Skyrim.esm:0F46D1,00 00 00 00,Subject",
                f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Craftsmanship")},0,Subject',
            ]
        else:
            conditions = [
                f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Craftsmanship")},0,Subject'
            ]
    elif perk == "Stalhrim Smithing":
        conditions = [
            "10000000,1.000000,GetStageDone,Dragonborn.esm:01CAF1,200,Subject",
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name("Ebony Smithing")},0,Subject',
        ]
    else:
        conditions = [
            f'10000000,1.000000,HasPerk,{lookup.form_by_full_name(perk)},0,Subject'
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


def get_breakdown_conditions() -> str:
    conditions = [
        "10000000,0.000000,GetGlobalValue,Requiem.esp:3BA1F3,00 00 00 00,Subject",
        "01000000,0.000000,GetItemCount,GetBreakdownItem(),00 00 00 00,Subject",
    ]
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
        "Breakdown",
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
    usecols=["Unnamed: 0", "Base", "Divine"]).convert_dtypes().dropna()
armor_variants = pd.read_excel(
    "patcher_data/Armor.xlsx",
    sheet_name="Patches",
    index_col=0,
    usecols=["Unnamed: 0", "Base"]).convert_dtypes().dropna().squeeze()

weapon_materials = pd.read_excel(
    "patcher_data/Weapon.xlsx",
    sheet_name="Weapons",
    index_col=0,
    usecols=[
        "Unnamed: 0",
        "Primary",
        "Secondary",
        "Temper",
        "Breakdown",
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
    usecols=["Unnamed: 0", "Base", "Divine"]).convert_dtypes().dropna()
weapon_variants = pd.read_excel(
    "patcher_data/Weapon.xlsx",
    sheet_name="Patches",
    index_col=0,
    usecols=["Unnamed: 0", "Base"]).convert_dtypes().dropna().squeeze()


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
                f'{lookup.form_by_full_name(materials["Primary"])},{quantities["Primary"]}',
                f'{lookup.form_by_full_name(materials["Leather"])},{quantities["Leather"]}',
            ]
            if pd.notna(materials["Secondary"]):
                ingredients.append(
                    f'{lookup.form_by_full_name(materials["Secondary"])},{quantities["Secondary"]}')
            if armor_set == "Heavy_ImprovedBonemold":
                ingredients.append(f'{lookup.form_by_editor_id("DLC2NetchJelly")},1')
                ingredients.append(f'{lookup.form_by_editor_id("DLC2OreStalhrim")},1')
                ingredients.append(f'{lookup.form_by_editor_id("VoidSalts")},1')
            ingredients.sort(key=ingredients_sort_key)
            conditions = get_conditions(materials["Perk"], editor_id)
            recipes_ingredients[editor_id] = ",".join(ingredients)
            recipes_conditions[editor_id] = conditions

        if pd.notna(materials["Breakdown"]):
            workbench = "Rack" if materials["Breakdown"] == "Leather" else "Smelter"
            editor_id = f'{workbench}_{armor_set}_{armor_part}'
            recipes_ingredients[editor_id] = f'{lookup.form_by_full_name(materials["Breakdown"])},{quantities["Breakdown"]}'
            recipes_conditions[editor_id] = get_breakdown_conditions()

        editor_id = f'Temper_{armor_set}_{armor_part}'
        conditions = get_conditions(materials["Perk"], editor_id)
        recipes_ingredients[editor_id] = f'{lookup.form_by_full_name(materials["Temper"])},1'
        recipes_conditions[editor_id] = conditions
for armor_part in armor_quantities.index:
    editor_id = f'Forge_Heavy_Daedric_{armor_part}'
    ingredients = [
        f'{lookup.form_by_editor_id("REQ_Heavy_Ebony_" + armor_part)},1',
        f'{lookup.form_by_editor_id("DaedraHeart")},1',
        f'{lookup.form_by_editor_id("SoulGemBlack")},1',
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
        try:
            editor_id = f'Forge_{variant}_{armor_part}'
            template_key = f'Forge_{template}_{armor_part}'
            recipes_ingredients[editor_id] = recipes_ingredients[template_key]
            recipes_conditions[editor_id] = recipes_conditions[template_key]
        except KeyError:
            pass
        editor_id = f'Temper_{variant}_{armor_part}'
        template_key = f'Temper_{template}_{armor_part}'
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
                f'{lookup.form_by_full_name(materials["Primary"])},{quantities["Primary"]}',
                f'{lookup.form_by_full_name("Leather Strips")},{quantities["Leather"]}',
            ]
            if pd.notna(materials["Secondary"]):
                ingredients.append(
                    f'{lookup.form_by_full_name(materials["Secondary"])},{quantities["Secondary"]}')
            ingredients.sort(key=ingredients_sort_key)
            conditions = get_conditions(materials["Perk"], editor_id)
            recipes_ingredients[editor_id] = ",".join(ingredients)
            recipes_conditions[editor_id] = conditions

        if pd.notna(materials["Breakdown"]):
            editor_id = f'Smelter_Weapon_{weapon_set}_{weapon_type}'
            recipes_ingredients[editor_id] = f'{lookup.form_by_full_name(materials["Breakdown"])},{quantities["Breakdown"]}'
            recipes_conditions[editor_id] = get_breakdown_conditions()

        editor_id = f'Temper_Weapon_{weapon_set}_{weapon_type}'
        conditions = get_conditions(materials["Perk"], editor_id)
        recipes_ingredients[editor_id] = f'{lookup.form_by_full_name(materials["Temper"])},1'
        recipes_conditions[editor_id] = conditions
for weapon_type in weapon_quantities.index:
    editor_id = f'Forge_Weapon_Daedric_{weapon_type}'
    try:
        ebony_item = lookup.form_by_editor_id(f'REQ_Weapon_Ebony_{weapon_type}')
    except KeyError:
        ebony_item = "Skyrim.esm:000000"
    ingredients = [
        f'{ebony_item},1',
        f'{lookup.form_by_editor_id("DaedraHeart")},1',
        f'{lookup.form_by_editor_id("SoulGemBlack")},1',
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
        try:
            editor_id = f'Forge_Weapon_{variant}_{armor_part}'
            template_key = f'Forge_Weapon_{template}_{armor_part}'
            recipes_ingredients[editor_id] = recipes_ingredients[template_key]
            recipes_conditions[editor_id] = recipes_conditions[template_key]
        except KeyError:
            pass
        editor_id = f'Temper_Weapon_{variant}_{armor_part}'
        template_key = f'Temper_Weapon_{template}_{armor_part}'
        recipes_ingredients[editor_id] = recipes_ingredients[template_key]
        recipes_conditions[editor_id] = recipes_conditions[template_key]


with open("REQ_RecipePatcherIngredients.txt", "w") as fh:
    for editor_id, ingredients in sorted(recipes_ingredients.items()):
        fh.write(f'{editor_id}={ingredients}\n')
with open("REQ_RecipePatcherConditions.txt", "w") as fh:
    for editor_id, conditions in sorted(recipes_conditions.items()):
        fh.write(f'{editor_id}={conditions}\n')
