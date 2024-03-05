using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.StaticReferences
{
    public static class Keywords
    {
        public static readonly IFormLinkGetter<IKeywordGetter> ArmorHeavy =
            new FormLink<IKeywordGetter>(FormKey.Factory("06BBD2:Skyrim.esm"));

        public static readonly IFormLinkGetter<IKeywordGetter> ArmorLight =
            new FormLink<IKeywordGetter>(FormKey.Factory("06BBD3:Skyrim.esm"));

        public static readonly IFormLinkGetter<IKeywordGetter> ArmorBody =
            new FormLink<IKeywordGetter>(FormKey.Factory("06C0EC:Skyrim.esm"));

        public static readonly IFormLinkGetter<IKeywordGetter> ArmorFeet =
            new FormLink<IKeywordGetter>(FormKey.Factory("06C0ED:Skyrim.esm"));

        public static readonly IFormLinkGetter<IKeywordGetter> ArmorHands =
            new FormLink<IKeywordGetter>(FormKey.Factory("06C0EF:Skyrim.esm"));

        public static readonly IFormLinkGetter<IKeywordGetter> ArmorHead =
            new FormLink<IKeywordGetter>(FormKey.Factory("06C0EE:Skyrim.esm"));

        public static readonly IFormLinkGetter<IKeywordGetter> ArmorShield =
            new FormLink<IKeywordGetter>(FormKey.Factory("0965B2:Skyrim.esm"));

        public static readonly IFormLinkGetter<IKeywordGetter> NoArmorTypeKeyword =
            new FormLink<IKeywordGetter>(FormKey.Factory("AE35A8:Requiem.esp"));

        public static readonly IFormLinkGetter<IKeywordGetter> NoArmorRatingRescaling =
            new FormLink<IKeywordGetter>(FormKey.Factory("AD3B2B:Requiem.esp"));

        public static readonly IFormLinkGetter<IKeywordGetter> AlreadyReqtified =
            new FormLink<IKeywordGetter>(FormKey.Factory("8F08B5:Requiem.esp"));

        public static readonly IFormLinkGetter<IKeywordGetter> NoDamageRescaling =
            new FormLink<IKeywordGetter>(FormKey.Factory("AD3B2D:Requiem.esp"));

        public static readonly IFormLinkGetter<IKeywordGetter> NoWeaponReachRescaling =
            new FormLink<IKeywordGetter>(FormKey.Factory("AD3B2E:Requiem.esp"));

        public static readonly IFormLinkGetter<IKeywordGetter> NoWeaponSpeedRescaling =
            new FormLink<IKeywordGetter>(FormKey.Factory("AD3B2F:Requiem.esp"));

        public static readonly IFormLinkGetter<IKeywordGetter> WeaponBowHeavy =
            new FormLink<IKeywordGetter>(FormKey.Factory("9F9914:Requiem.esp"));

        public static readonly IFormLinkGetter<IKeywordGetter> WeaponCrossbowHeavy =
            new FormLink<IKeywordGetter>(FormKey.Factory("899DBE:Requiem.esp"));
    }

    public static class GlobalVariables
    {
        public static readonly IFormLinkGetter<IGlobalGetter> VersionStampPlugin =
            new FormLink<IGlobalGetter>(FormKey.Factory("2F389F:Requiem.esp"));

        public static readonly IFormLinkGetter<IGlobalGetter> VersionStampPatch =
            new FormLink<IGlobalGetter>(FormKey.Factory("973D69:Requiem.esp"));
    }

    public static class FormLists
    {
        public static readonly IFormLinkGetter<IFormListGetter> ClosedEncounterZones =
            new FormLink<IFormListGetter>(FormKey.Factory("A46546:Requiem.esp"));

        public static readonly IFormLinkGetter<IFormListGetter> GlobalPerks =
            new FormLink<IFormListGetter>(FormKey.Factory("8F57EA:Requiem.esp"));

        public static readonly IFormLinkGetter<IFormListGetter> HelpMenuRequiem =
            new FormLink<IFormListGetter>(FormKey.Factory("AD3A1A:Requiem.esp"));

        public static readonly IFormLinkGetter<IFormListGetter> HelpMenuPc =
            new FormLink<IFormListGetter>(FormKey.Factory("000163:Skyrim.esm"));

        public static readonly IFormLinkGetter<IFormListGetter> HelpMenuXbox =
            new FormLink<IFormListGetter>(FormKey.Factory("000165:Skyrim.esm"));
    }

    public static class Quests
    {
        public static readonly IFormLinkGetter<IQuestGetter> LockpickingControl =
            new FormLink<IQuestGetter>(FormKey.Factory("AD3B22:Requiem.esp"));

        public static readonly IFormLinkGetter<IQuestGetter> FollowerControl =
            new FormLink<IQuestGetter>(FormKey.Factory("82F3AF:Requiem.esp"));
    }

    public static class Npcs
    {
        public static readonly IFormLinkGetter<INpcGetter> Player =
            new FormLink<INpcGetter>(FormKey.Factory("000007:Skyrim.esm"));
    }

    public static class Spells
    {
        public static readonly IFormLinkGetter<ISpellGetter> MassEffectBase =
            new FormLink<ISpellGetter>(FormKey.Factory("7E76F4:Requiem.esp"));

        public static readonly IFormLinkGetter<ISpellGetter> MassEffectNpc =
            new FormLink<ISpellGetter>(FormKey.Factory("82CC14:Requiem.esp"));

        public static readonly IFormLinkGetter<ISpellGetter> NoNaturalHealthRegeneration =
                new FormLink<ISpellGetter>(FormKey.Factory("609AF0:Requiem.esp"));

    }
}