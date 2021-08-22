using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers

{
    internal static class ActorCopyTools
    {
        private static Npc.TranslationMask GenerateMask(bool overallEqualityMode)
        {
            var maskFilter = !overallEqualityMode;
            return new Npc.TranslationMask(defaultOn: overallEqualityMode)
            {
                EditorID = false, //never compare/copy the editorID
                //"inherit traits" data from the traits tab
                Race = maskFilter,
                // Configuration = new NpcConfiguration.TranslationMask(defaultOn: overallEqualityMode)
                // {
                //     Flags = false // TODO: needs to be handled more delicately for the female flag
                // },
                WornArmor = maskFilter, // "armor" used as skin, not default worn outfit
                Height = maskFilter,
                Weight = maskFilter,
                FarAwayModel = maskFilter,
                // PlayerSkills = new PlayerSkills.TranslationMask(defaultOn: overallEqualityMode)
                // {
                //     FarAwayModelDistance = maskFilter
                // },
                Voice = maskFilter,
                // DeathItem = ??? //let's skip this one for the comparison
                // "inherit traits" data from the sound tab
                SoundLevel = maskFilter,
                Sound = maskFilter,
                // "inherit traits" data from the character gen parts tab
                HeadTexture = maskFilter,
                HeadParts = maskFilter,
                HairColor = maskFilter,
                TintLayers = maskFilter,
                TextureLighting = maskFilter,
                // "inherit traits" data from the character gen morphs tab
                FaceParts = maskFilter,
                FaceMorph = maskFilter,
                //"inherit attack data" related fields
                AttackRace = maskFilter,
                Attacks = maskFilter
            };
        }

        public static Npc MergeVisualAndSkillTemplates(ISkyrimMod targetMod, string editorId, INpcGetter skillTemplate,
            INpcGetter lookTemplate)
        {
            //FIXME: this doesn't properly copy inherited data from the original actor
            var newActor = targetMod.Npcs.AddNew(editorId);
            newActor.DeepCopyIn(skillTemplate, GenerateMask(true));
            newActor.Template = lookTemplate.AsNullableLink();
            newActor.Configuration.TemplateFlags =
                NpcConfiguration.TemplateFlag.Traits | NpcConfiguration.TemplateFlag.AttackData;
            return newActor;
        }
    }
}