import re

import pandas as pd

ignore = [
    "REQ_Artifact_KynesToken",
    "REQ_Artifact_WabbajackDA15",
    "REQ_Cloth_Dremora_Body",
    "REQ_Cloth_Dremora_Feet",
    "REQ_Cloth_Necromancer_Body",
    "REQ_Cloth_Necromancer_Body_Hooded",
    "REQ_Cloth_Thalmor_Body",
    "REQ_Cloth_Thalmor_Body_Hooded",
    "REQ_Cloth_Thalmor_Body_Hooded",
    "REQ_Cloth_Warlock_Body",
    "REQ_Cloth_Warlock_Body_Hooded",
    "REQ_Heavy_Dremora_Body",
    "REQ_Heavy_Dremora_Feet",
    "REQ_Heavy_Dremora_Hands",
    "REQ_Heavy_Dremora_Head",
    "REQ_Var_Amulet_ReligionArkay_Andur",
    "REQ_Var_Amulet_ReligionTalos_Ogmund",
    "REQ_Var_Amulet_ReligionZenithar_Shahvee",
    "REQ_Var_Heavy_Iron_Hands_Forgemaster",
    "REQ_Var_Ring_Gold_Frida",
    "REQ_Var_Ring_Gold_Marriage",
    "REQ_Var_Ring_Gold_Saarthal",
    "REQ_Var_Weapon_Iron_Dagger_ArcaneExperimentation1",
    "REQ_Var_Weapon_Iron_Dagger_ArcaneExperimentation2",
    "REQ_Var_Weapon_Iron_Dagger_ArcaneExperimentation3",
    "REQ_Var_Weapon_Iron_Dagger_ArcaneExperimentation4",
    "REQ_Var_Weapon_Iron_Dagger_ArcaneExperimentation5",
    "REQ_Var_Weapon_Iron_Dagger_ArtificersInsight1",
    "REQ_Var_Weapon_Iron_Dagger_ArtificersInsight2",
    "REQ_Var_Weapon_Iron_Dagger_ArtificersInsight3",
    "REQ_Var_Weapon_Iron_Dagger_ArtificersInsight4",
    "REQ_Var_Weapon_Iron_Dagger_ArtificersInsight5",
    "REQ_Weapon_Dawnguard_Battleaxe",
    "REQ_Weapon_Dawnguard_Mace",
    "REQ_Weapon_Dawnguard_Quarterstaff",
    "REQ_Weapon_Dawnguard_WarAxe",
    "REQ_Weapon_Dawnguard_Warhammer",
]
add = {
    "Ring of Hircine": "Provides unlimited transformations for werewolves.",
    "Ring of Namira": "Feeding on corpses grants Namira's blessing: Health increases by 50 for 7200 seconds. Health regenerates 50% faster for 7200 seconds.",
    "Necromancer's Amulet": "Black soul gems can crafted with the perk Soul Gem Mastery.",
    "Bloodscythe": "Attacks deal 50% less damage to you while attacking with two weapons.",
    "Soulrender": "Attacks deal 50% less damage to you while attacking with two weapons.",
    "Miraak's Sword": "Attacks reach 20% further.",
    "Auriel's Bow": "Firing a Sunhallowed Elven Arrow at the sun causes a sunburst that targets random enemies for 60 seconds: Deals 150 sun damage. Undead take triple damage."
}
replace = {
    "Keening": "The wielder can summon the Shade of Arniel Gane.",
    "Longhammer": "The Longhammer swings 33% faster.",
    "Wabbajack": """Soul tomatoes can be crafted at a cooking pot. The Wabbajack randomly applies one of the following effects:
    * 10% chance: Deals 60 fire damage.
    * 10% chance: Deals 60 frost damage.
    * 10% chance: Deals 60 shock damage.
    *  5% chance: Absorbs 50% of the target's health, stamina, and magicka.
    *  5% chance: Summons 3 crazy dogs.
    *  5% chance: Summons 5 crazy goats.
    *  5% chance: Transforms the target into a chicken for 30 seconds.
    *  5% chance: Transforms the target into a dremora for 30 seconds.
    *  5% chance: Transforms the target into a mudcrab for 30 seconds.
    *  5% chance: Transforms the target into a rabbit for 30 seconds.
    *  5% chance: The target unequips all items and flees for 30 seconds.
    *  5% chance: The target becomes invisible for 30 seconds.
    *  5% chance: The target heals 500 health.
    *  5% chance: Freezes the target in solid ice for 30 seconds.
    *  5% chance: The target explodes into 150 septims. If the target has more than 300 health, it is knocked back instead.
    *  5% chance: The target turns into a sweet roll. If the target has more than 300 health, it is knocked back instead.
    *  5% chance: The target is covered in cheese.""",
}
categories = {
    "Heavy": "Heavy Armor",
    "Light": "Light Armor",
    "Cloth": "Clothing",
    "Head": "Head",
    "Body": "Body",
    "Hands": "Hands",
    "Feet": "Feet",
    "Shield": "Shield",
    "Circlet": "Circlet",
    "Amulet": "Amulet",
    "Ring": "Ring",
    "Dagger": "Dagger",
    "Sword": "Sword",
    "WarAxe": "War Axe",
    "Mace": "Mace",
    "Greatsword": "Greatsword",
    "Battleaxe": "Battleaxe",
    "Warhammer": "Warhammer",
    "Bow": "Bow",
    "Crossbow": "Crossbow",
    "Staff": "Staff",
}

