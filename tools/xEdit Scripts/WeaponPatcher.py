import copy


class WeaponSet:
    def __init__(self, damage_offset, weight_offset, gold_mult):
        self.damage_offset = damage_offset
        self.weight_offset = weight_offset
        self.gold_mult = gold_mult


class WeaponStats:
    def __init__(self, damage, weight, gold, speed, reach, stagger):
        self.damage = damage
        self.weight = weight
        self.gold = gold
        self.speed = speed
        self.reach = reach
        self.stagger = stagger


weapon_sets = {
    "Blades": WeaponSet(damage_offset=3, weight_offset=0, gold_mult=7.0),
    "Bound": WeaponSet(damage_offset=1, weight_offset=None, gold_mult=None),
    "BoundMystic": WeaponSet(damage_offset=6, weight_offset=None, gold_mult=None),
    "Daedric": WeaponSet(damage_offset=7, weight_offset=7, gold_mult=100.0),
    "Dawnguard": WeaponSet(damage_offset=2, weight_offset=1, gold_mult=6.0),
    "Dragonbone": WeaponSet(damage_offset=6, weight_offset=6, gold_mult=80.0),
    "AncientNord": WeaponSet(damage_offset=0, weight_offset=1, gold_mult=1 / 3.0),
    "AncientNordHoned": WeaponSet(damage_offset=3, weight_offset=1, gold_mult=2 / 3),
    "Dwarven": WeaponSet(damage_offset=3, weight_offset=4, gold_mult=5.0),
    "Ebony": WeaponSet(damage_offset=5, weight_offset=5, gold_mult=40.0),
    "Elven": WeaponSet(damage_offset=1, weight_offset=-2, gold_mult=3.0),
    "Falmer": WeaponSet(damage_offset=-1, weight_offset=0, gold_mult=0.25),
    "FalmerHoned": WeaponSet(damage_offset=2, weight_offset=0, gold_mult=0.5),
    "Forsworn": WeaponSet(damage_offset=-2, weight_offset=-2, gold_mult=0.2),
    "Glass": WeaponSet(damage_offset=4, weight_offset=-1, gold_mult=25.0),
    "Imperial": WeaponSet(damage_offset=0, weight_offset=0, gold_mult=4 / 3),
    "Iron": WeaponSet(damage_offset=-1, weight_offset=-1, gold_mult=0.55),
    "NordHero": WeaponSet(damage_offset=3, weight_offset=-1, gold_mult=6.0),
    "Nordic": WeaponSet(damage_offset=1, weight_offset=1, gold_mult=2.0),
    "Orcish": WeaponSet(damage_offset=2, weight_offset=2, gold_mult=4.0),
    "Redguard": WeaponSet(damage_offset=3, weight_offset=0, gold_mult=2.0),
    "Silver": WeaponSet(damage_offset=0, weight_offset=0, gold_mult=2.0),
    "SkyforgeSteel": WeaponSet(damage_offset=3, weight_offset=0, gold_mult=7.0),
    "Spectral": WeaponSet(damage_offset=3, weight_offset=None, gold_mult=None),
    "Stalhrim": WeaponSet(damage_offset=5, weight_offset=4, gold_mult=40.0),
    "Steel": WeaponSet(damage_offset=0, weight_offset=0, gold_mult=1.0),
    "Wood": WeaponSet(damage_offset=-6, weight_offset=-7, gold_mult=0.25),
}

