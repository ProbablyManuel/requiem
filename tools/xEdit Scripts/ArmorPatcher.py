import math
import pandas as pd


armor = pd.read_excel("../Spreadsheet/Armor.xlsx", sheet_name="Armors", index_col=0, usecols=[0, 1, 2, 3]).convert_dtypes()
armor_parts = pd.read_excel("../Spreadsheet/Armor.xlsx", sheet_name="Stats", index_col=0).convert_dtypes()
armor_artifacts = pd.read_excel("../Spreadsheet/Armor.xlsx", sheet_name="Artifacts", index_col=0).convert_dtypes().dropna(how="all")
armor_extras = pd.read_excel("../Spreadsheet/Armor.xlsx", sheet_name="Extras", index_col=0).convert_dtypes()


def round_js(x: float):
    if x - math.floor(x) < 0.5:
        return math.floor(x)
    else:
        return math.ceil(x)


def get_armor_rating(set_armor_rating: int, part: str):
    if set_armor_rating % 50 != 0:
        raise ValueError(f'{set_armor_rating} is not a multiple of 50')
    if part == "Head":
        return set_armor_rating * 0.2
    if part == "Feet":
        return math.ceil(set_armor_rating * 0.15)
    if part == "Hands":
        return math.floor(set_armor_rating * 0.15)
    if part == "Body":
        return set_armor_rating * 0.5
    if part == "Shield":
        return set_armor_rating * 0.3
    raise ValueError(f'Unknown body part: {part}')


def get_gold(set_gold, part):
    if part == "Head":
        return round_js(set_gold * 0.2)
    if part == "Feet":
        return round_js(set_gold * 0.15)
    if part == "Hands":
        return round_js(set_gold * 0.15)
    if part == "Body":
        return round_js(set_gold * 0.5)
    if part == "Shield":
        return round_js(set_gold * 0.25)
    raise ValueError(f'Unknown body part: {part}')


def get_weight(set_weight, part):
    if part == "Head":
        return set_weight * 0.2
    if part == "Feet":
        return set_weight * 0.15
    if part == "Hands":
        return set_weight * 0.15
    if part == "Body":
        return set_weight * 0.5
    if part == "Shield":
        return set_weight * 0.25
    raise ValueError(f'Unknown body part: {part}')


armor_stats = {}
for armor_set, set_stats in armor.iterrows():
    for armor_part, part_stats in armor_parts.iterrows():
        editor_id = f'{armor_set}_{armor_part}'
        stats = {}
        stats["Armor Rating"] = get_armor_rating(set_stats["Armor Rating"], armor_part)
        stats["Weight"] = get_weight(set_stats["Weight"], armor_part)
        stats["Gold"] = get_gold(set_stats["Gold"], armor_part)
        armor_stats[editor_id] = stats
for extra, rows in armor_extras.iterrows():
    template, _, _ = extra.rpartition("_")
    stats = armor_stats[template].copy()
    if pd.notna(rows["Armor Rating"]):
        stats["Armor Rating"] += rows["Armor Rating"]
    if pd.notna(rows["Weight"]):
        stats["Weight"] += rows["Weight"]
    if pd.notna(rows["Gold"]):
        stats["Gold"] += rows["Gold"]
    armor_stats[extra] = stats
for artifact, rows in armor_artifacts.iterrows():
    editor_id = f'Artifact_{artifact}'
    stats = armor_stats[rows["Base"]].copy()
    if pd.notna(rows["Armor Rating"]):
        stats["Armor Rating"] += rows["Armor Rating"]
    if pd.notna(rows["Weight"]):
        stats["Weight"] += rows["Weight"]
    if pd.notna(rows["Gold"]):
        stats["Gold"] = rows["Gold"]
    armor_stats[editor_id] = stats


with open("REQ_ArmorPatcher.txt", "w") as fh:
    for armor, stats in sorted(armor_stats.items()):
        fh.write(f'{armor}=')
        fh.write(f'{stats["Armor Rating"]:6f} ')
        fh.write(f'{stats["Weight"]:.6f} ')
        fh.write(f'{stats["Gold"]:.0f}\n')
