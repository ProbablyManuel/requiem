---
layout: post
title: "Requiem Developer Diary #14: Roadmap to Requiem 6.0.0"
author: ProbablyManuel
---
Hi Requiem enthusiasts,

In this blog post I’m revealing the roadmap for the next releases until 6.0.0.

However, first I have a different announcement. I’m now going by my first name Manuel on all modding related avenues (NexusMods, GitHub, Reddit, and Discord). I’ve been meaning to make this change for some time and this transition to a more public role gave me the impetus to move forward with it. “thetrader” is a meaningless term I picked years ago because I had to enter _something_, and it felt increasingly strange to be referred to by this nickname instead of the name everyone else uses. Since Manuel was already occupied on all platforms, I opted for the username “ProbablyManuel”.

Before presenting the roadmap, I’d like to backtrack a little. While Requiem has seen limited content updates in the past 2-3 years that doesn’t mean development remained idle. In Summer 2019 I dusted off my playing setup and enjoyed several lengthy playthroughs. This playing experience gave me a lot of valuable perspective on areas of Requiem that have been neglected throughout time or have developed in undesirable directions. While Ogerboss was mostly focusing his available time on getting Requiem into a shape ready for porting to SSE and then rewriting the Reqtificator, I began exploring solutions to the most pressing issues, especially those related to recent features I bear responsibility for. I battle-tested my rectifications directly in those playthroughs because I also realized the necessity of being exposed to your own work as soon as possible. With this newfound insight, I proceeded to refine those changes over time and I started to brainstorm how other areas of Requiem can be improved in a sustainable manner.

The content of the next releases will be the fruits of this labor. Since most of the work has already been done, you can expect a fast-paced release cycle for this roadmap. I won’t give an ETA for the releases, but I don’t expect the overall schedule to take more than three months to complete.

# 5.1.0

This release removes the additional slash, blunt, and pierce protection provided by each cuirass. While it was a well-intended idea to bring a common feature from other RPGs to Skyrim, it didn’t work out in practice for several reasons.

* You don’t have enough control over the damage type of encounters to make the choice of armor meaningful.
* The resistances aren’t powerful enough to make a noticeable difference when fighting NPCs.
* The feature lacks proper integration into the UI.

In its place high-quality armor materials now provide unique bonuses for each piece. For example, elven armor increases the strength of spells or dragon armors reduce the strength of incoming shouts and can even make their wearer immune to Unrelenting Force. Additional ranks of ranged protection remain in place, but they no longer clutter up your active magic effect menu. Instead, the ranks are reassigned to provide a more natural progression so that there is no need to compare values.

I intend this release to be the last iteration of the armor system for the foreseeable future (but the armor perks and their relation to the Mass Effect are subject to change post 6.0).

# 5.2.0

This is a small release that makes a number of quick and probably long overdue gameplay tweaks. You can look forward to changes like reworking the stamina drain from running or removing the overly verbose lockpicking pop-ups. Furthermore, this release is expected to feature a reimplementation of the Mass Effect that is robust to improperly timed equip events sent by the engine. This means the Mass Effect will no longer break just by looking at it the wrong way and you will be able to use features like Unlocked Grip.

# GitHub Migration

After 5.2.0 is released, I will move the repository, issue tracker, and documentation to GitHub and open the repository for contributions. The bug tracker on the Nexus will remain as a low-barrier support desk.

# 5.3.0

This release focuses on repopulating Requiem with suitable assets as replacement for the armors and weapons that were removed prior to the SSE port due to quality and lore concerns. I will present concrete mods at a later point because some assets require obtaining permission from their author first. However, the armor and weapon mods covered by the Requiem Patch Central should give you a good impression of what type of content I’m looking for.

# 5.4.0

This release redefines lockpicking expertise to be more intuitive. Instead of a seemingly arbitrary value going well beyond 100, the range is restricted to 10 and each point counts (previously small enough boosts to expertise had no impact whatsoever). The odd values 1/3/5/7/9 indicate you can unlock Novice/Apprentice/Adept/Expert/Master locks and the even values indicate you are proficient at picking the previous lock level, i.e., the sweet spot is much larger. Lockpicking skill no longer functions as an awkward copycat that too increases the sweet spot. In its place, enchantments are changed to increase lockpick durability.

# 5.4.x

The next release(s) will be a series of bug fixes until all known bugs are squashed.

# 6.0.0

This release brings three major content changes that each require a new game.

## Races

Daily powers are reworked into lesser powers that consume a fixed fraction of your health, magicka, or stamina and then apply an effect scaling with the consumed value. NPCs will be able to occasionally use these powers in appropriate situations too. Furthermore, every race receives two (or more) new passive abilities to establish a strong racial identity. The powers and abilities build upon racial lore and draw a connection to the primary deity of each race.

## Artifacts

Anything resembling a unique item or reward is overhauled. All “big” artifacts should have a unique twist that makes them stand out and implements a piece of lore. The artifacts are balanced around the level they’re usually acquired. Artifacts that are easily acquired are either less powerful or have a scaling factor. Artifacts that are acquired late in the game are allowed to be ludicrously overpowered if it fits the item. In particular, many artifacts of divine origin are changed to be underwhelming in the hands of an ordinary peasant, or even cease to work in the hands of a transgressor, but also become significantly stronger if its wielder spreads the influence of the respective deity.

## Food

Food is greatly simplified to only fortify four attributes depending on its nutrition: carry weight, disease resistance, magicka regeneration, and stamina regeneration. The magnitude is fixed for each effect so that there are no overshadowed items. Rarer and more expensive food has increased duration and sometimes multiple effects, e.g., beef stew provides the effect of both meat and vegetables and lasts 1 hour (twice the current duration).

# What about the Anniversary Edition DLCs?

After taking a look at some of the DLCs, I’m very underwhelmed with the scope and quality of these additions. The plans to provide official support the Anniversary Edition DLCs are therefore postponed indefinitely.