armor_regex = re.compile(r'^[^_]+(_Var)?_(?P<type>Cloth|Heavy|Light)_(?P<set>[^_]+)_(?P<slot>[^_]+)(?:_(?P<mod>.+))?')


def parse_description(name: str, description: str):
    try:
        return replace[name]
    except KeyError:
        pass
    if not isinstance(description, str):
        return description
    sanitized = re.sub(r'<Global=[^>]+>', "0", description)
    parts = [s for s in sanitized.split(";")]
    dedicated = [s for s in parts if s.removeprefix("The ").startswith(name)]
    if dedicated:
        return " ".join(dedicated)
    try:
        parts.append(add[name])
    except KeyError:
        pass
    return " ".join(parts)


def get_category():
    match = df["editor_id"].str.extract(armor_regex)
    armor_set = match["mod"].where(match["mod"].notna(), match["set"])
    armor_set_counts = armor_set.value_counts()
    armor = armor_set.isin(armor_set_counts[armor_set_counts > 1].index)
    return match["type"].where(armor, df["slot"])


def get_key(editor_id: str, name: str):
    m = armor_regex.match(editor_id)
    if m and m.group("mod") == "OldGods":
        return f'{m.group("mod")}_{name}'
    else:
        return name


df = pd.read_csv(
    "export/ArtifactDescriptions.csv",
    names=["editor_id", "slot", "name", "description"])
df = df.convert_dtypes()
df["category"] = get_category()
df["description"] = df.apply(lambda x: parse_description(x["name"], x["description"]), axis=1)
df = df.dropna()
df = df[~df["editor_id"].isin(ignore)]
df = df.drop_duplicates(["name", "description"])
df["key"] = df.apply(lambda x: get_key(x["editor_id"], x["name"]), axis=1)
df = df.sort_values("key")
df = df.replace(r'<(\d+)>', r'\1', regex=True)
with open("../../components/documentation/src/Artifacts.md", "w") as fh:
    for category in categories:
        if category not in df["category"].values:
            continue
        fh.write(f'{categories[category]}\n')
        fh.write(f'{"=" * len(categories[category])}\n')
        fh.write("\n")
        for row in df[df["category"] == category].itertuples():
            fh.write(f'* **{row.name}**: {row.description}\n')
        fh.write("\n")
