import re

import pandas as pd


unperked_description = {
    "CreatePotionsUnperked": "You can create potions and poisons without perks.",
    "LockpickUnperked": "You can pick Novice locks without perks.",
    "PickpocketUnperked": "You can pick pockets without perks.",
    "RechargeWeaponsUnperked": "You can recharge enchantments without perks.",
    "SneakUnperked": "You can sneak without perks.",
}

cuisine = {
    "Argonian": "Fish ingredients are five times more nourishing.",
    "Bosmer": "Raw Meat is twice as nourishing.",
    "Khajiit": "Elswyr Fondue is twice as nourishing.",
    "Orc": "Your stomach is strong enough to digest the infamous Wrothgar Tartare, which increases health by 25.",
}


df = pd.read_csv("export/RaceDescriptions.csv")
df = df.convert_dtypes()
df = df.replace(r'<(\d+)>(?:<0>)*', r'\1', regex=True)
with open("../../components/documentation/src/Races.md", "w") as fh:
    fh.write("Races\n")
    fh.write("=====\n")
    for row in df.itertuples():
        fh.write("\n")
        fh.write(f'{row.race}\n')
        fh.write(f'{"-" * len(row.race)}\n')
        fh.write("\n")
        # Attributes
        fh.write(f'* **Attributes**:\n')
        fh.write(f'    * {row.health} Health\n')
        fh.write(f'    * {row.magicka} Magicka\n')
        fh.write(f'    * {row.stamina} Stamina\n')
        fh.write(f'    * {row.carry_weight} Carry Weight\n')
        fh.write(f'    * {row.unarmed_damage:g} Unarmed Damage\n')
        fh.write(f'    * {row.magicka_rate:g} Magicka Rate\n')
        fh.write(f'    * {row.stamina_rate:g} Stamina Rate\n')
        # Blood
        if not pd.isna(row.blood):
            fh.write(f'* **{row.race} Blood**:\n')
            for effect in re.split(r'(?<=\.)\s+', row.blood):
                fh.write(f'    * {effect}\n')
        # Physique
        if not pd.isna(row.physique):
            fh.write(f'* **{row.race} Physique**:\n')
            for effect in re.split(r'(?<=\.)\s+', row.physique):
                fh.write(f'    * {effect}\n')
        # Heritage
        fh.write(f'* **{row.race} Heritage**:\n')
        for effect in re.split(r'(?<=\.)\s+', row.heritage):
            fh.write(f'    * {effect}\n')
        # Cuisine
        if row.race in cuisine:
            fh.write(f'* **{row.race} Cuisine**: {cuisine[row.race]}\n')
        # Power
        fh.write(f'* **Active Power**: {row.power}\n')
        # Unperked
        if not pd.isna(row.unperked):
            fh.write(f'* **Unperked Skills**:\n')
            for unperked in row.unperked.split(";"):
                fh.write(f'    * {unperked_description[unperked]}\n')
        # Skill Boosts
        fh.write(f'* **Skill Boosts**:\n')
        skill_boosts = [s.split(maxsplit=1) for s in row.skill.split(";")]
        for boost, skill in sorted(skill_boosts):
            fh.write(f'    * {boost} {skill}\n')