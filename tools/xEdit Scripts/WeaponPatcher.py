import pandas as pd


weapon = pd.read_excel("../Spreadsheet/Weapon.xlsx", sheet_name="Weapons", index_col=0, usecols=["Unnamed: 0", "Damage", "Weight", "Gold"]).convert_dtypes()
weapon_types = pd.read_excel("../Spreadsheet/Weapon.xlsx", sheet_name="Stats", index_col=0).convert_dtypes().dropna()
weapon_artifacts = pd.read_excel("../Spreadsheet/Weapon.xlsx", sheet_name="Artifacts", index_col=0, usecols=["Unnamed: 0", "Base", "Damage", "Weight", "Gold", "Speed", "Reach", "Stagger"]).convert_dtypes().dropna(how="all")


weapon_stats = {}
for weapon_set, set_stats in weapon.iterrows():
    for weapon_type, type_stats in weapon_types.iterrows():
        editor_id = f'Weapon_{weapon_set}_{weapon_type}'
        stats = type_stats.to_dict()

        if pd.isna(set_stats["Damage"]):
            stats["Damage"] = 0
        else:
            stats["Damage"] += set_stats["Damage"]

        if pd.isna(set_stats["Weight"]):
            stats["Weight"] = 0.0
        elif weapon_type == "Dagger":
            stats["Weight"] += set_stats["Weight"] / 2
        else:
            stats["Weight"] += set_stats["Weight"]

        if pd.isna(set_stats["Gold"]):
            stats["Gold"] = 0
        else:
            stats["Gold"] *= set_stats["Gold"]
            stats["Gold"] = round(stats["Gold"] / 5) * 5

        weapon_stats[editor_id] = stats
for artifact, rows in weapon_artifacts.iterrows():
    if rows["Base"].endswith("Bow"):
        continue
    editor_id = f'Artifact_{artifact}'
    stats = weapon_stats[f'Weapon_{rows["Base"]}'].copy()
    if pd.notna(rows["Damage"]):
        stats["Damage"] += rows["Damage"]
    if pd.notna(rows["Weight"]):
        stats["Weight"] += rows["Weight"]
    if pd.notna(rows["Gold"]):
        stats["Gold"] = rows["Gold"]
    if pd.notna(rows["Speed"]):
        stats["Speed"] += rows["Speed"]
    if pd.notna(rows["Reach"]):
        stats["Reach"] += rows["Reach"]
    if pd.notna(rows["Stagger"]):
        stats["Stagger"] += rows["Stagger"]
    weapon_stats[editor_id] = stats


with open("REQ_WeaponPatcher.txt", "w") as fh:
    for weapon, stats in sorted(weapon_stats.items()):
        fh.write(f'{weapon}=')
        fh.write(f'{stats["Damage"]:d} ')
        fh.write(f'{stats["Weight"]:.6f} ')
        fh.write(f'{stats["Gold"]:d} ')
        fh.write(f'{stats["Speed"]:.6f} ')
        fh.write(f'{stats["Reach"]:.6f} ')
        fh.write(f'{stats["Stagger"]:.6f} ')
        fh.write(f'{stats["Damage"] // 2:d}\n')
