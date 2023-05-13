import pandas as pd


df = pd.read_csv("patcher_data/SpellScalingRaw.csv")
df = df.sort_values("Spell")
# rows with duplicate spell but different flags
# asdf = df[df.duplicated("Spell", keep=False)].drop_duplicates(keep=False)
# for spell in asdf["Spell"].drop_duplicates():
#     print(f'Different values for {spell}')

spells = {}
for row in df.itertuples():
    key = row.Spell
    value = row[1:]
    if key in spells and spells[key] != value:
        print(f'Different values for {key}:\n  {spells[key]}\n  {value}')
    else:
        spells[key] = value

df = df.drop_duplicates("Spell")
df.to_excel("patcher_data/SpellScalingRaw.xlsx", index=False)
