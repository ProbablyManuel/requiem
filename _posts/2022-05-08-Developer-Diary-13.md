---
layout: post
title: "Requiem Developer Diary #13: A NEW HAND TOUCHES THE BEACON"
author: ProbablyManuel
---
Hi Requiem enthusiasts,

I am excited to share my first Developer’s Diary and fill you in on my vision for the future of Requiem. I have been creating mods for Skyrim since 2015 and may be most commonly known for my first upload, the Requiem Patch Central, which I’m maintaining to this day. Already in 2015 I was asked to join the Requiem Dungeon Masters. Since then I have been a main developer of Requiem alongside Ogerboss and Axonis. As Axonis quit Skyrim in 2018 and Ogerboss made the same decision a few days ago, the project has now fallen in my hands.

I intend to continue Requiem as a one-man operation (+one woman when Ludovician is available). The main advantage of this approach is that it enables me to significantly speed up development by bypassing the coordination overhead of a team. I will take the following steps to align the direction of Requiem with the community nonetheless.

1. Migrate the repository to GitHub and open it for third-party contributions. This primarily targets bug fixes, technical improvements, and undisputed tweaks.
1. Collaborate with prolific modders in the community on areas I’m unlikely to touch in the near future (e.g. Dragonborn or Spells) and give them the necessary freedom to implement their vision without too much meddling from my side. Ideally, and I think for the time being this will be a hard requirement, such contributions are already published addons that have established a foothold on the Nexus.
1. Start hanging out in the Requiem Discord to lower the barrier for engagement of any kind.

The logistics of 1 and 2 will be worked out in the following weeks.

# The future of the Reqtificator

Unfortunately, I lack the necessary experience to develop the Reqtificator further and I don’t intend to delve into this field. Additionally, as most of you will be aware an external patcher like the Reqtificator entails a number of disadvantages.

* Involves a noticeably more complicated installation than the usual plug’n’play.
* Imposes an opaque plugin limit by the master limit of the generated patch.
* Requires persistent storage of a consistency file for new records in the patch.
* Forces the user to run an external every time they change their load order.

Several years ago, these concerns were voided by the lack of alternatives to automate imperative procedures such as distributing perks to NPCs. However, the Skyrim community has rapidly evolved since then. The rise of CommonLibSSE (the library which powers most SKSE plugins) and its derived works, in particular [Spell Perk Item Distributor (SPID)](https://www.nexusmods.com/skyrimspecialedition/mods/36869) and [Keyword Item Distributor (KID)](https://www.nexusmods.com/skyrimspecialedition/mods/55728), enables many features of the Reqtificator to be implemented by SKSE plugins instead. Other features can be reimplemented within the limitations of the Creation Kit without major feature loss. Therefore, the most likely scenario at this point is a replacement of the Reqtificator by suitable SKSE plugins. However, I will not rush a decision on this matter because this replacement would be a big investment. For the moment, I will wait and see how this situation develops. Ludovician, who wrote parts of the new Reqtificator, has offered to help maintain the Reqtificator as best she’s able in the meantime.

# The future of the Service Central

Effective immediately, the Service Central is discontinued. In its place I will open the bug tracker on the Nexus for installation requests and bug reports. However, please be aware that support for non-trivial issues with the Reqtificator will be limited.

# Permissions

The copyright license of Requiem has been rewritten to be more concise and update outdated pieces of information.

> **Plugins**
> 
> You may extend Requiem by plugins without restriction. If you intend to distribute a copy of the file `Requiem.esp`, modified or not, please contact us first.
> 
> **Papyrus Scripts**
> 
> You may use the Papyrus scripts without restriction.
> 
> **Meshes and Textures**
> 
> Most of the meshes and textures are the work of other people. If you want to use them, read ourcredits and send your permission request to the original authors.
> 
> **Reqtificator**
> 
> The Reqtificator is licensed under the GNU General Public License v3.0.
> 
> **Artwork**
> 
> You may use all artwork [on this page]({{site.github.repository_url}}/wiki/Artwork) in your Requiem-derived works.

The only remaining restriction is that standalone forks require explicit permission to prevent an undesirable divergence of the mod, tearing the intricate ecosystem apart. As I don’t really feel comfortable gatekeeping a piece of work that already passed through multiple hands, any permission request will be collectively decided by all Requiem Dungeon Masters that are still reachable.

# What’s next?

Requiem 5.0.3 has been released today and Requiem 5.1.0 will follow in 1-2 weeks. The content of 5.1.0 and the roadmap for the next releases will be communicated next week.
