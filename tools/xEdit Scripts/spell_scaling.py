import pandas as pd


df = pd.read_excel("patcher_data/SpellScaling.xlsx")
with open("REQ_SpellScaling.txt", "w") as fh:
    for row in df.itertuples():
        fh.write(f'{row.Spell}=')
        fh.write(f'{row._2:d},')
        fh.write(f'{row._3:d},')
        fh.write(f'{row._4:d},')
        fh.write(f'{row._5:d},')
        fh.write(f'{row._6:d},')
        fh.write(f'{row._7:d},')
        fh.write(f'{row._8:d}\n')
