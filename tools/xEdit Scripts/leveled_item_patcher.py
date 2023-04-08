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
with open("patcher_data/leveled_armor.json") as fh:
    leveled_armors = json.load(fh)
with open("patcher_data/leveled_weapon.json") as fh:
    leveled_weapons = json.load(fh)

leveled_items = strict_dict()


def generate_leveled_items(editor_id_template: str,
                           quantities: dict[str: int],
                           specifier_list: list[str]):
    for specifier in specifier_list:
        items = []
        for item_template, count in quantities.items():
            item_editor_id = "REQ_" + item_template.format(specifier)
            try:
                form = lookup.form_by_editor_id(item_editor_id)
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

with open("REQ_LeveledItemPatcher.txt", "w") as fh:
    for editor_id, conditions in sorted(leveled_items.items()):
        fh.write(f'{editor_id}={conditions}\n')
