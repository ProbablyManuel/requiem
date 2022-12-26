import itertools
import json
import pandas as pd

from strict_dict import strict_dict
from lookup import form_by_editor_id, form_to_load_order_form_id


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


def generate_leveled_items(editor_id: str,
                           quantities: dict[str: int],
                           item_slots: list[str]):
    for item_slot in item_slots:
        items = []
        for item_type_and_set, count in quantities.items():
            item_editor_id = f'REQ_{item_type_and_set}_{item_slot}'
            try:
                form = form_by_editor_id(item_editor_id)
            except KeyError:
                continue
            data = ["1", form, "1"]
            items += [data.copy() for _ in range(count)]
        items.sort(key=lambda x: form_to_load_order_form_id(x[1]))
        key = editor_id.replace("{item_slot}", item_slot)
        value = ",".join(itertools.chain.from_iterable(items))
        leveled_items[key] = value


for editor_id, quantities in leveled_armors.items():
    generate_leveled_items(editor_id, quantities, armor_parts)
for editor_id, quantities in leveled_weapons.items():
    generate_leveled_items(editor_id, quantities, weapon_types)

with open("REQ_LeveledItemPatcher.txt", "w") as fh:
    for editor_id, conditions in sorted(leveled_items.items()):
        fh.write(f'{editor_id}={conditions}\n')
