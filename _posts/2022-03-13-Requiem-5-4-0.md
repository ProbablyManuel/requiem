---
layout: post
title: "Requiem 5.4.0 “Towers and Shadows” has been released"
author: ProbablyManuel
---
Hi Requiem enthusiasts,

The focus of this release is redefining lockpicking expertise to be more intuitive. Additionally, four more Creation Club DLCs are integrated and most text improvements from [Requiem - Alternative Descriptions](https://www.nexusmods.com/skyrimspecialedition/mods/33663) by Pamposzek are forwarded (but not the consolidated descriptions). For the full list of changes, please refer to the [changelog]({{site.github.repository_url}}/blob/main/components/documentation/src/Changelog.md#requiem-540---towers-and-shadows).

# Lockpicking

Locks in Requiem fall into one of three categories depending on your lockpicking expertise: easy, hard, or impossible. The sweetspot of hard locks only depends on the lock level, while easy locks have an up to 8 times wider sweetspot (the exact value depends on skill and perks). However, lockpicking expertise is presented in a rather unintuitive manner. The values go beyond 100 and the thresholds at which locks fall into a different category are unevenly distributed. This makes it hard to tell the impact of additional lockpicking expertise and whether a potion or enchantment will make a difference.

Therefore, lockpicking expertise is changed to range from 0 to 10 so that each point matters. The odd values 1/3/5/7/9 indicate you can unlock Novice/Apprentice/Adept/Expert/Master locks. The even values 2/4/6/8/10 indicate the sweetspot of all locks up to this level is 8 times larger regardless of skill and perks. This bonus stacks additively, e.g., 4 lockpicking expertise makes the sweetspot of Novice locks 16 times larger. This will reduce the tedium of lockpicking on long playthroughs without lessening the difficulty because hard locks remain unchanged.

Sources of lockpicking expertise are rebalanced accordingly. The main differences are:

* The first perk no longer allows to unlock Adept lock on its own.
* The second perk is split into two new perks unlocked at skill 30 and 60 respectively.
* Enchantments increase lockpick durability instead of expertise.

This addresses the issue that lockpicking expertise was too readily available with very little investment. This incited all characters, regardless of their background, to pick up lockpicking because it yields more perk points than it costs (this is still true but to a less problematic extent). Furthermore, while being able to unlock Adept locks with just the first perk is a nice quality of life feature, it is a design flaw because it removes any functional difference between Novice and Apprentice locks. To ensure Adept locks do not become an inconvenience with this change Khajiit caravans and Thieves Guild fences always stock a decent supply of Fortify Lockpick potions and the second lockpicking perk can already be unlocked at skill 30.

# Creation Club

The (optional) Creation Club integration started in Requiem 5.3.0 has been extended by 4 more DLCs with this release.

If you have the Creation Club patch installed, Requiem’s crossbows will now use the more authentic models from the Creation Club.

Horse armors are rebalanced for Requiem. They no longer render your horse immortal, but substantially increase both armor rating and health. Due to the unique internal structure of these two DLCs it was possible to integrate them without adding the DLCs as master files (but you still need the DLCs in order to access the content). Therefore, you can use the permanent discount of 100% on these two DLCs to install them without buying the Anniversary Edition or the Anniversary Upgrade.

# Horses

Horses in Skyrim are rather disappointing in Skyrim (aside from horse combat cheese). Hence, there are several mods of varying complexity that overhaul this area of the game. Requiem also contains a small horse overhaul with the following features:

* Open your nearby horse’s saddlebags through the Examine power.
* Horses are much faster.
* Some horses wear armor.
* Each purchasable horse has different stats.

However, Requiem still lacks important functionality such as a horse whistle to summon your horse. Furthermore, the included horse armors and the different stats come with their own issues. The armors have a bug that hides the manes of all horses, even wild and unsaddles ones. The horse stats are not visible to the player and therefore a moot decision.

On these grounds, I decided to remove all edits to horse NPCs including the armors and leave this area of the game to other mods or official DLCs. Movement speed changes are reimplemented through movement types edits, i.e., horses are still much faster, but all horses move at the same speed. The Examine power remains uncnhanged because it is a standalone, conflict-free utility.
