import itertools
import json
import pandas as pd

import lookup
from strict_dict import strict_dict


armor_parts = pd.read_excel(
    "patcher_data/Armor.xlsx",
    sheet_name="CraftingQuantities",
    usecols=[0]).squeeze("columns").convert_dtypes()
weapon_types = pd.read_excel(
    "patcher_data/Weapon.xlsx",
    sheet_name="CraftingQuantities",
    usecols=[0]).squeeze("columns").convert_dtypes()
potion_types = pd.read_excel(
    "patcher_data/Alchemy.xlsx",
    sheet_name="Potions",
    usecols=[0]).squeeze("columns").convert_dtypes()
with open("patcher_data/leveled_armor.json") as fh:
    leveled_armors = json.load(fh)
with open("patcher_data/leveled_weapon.json") as fh:
    leveled_weapons = json.load(fh)
with open("patcher_data/leveled_potion.json") as fh:
    leveled_potions = json.load(fh)

leveled_items = strict_dict()

replace = {
    "Heavy_Chitin_Shield": "Heavy_Iron_Shield_Dented",
    "Light_NetchLeather_Body": "LI_Light_NetchLeather_Body",
    "Light_NetchLeather_Head": "LI_Light_NetchLeather_Head",
    "Light_NetchLeather_Shield": "LI_Light_NetchLeather_Shield",
}


def generate_leveled_items(editor_id_template: str,
                           quantities: dict[str: int],
                           specifier_list: list[str]):
    for specifier in specifier_list:
        items = []
        for item_template, count in quantities.items():
            item = item_template.format(specifier)
            if item in replace:
                item = replace[item]
            try:
                form = lookup.form_by_editor_id("REQ_" + item)
            except KeyError:
                continue
            data = ["1", form, "1"]
            items += [data.copy() for _ in range(count)]
        items.sort(key=lambda x: lookup.form_to_load_order_form_id(x[1]))
        key = editor_id_template.format(specifier)
        value = ",".join(itertools.chain.from_iterable(items))
        leveled_items[key] = value


for editor_id, quantities in leveled_armors.items():
    generate_leveled_items(editor_id, quantities, armor_parts)
for editor_id, quantities in leveled_weapons.items():
    generate_leveled_items(editor_id, quantities, weapon_types)
for editor_id, quantities in leveled_potions.items():
    generate_leveled_items(editor_id, quantities, potion_types)

with open("REQ_LeveledItemPatcher.txt", "w") as fh:
    for editor_id, conditions in sorted(leveled_items.items()):
        fh.write(f'{editor_id}={conditions}\n')
