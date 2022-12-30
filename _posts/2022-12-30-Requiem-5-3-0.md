---
layout: post
title: "Requiem 5.3.0 “Around the Fire” has been released"
author: ProbablyManuel
---
Hi Requiem enthusiasts,

This release features new armors and weapons as replacement for the assets that were removed prior to the SSE port due to quality and lore concerns. Additionally, it marks the first step at integrating the Creation Club DLCs into Requiem. For the full list of changes, please refer to the [changelog]({{site.github.repository_url}}/blob/main/components/documentation/src/Changelog.md#requiem-530---around-the-fire).

**Requiem can still be used without any paid Creation Club DLCs**. Integration of the Creation Club DLCs is offered in an optional patch that can be selected in the fomod installer. Thus far, 19 armor DLCs are supported. For the motivation behind the Creation Club integration, please read the previous blog post [Creation Club, GitHub, and Requiem 5.3.0]({{ site.baseurl }}{% post_url 2022-12-24-Developer-Diary-15 %}).

# New armors

Four new light armors are added to the game using assets from the Creation Club and [Chainmail Armor](https://www.nexusmods.com/skyrimspecialedition/mods/27340) by NordWarUA.

* Chainmail
* Dwarven Light
* Orcish Light
* Netch Leather

They each have a unique material bonus and therefore greatly increase the variety of mid-level light armors to provide a similar range of choices as heavy armors. The armors can be crafted with the respective material perk, and they are integrated into the world through loot and merchants.

The remaining armors from the Creation Club (15 heavy and 5 light) are used to enhance visual storytelling. All NPCs who used to wear a custom armor in older versions of Requiem and many others who represent a prominent position or faction now boast a unique armor.

Alongside the new armors, the unique material bonuses first introduced in Requiem 5.1.0 have been rebalanced to address current shortcomings. [This document]({{site.github.repository_url}}/blob/main/tools/Notes/Creation%20Club.pdf) describes the integration and most related armor tweaks in more detail.

# New weapons

Six new weapon types are added to the game.

* Tanto
* Wakizashi
* Katana
* Dai-Katana
* Shortsword
* Quarterstaff

The Akaviri-style weapons use assets from [Katana Crafting](https://www.nexusmods.com/skyrimspecialedition/mods/5306) by lautasantenni. You can obtain them either through crafting or by finding the unique copy in the world. The weapons have the same stats as the corresponding standard blade. Currently, they primarily exist to provide aesthetic variety and to revive items present in previous Elder Scrolls games. In the future, Akaviri-style weapons may be differentiated from Tamrielic weapons by dedicated boons from certain blessings or similar sources.

The shortswords and quarterstaffs use assets from [Heavy Armory](https://www.nexusmods.com/skyrimspecialedition/mods/6308) by PrivateEye. They can be crafted with the respective material perk, and they are integrated into the world through loot and merchants.
Shortswords have less damage, weight, and stagger than normal swords, but they swing noticeably faster. Enemy marksmen will often carry a shortsword as their melee backup weapon. Shortswords are also interesting to enchanters because they connect more frequently with the target.
Quarterstaffs fulfill the same role as the existing battlestaffs, but unlike battlestaffs they are available in all materials and their models are clearly designed for combat rather than magic. I plan to remove the battlestaffs at some point and use the Dragonborn DLC crafting system (or a similar concept that is not restricted to Solstheim) for enchanted staffs instead. However, I decided to postpone this endeavor and keep the battlestaffs as a minor variant of quarterstaffs for the time-being.

Furthermore, a few more weapons from Heavy Armory are included to complement the world. Most notably, silver weapons are available in all weapon types.

# Aetherium armor

With this release the Aetherium armor assets are removed from Requiem. In its place Dwarven armor with the same enchantments can be found. The items are named after the corresponding Dwarven city (e.g. Dwarven Helmet of Arkngthamz) and have a unique description to maintain their status as an artifact of importance.

This revision was already closely considered as part of the asset removal prior to the SSE port. At the time, we ultimately decided to tiebreak in favor of the status quo, but now that Requiem is repopulated with various new assets I think it’s more justifiable to follow through with the removal.

The main concern with the Aetherium armor is that its existence would have a major impact on the Aetherium quest and its lore. Katria freaks out when you find a small Aetherium shard, yet she doesn’t acknowledge the much more impressive Aetherium helmet right next to it. At the conclusion of the quest you can have this bewildering conversation with Katria while wearing a full suit of Aetherium.

> **Forge something? With what?** “*There isn’t any Aetherium here, is there? Damn it! Wait!... Yes, yes there is. The shards we collected... they’re pure Aetherium, remember? It’s not much, but it’ll do. With them, and the materials in this room, we should have everything we need.*”
>
> **Do you think there’s more Aetherium somewhere?** “*I don’t know. I honestly don’t know. After all these years, it’s a miracle even this much survived. Make it count. This may be the only chance we ever get to use the Forge.*”