weapon_types = {
    "Dagger": WeaponStats(damage=5, weight=2.5, gold=18, speed=1.3, reach=0.7, stagger=0.0),
    "Tanto": WeaponStats(damage=5, weight=2.5, gold=18, speed=1.3, reach=0.7, stagger=0.0),

    "Sword": WeaponStats(damage=8, weight=10.0, gold=45, speed=1.0, reach=1.0, stagger=0.75),
    "Katana": WeaponStats(damage=8, weight=10.0, gold=45, speed=1.0, reach=1.0, stagger=0.75),
    "Shortsword": WeaponStats(damage=7, weight=9.0, gold=45, speed=1.15, reach=0.9, stagger=0.60),
    "Wakizashi": WeaponStats(damage=7, weight=9.0, gold=45, speed=1.15, reach=0.9, stagger=0.60),

    "WarAxe": WeaponStats(damage=9, weight=12.0, gold=55, speed=0.9, reach=1.0, stagger=0.85),
    "Hatchet": WeaponStats(damage=8, weight=11.0, gold=55, speed=1.05, reach=0.9, stagger=0.70),

    "Mace": WeaponStats(damage=10, weight=14.0, gold=65, speed=0.8, reach=1.0, stagger=1.0),
    "Club": WeaponStats(damage=9, weight=13.0, gold=65, speed=0.95, reach=0.9, stagger=0.85),
    "Maul": WeaponStats(damage=12, weight=16.0, gold=65, speed=0.75, reach=1.0, stagger=1.1),

    "Greatsword": WeaponStats(damage=17, weight=17.0, gold=90, speed=0.75, reach=1.3, stagger=1.1),
    "DaiKatana": WeaponStats(damage=17, weight=17.0, gold=90, speed=0.75, reach=1.3, stagger=1.1),

    "Battleaxe": WeaponStats(damage=18, weight=21.0, gold=100, speed=0.7, reach=1.3, stagger=1.15),

    "Warhammer": WeaponStats(damage=20, weight=25.0, gold=110, speed=0.6, reach=1.3, stagger=1.25),
    "LongMace": WeaponStats(damage=16, weight=21.0, gold=110, speed=0.7, reach=1.3, stagger=1.15),

    "Quarterstaff": WeaponStats(damage=12, weight=15.0, gold=80, speed=0.8, reach=1.3, stagger=1.05),
    "Battlestaff": WeaponStats(damage=10, weight=15.0, gold=80, speed=0.9, reach=1.5, stagger=1.05),
}


weapon_stats = {}
for weapon_set, set_stats in weapon_sets.items():
    for weapon_type, type_stats in weapon_types.items():
        stats = copy.copy(type_stats)

        if set_stats.damage_offset is None:
            stats.damage = 0
        else:
            stats.damage += set_stats.damage_offset
        stats.critical_damage = stats.damage // 2

        if set_stats.weight_offset is None:
            stats.weight = 0.0
        elif weapon_type == "Dagger":
            stats.weight += set_stats.weight_offset / 2
        else:
            stats.weight += set_stats.weight_offset

        if set_stats.gold_mult is None:
            stats.gold = 0
        else:
            stats.gold *= set_stats.gold_mult
            stats.gold = round(stats.gold / 5) * 5

        weapon_stats[f'Weapon_{weapon_set}_{weapon_type}'] = stats


stats = copy.copy(weapon_stats["Weapon_Daedric_Battleaxe"])
stats.gold = 50000
stats.weight -= 5.0
weapon_stats["Artifact_RuefulAxe"] = stats

stats = copy.copy(weapon_stats["Weapon_Ebony_Battleaxe"])
stats.damage += 2
stats.gold = 50000
weapon_stats["Artifact_Wuuthrad"] = stats

stats = copy.copy(weapon_stats["Weapon_Ebony_Dagger"])
weapon_stats["Artifact_BladeOfSacrifice"] = stats

stats = copy.copy(weapon_stats["Weapon_Ebony_Dagger"])
weapon_stats["Artifact_BladeOfWoe"] = stats

stats = copy.copy(weapon_stats["Weapon_Ebony_Dagger"])
weapon_stats["Artifact_AwakenedBladeOfWoe"] = stats

stats = copy.copy(weapon_stats["Weapon_Dragonbone_Dagger"])
stats.gold = 5000
weapon_stats["Artifact_KahvozeinsFang"] = stats

stats = copy.copy(weapon_stats["Weapon_Daedric_Dagger"])
stats.damage += 2
stats.weight += 1.0
stats.gold = 50000
weapon_stats["Artifact_Keening"] = stats

stats = copy.copy(weapon_stats["Weapon_Daedric_Dagger"])
stats.gold = 50000
weapon_stats["Artifact_MehrunesRazor"] = stats

stats = copy.copy(weapon_stats["Weapon_Ebony_Dagger"])
stats.gold = 5000
weapon_stats["Artifact_Nettlebane"] = stats

# stats = copy.copy(weapon_stats["Weapon_Silver_Greatsword"])
# stats.gold = 5000
# weapon_stats["Artifact_BloodskalBlade"] = stats

stats = copy.copy(weapon_stats["Weapon_Ebony_Greatsword"])
stats.gold = 50000
stats.weight -= 3.0
weapon_stats["Artifact_EbonyBlade"] = stats

