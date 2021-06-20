using Mutagen.Bethesda;
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

        public static readonly FormKey AlreadyReqtified = FormKey.Factory("8F08B5:Requiem.esp");
        public static readonly FormKey NoDamageRescaling = FormKey.Factory("AD3B2D:Requiem.esp");
        public static readonly FormKey NoWeaponReachRescaling = FormKey.Factory("AD3B2E:Requiem.esp");
    }

    public static class GlobalVariables
    {
        public static readonly FormKey VersionStamp = FormKey.Factory("973D69:Requiem.esp");
    }

    public static class FormLists
    {
        public static readonly FormLink<IFormListGetter> ClosedEncounterZones =
            new(FormKey.Factory("A46546:Requiem.esp"));
    }

    public static class Quests
    {
        public static readonly FormLink<IQuestGetter> LockpickingControl = new(FormKey.Factory("AD3B22:Requiem.esp"));
        public static readonly FormLink<IQuestGetter> FollowerControl = new(FormKey.Factory("82F3AF:Requiem.esp"));
    }
}