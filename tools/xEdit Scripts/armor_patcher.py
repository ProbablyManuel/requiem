import math
import pandas as pd

from strict_dict import strict_dict


armor = pd.read_excel(
    "patcher_data/Armor.xlsx",
    sheet_name="Armors",
    index_col=0,
    usecols=[
        "Unnamed: 0",
        "Armor Rating",
        "Weight",
        "Gold",
        "Survival"]).convert_dtypes()
armor_artifacts = pd.read_excel(
    "patcher_data/Armor.xlsx",
    sheet_name="Artifacts",
    index_col=0,
    usecols=[
        "Unnamed: 0",
        "Base",
        "Armor Rating",
        "Weight",
        "Gold",
        "Survival"]).convert_dtypes().dropna(how="all")
armor_extras = pd.read_excel(
    "patcher_data/Armor.xlsx",
    sheet_name="Extras",
    index_col=0,
    usecols=[
        "Unnamed: 0",
        "Armor Rating",
        "Weight",
        "Gold",
        "Survival"]).convert_dtypes()
armor_variants = pd.read_excel(
    "patcher_data/Armor.xlsx",
    sheet_name="Patches",
    index_col=0).convert_dtypes().dropna(how="all")


def get_armor_rating(set_armor_rating: int, part: str) -> float:
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


def get_gold(set_gold: int, part: str) -> int:
    if set_gold % 50 != 0:
        raise ValueError(f'{set_gold} is not a multiple of 50')
    if part == "Head":
        return int(set_gold * 0.2)
    if part == "Feet":
        return int(math.ceil(set_gold * 0.15))
    if part == "Hands":
        return int(math.floor(set_gold * 0.15))
    if part == "Body":
        return int(set_gold * 0.5)
    if part == "Shield":
        return int(set_gold * 0.25)
    raise ValueError(f'Unknown body part: {part}')


def get_weight(set_weight: int, part: str) -> float:
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


def get_survival(set_survival: str, part: str) -> str:
    if part in ("Head", "Feet", "Hands", "Body"):
        if set_survival in ("Cold", "None", "Warm"):
            return set_survival
        raise ValueError(f'Unknown survival keyword: {set_survival}')
    if part == "Shield":
        return "None"
    raise ValueError(f'Unknown body part: {part}')


armor_stats = strict_dict()
for armor_set, set_stats in armor.iterrows():
    for armor_part in ("Head", "Feet", "Hands", "Body", "Shield"):
        editor_id = f'{armor_set}_{armor_part}'
        stats = {}
        stats["Armor Rating"] = get_armor_rating(
            set_stats["Armor Rating"], armor_part)
        stats["Weight"] = get_weight(set_stats["Weight"], armor_part)
        stats["Gold"] = get_gold(set_stats["Gold"], armor_part)
        stats["Survival"] = get_survival(set_stats["Survival"], armor_part)
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
    if pd.notna(rows["Survival"]):
        stats["Survival"] = rows["Survival"]
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
    if pd.notna(rows["Survival"]):
        stats["Survival"] = rows["Survival"]
    armor_stats[editor_id] = stats
for variant, rows in armor_variants.iterrows():
    for armor_part in ("Head", "Feet", "Hands", "Body", "Shield"):
        editor_id = f'{variant}_{armor_part}'
        stats = armor_stats[f'{rows["Base"]}_{armor_part}'].copy()
        if pd.notna(rows["Armor Rating"]):
            stats["Armor Rating"] += get_armor_rating(
                rows["Armor Rating"], armor_part)
        if pd.notna(rows["Weight"]):
            stats["Weight"] += get_weight(rows["Weight"], armor_part)
        if pd.notna(rows["Gold"]):
            stats["Gold"] += get_gold(rows["Gold"], armor_part)
        if pd.notna(rows["Survival"]):
            stats["Survival"] = rows["Survival"]
        armor_stats[editor_id] = stats


with open("REQ_ArmorPatcher.txt", "w") as fh:
    for armor, stats in sorted(armor_stats.items()):
        fh.write(f'{armor}=')
        fh.write(f'{stats["Armor Rating"]:6f},')
        fh.write(f'{stats["Weight"]:.6f},')
        fh.write(f'{stats["Gold"]:d},')
        fh.write(f'{stats["Survival"]:s}\n')