# stats = copy.copy(weapon_stats["Weapon_Steel_Mace"])
# weapon_stats["Artifact_Horksbane"] = stats

stats = copy.copy(weapon_stats["Weapon_Daedric_Mace"])
stats.weight += 2.0
stats.gold = 50000
weapon_stats["Artifact_MaceOfMolagBal"] = stats

# stats = copy.copy(weapon_stats["Weapon_"])
# weapon_stats["Artifact_RustyMace"] = stats

stats = copy.copy(weapon_stats["Weapon_Glass_Sword"])
stats.damage += 1
stats.weight -= 1.0
stats.gold = 5000
weapon_stats["Artifact_Chillrend"] = stats

# stats = copy.copy(weapon_stats["Weapon_Redguard_Sword"])
# weapon_stats["Artifact_Amren"] = stats

# stats = copy.copy(weapon_stats["Weapon_Blades_Sword"])
# stats.gold = 5000
# weapon_stats["Artifact_BolarsOathblade"] = stats

stats = copy.copy(weapon_stats["Weapon_Silver_Sword"])
stats.damage += 5
stats.gold = 50000
weapon_stats["Artifact_Dawnbreaker"] = stats

stats = copy.copy(weapon_stats["Weapon_Blades_Sword"])
stats.weight += 4.0
stats.damage += 3
stats.gold = 5000
weapon_stats["Artifact_Dragonbane"] = stats

stats = copy.copy(weapon_stats["Weapon_Spectral_Sword"])
stats.gold = 5000
weapon_stats["Artifact_Ghostblade"] = stats

stats = copy.copy(weapon_stats["Weapon_Forsworn_Sword"])
weapon_stats["Artifact_GoraksTrollGuttingKnife"] = stats

stats = copy.copy(weapon_stats["Weapon_Daedric_Sword"])
stats.gold = 5000
weapon_stats["Artifact_HarkonsSword"] = stats

stats = copy.copy(weapon_stats["Weapon_Ebony_Sword"])
stats.damage += 1
stats.weight -= 4.0
stats.gold = 5000
weapon_stats["Artifact_NightingaleBlade"] = stats

stats = copy.copy(weapon_stats["Weapon_NordHero_Sword"])
stats.gold = 5000
stats.speed += 0.075
stats.reach += 0.075
weapon_stats["Artifact_RedEaglesBane"] = stats

stats = copy.copy(weapon_stats["Weapon_NordHero_Sword"])
stats.damage -= 3
stats.speed += 0.075
stats.reach += 0.075
weapon_stats["Artifact_RedEaglesFury"] = stats

stats = copy.copy(weapon_stats["Weapon_Redguard_Sword"])
weapon_stats["Artifact_Windshear"] = stats

stats = copy.copy(weapon_stats["Weapon_Dawnguard_WarAxe"])
stats.damage += 2
stats.gold = 5000
weapon_stats["Artifact_AncientDawnguardWarAxe"] = stats

# stats = copy.copy(weapon_stats["Weapon_Iron_Warhammer"])
# weapon_stats["Artifact_Aegisbane"] = stats

stats = copy.copy(weapon_stats["Weapon_Orcish_Warhammer"])
stats.weight -= 9.0
stats.speed += 0.2
weapon_stats["Artifact_TheLonghammer"] = stats

stats = copy.copy(weapon_stats["Weapon_Dawnguard_Warhammer"])
stats.damage += 2
stats.gold = 5000
weapon_stats["Artifact_AncientDawnguardWarhammer"] = stats

stats = copy.copy(weapon_stats["Weapon_Daedric_Warhammer"])
stats.weight += 5.0
stats.gold = 50000
weapon_stats["Artifact_Volendrung"] = stats


with open("REQ_WeaponPatcher.txt", "w") as fh:
    for weapon, stats in sorted(weapon_stats.items()):
        fh.write(f'{weapon}=')
        fh.write(f'{stats.damage:d} ')
        fh.write(f'{stats.weight:.6f} ')
        fh.write(f'{stats.gold:d} ')
        fh.write(f'{stats.speed:.6f} ')
        fh.write(f'{stats.reach:.6f} ')
        fh.write(f'{stats.stagger:.6f} ')
        fh.write(f'{stats.critical_damage:d}\n')
