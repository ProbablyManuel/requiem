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
        private static Npc.TranslationMask _inheritAiDataMask = new Npc.TranslationMask(defaultOn: false)
        {
            AIData = true,
            CombatStyle = true,
            GiftFilter = true
        };

        private static Npc.TranslationMask _inheritAiPackagesMask = new Npc.TranslationMask(defaultOn: false)
        {
            Packages = true
        };

        private static Npc.TranslationMask _inheritAttackDataMask = new Npc.TranslationMask(defaultOn: false)
        {
            AttackRace = true,
            Attacks = true
        };

        private static Npc.TranslationMask _inheritBaseDataMask = new Npc.TranslationMask(defaultOn: false)
        {
            Name = true,
        };

        private static Npc.TranslationMask _inheritDefaultPackageListMask = new Npc.TranslationMask(defaultOn: false)
        {
            DefaultPackageList = true,
            SpectatorOverridePackageList = true,
            ObserveDeadBodyOverridePackageList = true,
            GuardWarnOverridePackageList = true,
            CombatOverridePackageList = true
        };

        private static Npc.TranslationMask _inheritFactionsMask = new Npc.TranslationMask(defaultOn: false)
        {
            Factions = true,
            CrimeFaction = true
        };

        private static Npc.TranslationMask _inheritInventoryMask = new Npc.TranslationMask(defaultOn: false)
        {
            DefaultOutfit = true,
            SleepingOutfit = true,
            Items = true,
            // PlayerSkills = new PlayerSkills.TranslationMask(defaultOn: false)
            // {
            //     GearedUpWeapons = true
            // }
        };

        private static Npc.TranslationMask _inheritKeywordsMask = new Npc.TranslationMask(defaultOn: false)
        {
            Keywords = true
        };

        private static Npc.TranslationMask _inheritScriptsMask = new Npc.TranslationMask(defaultOn: false)
        {
            VirtualMachineAdapter = true
        };

        private static Npc.TranslationMask _inheritSpellListMask = new Npc.TranslationMask(defaultOn: false)
        {
            ActorEffect = true,
            Perks = true
        };

        private static Npc.TranslationMask _inheritStatsMask = new Npc.TranslationMask(defaultOn: false)
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

        private static Npc.TranslationMask _inheritTraitsMask = new Npc.TranslationMask(defaultOn: false)
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
            Configuration = new NpcConfiguration.TranslationMask(defaultOn: false)
            {
                DispositionBase = true
            },
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

        // IsCharGenFacePreset = 4,
        // Unique = 32, // 0x00000020
        // UseTemplate = 256, // 0x00000100
        // DoesNotBleed = 65536, // 0x00010000
        // LoopedScript = 2097152, // 0x00200000
        // LoopedAudio = 268435456, // 0x10000000
        // IsGhost = 536870912, // 0x20000000
        // Invulnerable = 2147483648, // 0x80000000

        private static readonly ImmutableList<NpcConfiguration.Flag> InheritBaseDataFlags = ImmutableList<NpcConfiguration.Flag>
            .Empty
            .Add(NpcConfiguration.Flag.Essential)
            .Add(NpcConfiguration.Flag.Protected)
            .Add(NpcConfiguration.Flag.Respawn)
            .Add(NpcConfiguration.Flag.Summonable)
            .Add(NpcConfiguration.Flag.SimpleActor)
            .Add(NpcConfiguration.Flag.DoesntAffectStealthMeter);

        private static readonly ImmutableList<NpcConfiguration.Flag> InheritStatsFlags = ImmutableList<NpcConfiguration.Flag>
            .Empty
            .Add(NpcConfiguration.Flag.AutoCalcStats)
            .Add(NpcConfiguration.Flag.BleedoutOverride);

        private static readonly ImmutableList<NpcConfiguration.Flag> InheritTraitsFlags = ImmutableList<NpcConfiguration.Flag>
            .Empty
            .Add(NpcConfiguration.Flag.Female)
            .Add(NpcConfiguration.Flag.OppositeGenderAnims);

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
                NpcConfiguration.TemplateFlag.AIData => _inheritAiDataMask,
                NpcConfiguration.TemplateFlag.AIPackages => _inheritAiPackagesMask,
                NpcConfiguration.TemplateFlag.AttackData => _inheritAttackDataMask,
                NpcConfiguration.TemplateFlag.BaseData => _inheritBaseDataMask,
                NpcConfiguration.TemplateFlag.DefPackList => _inheritDefaultPackageListMask,
                NpcConfiguration.TemplateFlag.Factions => _inheritFactionsMask,
                NpcConfiguration.TemplateFlag.Inventory => _inheritInventoryMask,
                NpcConfiguration.TemplateFlag.Keywords => _inheritKeywordsMask,
                // NpcConfiguration.TemplateFlag.ModelAnimation => expr,
                NpcConfiguration.TemplateFlag.Script => _inheritScriptsMask,
                NpcConfiguration.TemplateFlag.SpellList => _inheritSpellListMask,
                NpcConfiguration.TemplateFlag.Stats => _inheritStatsMask,
                NpcConfiguration.TemplateFlag.Traits => _inheritTraitsMask,
                _ => throw new ArgumentOutOfRangeException(nameof(flag), flag, null)
            };
            target.DeepCopyIn(source, mask);
            target.Configuration.TemplateFlags &= ~flag;
            //special handling for the shared flags enum
            var flagsToCopy = flag switch
            {
                NpcConfiguration.TemplateFlag.BaseData => InheritBaseDataFlags,
                NpcConfiguration.TemplateFlag.Stats => InheritStatsFlags,
                NpcConfiguration.TemplateFlag.Traits => InheritTraitsFlags,
                _ => ImmutableList<NpcConfiguration.Flag>.Empty
            };
            flagsToCopy.ForEach(CopyFlagValue);
        }

        public static Npc MergeVisualAndSkillTemplates(ISkyrimMod targetMod, string editorId, INpcGetter skillTemplate,
            INpcGetter lookTemplate, ActorInheritanceGraphParser inheritanceGraph)
        {
            var newActor = targetMod.Npcs.AddNew(editorId);
            Enum.GetValues<NpcConfiguration.TemplateFlag>().Where(x =>
                    x != NpcConfiguration.TemplateFlag.Traits && x != NpcConfiguration.TemplateFlag.AttackData
                                                              && x != NpcConfiguration.TemplateFlag.ModelAnimation)
                .ForEach(f =>
                {
                    var source = inheritanceGraph.FindAllTemplates(skillTemplate, f).First()[f];
                    CopyDataForTemplateFlag(newActor, source, f);
                });
            newActor.Template = lookTemplate.AsNullableLink();
            newActor.Configuration.TemplateFlags =
                NpcConfiguration.TemplateFlag.Traits | NpcConfiguration.TemplateFlag.AttackData;
            return newActor;
        }
    }
}