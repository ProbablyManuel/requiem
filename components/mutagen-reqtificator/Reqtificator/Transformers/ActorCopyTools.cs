using System;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.Rules;

namespace Reqtificator.Transformers

{
    internal static class ActorCopyTools
    {
        public static Npc.TranslationMask InheritAiDataMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                AIData = true,
                CombatStyle = true,
                GiftFilter = true
            };
        }

        public static Npc.TranslationMask InheritAiPackagesMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                Packages = true
            };
        }

        public static Npc.TranslationMask InheritAttackDataMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                AttackRace = true,
                Attacks = true
            };
        }

        public static Npc.TranslationMask InheritBaseDataMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                Name = true,
            };
        }

        public static Npc.TranslationMask InheritDefaultPackageListMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                DefaultPackageList = true,
                SpectatorOverridePackageList = true,
                ObserveDeadBodyOverridePackageList = true,
                GuardWarnOverridePackageList = true,
                CombatOverridePackageList = true
            };
        }

        public static Npc.TranslationMask InheritFactionsMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                Factions = true,
                CrimeFaction = true
            };
        }

        public static Npc.TranslationMask InheritInventoryMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                DefaultOutfit = true,
                SleepingOutfit = true,
                Items = true,
                // PlayerSkills = new PlayerSkills.TranslationMask(defaultOn: false)
                // {
                //     GearedUpWeapons = true
                // }
            };
        }

        public static Npc.TranslationMask InheritKeywordsMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                Keywords = true
            };
        }

        public static Npc.TranslationMask InheritScriptsMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                VirtualMachineAdapter = true
            };
        }

        public static Npc.TranslationMask InheritSpellListMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                ActorEffect = true,
                Perks = true
            };
        }

        public static Npc.TranslationMask InheritStatsMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                Configuration = new NpcConfiguration.TranslationMask(false)
                {
                    Level = true,
                    CalcMinLevel = true,
                    CalcMaxLevel = true,
                    SpeedMultiplier = true,
                    BleedoutOverride = true,
                    HealthOffset = true,
                    MagickaOffset = true,
                    StaminaOffset = true
                },
                PlayerSkills = new PlayerSkills.TranslationMask(defaultOn: false)
                {
                    Health = true,
                    Magicka = true,
                    Stamina = true,
                    SkillOffsets = true,
                    SkillValues = true
                },
                Class = true
            };
        }

        public static Npc.TranslationMask InheritTraitsMask()
        {
            return new Npc.TranslationMask(defaultOn: false)
            {
                //traits tab
                Race = true,
                WornArmor = true,
                Height = true,
                Weight = true,
                FarAwayModel = true,
                Voice = true,
                DeathItem = true,
                // PlayerSkills = new PlayerSkills.TranslationMask(defaultOn: false)
                // {
                //     FarAwayModelDistance = true
                // },
                // Configuration = new NpcConfiguration.TranslationMask(defaultOn: false)
                // {
                //     DispositionBase = true
                // },
                //sounds tab
                SoundLevel = true,
                Sound = true,
                //character generation parts tab
                HeadParts = true,
                HairColor = true,
                TintLayers = true,
                HeadTexture = true,
                FaceMorph = true,
                FaceParts = true,
                TextureLighting = true,
            };
        }

        // IsCharGenFacePreset = 4,
        // Unique = 32, // 0x00000020
        // UseTemplate = 256, // 0x00000100
        // DoesNotBleed = 65536, // 0x00010000
        // LoopedScript = 2097152, // 0x00200000
        // LoopedAudio = 268435456, // 0x10000000
        // IsGhost = 536870912, // 0x20000000
        // Invulnerable = 2147483648, // 0x80000000

        public static readonly ImmutableList<NpcConfiguration.Flag> InheritBaseDataFlags =
        [
            NpcConfiguration.Flag.Essential,
            NpcConfiguration.Flag.Protected,
            NpcConfiguration.Flag.Respawn,
            NpcConfiguration.Flag.Summonable,
            NpcConfiguration.Flag.SimpleActor,
            NpcConfiguration.Flag.DoesntAffectStealthMeter,
        ];

        public static readonly ImmutableList<NpcConfiguration.Flag> InheritStatsFlags = [NpcConfiguration.Flag.AutoCalcStats, NpcConfiguration.Flag.BleedoutOverride];

        public static readonly ImmutableList<NpcConfiguration.Flag> InheritTraitsFlags = [NpcConfiguration.Flag.Female, NpcConfiguration.Flag.OppositeGenderAnims];

        public static void CopyDataForTemplateFlag(Npc target, INpcGetter source, NpcConfiguration.TemplateFlag flag)
        {
            void CopyFlagValue(NpcConfiguration.Flag flagToCopy)
            {
                if (source.Configuration.Flags.HasFlag(flagToCopy))
                {
                    target.Configuration.Flags |= flagToCopy;
                }
                else
                {
                    target.Configuration.Flags &= ~flagToCopy;
                }
            }

            var mask = flag switch
            {
                NpcConfiguration.TemplateFlag.AIData => InheritAiDataMask(),
                NpcConfiguration.TemplateFlag.AIPackages => InheritAiPackagesMask(),
                NpcConfiguration.TemplateFlag.AttackData => InheritAttackDataMask(),
                NpcConfiguration.TemplateFlag.BaseData => InheritBaseDataMask(),
                NpcConfiguration.TemplateFlag.DefPackList => InheritDefaultPackageListMask(),
                NpcConfiguration.TemplateFlag.Factions => InheritFactionsMask(),
                NpcConfiguration.TemplateFlag.Inventory => InheritInventoryMask(),
                NpcConfiguration.TemplateFlag.Keywords => InheritKeywordsMask(),
                NpcConfiguration.TemplateFlag.Script => InheritScriptsMask(),
                NpcConfiguration.TemplateFlag.SpellList => InheritSpellListMask(),
                NpcConfiguration.TemplateFlag.Stats => InheritStatsMask(),
                NpcConfiguration.TemplateFlag.Traits => InheritTraitsMask(),
                _ => throw new NotImplementedException()
            };
            target.DeepCopyIn(source, mask);
            if (source.Configuration.TemplateFlags.HasFlag(flag))
            {
                target.Configuration.TemplateFlags |= flag;
            }
            else
            {
                target.Configuration.TemplateFlags &= ~flag;
            }
            //special handling for the shared flags enum
            var flagsToCopy = flag switch
            {
                NpcConfiguration.TemplateFlag.BaseData => InheritBaseDataFlags,
                NpcConfiguration.TemplateFlag.Stats => InheritStatsFlags,
                NpcConfiguration.TemplateFlag.Traits => InheritTraitsFlags,
                _ => []
            };
            flagsToCopy.ForEach(CopyFlagValue);
        }

        public static Npc MergeVisualAndSkillTemplates(ISkyrimMod targetMod, string editorId, INpcGetter skillTemplate,
            INpcGetter lookTemplate, ActorInheritanceGraphParser inheritanceGraph)
        {
            var newActor = targetMod.Npcs.AddNew(editorId);
            Enum.GetValues<NpcConfiguration.TemplateFlag>().Where(x =>
                    x is not NpcConfiguration.TemplateFlag.Traits and not NpcConfiguration.TemplateFlag.AttackData
                                                              and not NpcConfiguration.TemplateFlag.ModelAnimation)
                .ForEach(f =>
                {
                    var source = inheritanceGraph.FindAllTemplates(skillTemplate, f).First()[f];
                    CopyDataForTemplateFlag(newActor, source, f);
                });
            newActor.Template = lookTemplate.ToNullableLink();
            newActor.Configuration.TemplateFlags =
                NpcConfiguration.TemplateFlag.Traits | NpcConfiguration.TemplateFlag.AttackData;
            newActor.Race = lookTemplate.Race.AsSetter();
            return newActor;
        }
    }
}