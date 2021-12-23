using System;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.Actors
{
    internal class ActorVisualAutoMerge : IDataForwardingLogic<Npc, INpcGetter>
    {
        private readonly Npc.TranslationMask _compareTraitsMask;

        public ActorVisualAutoMerge()
        {
            _compareTraitsMask = ActorCopyTools.InheritTraitsMask();
            _compareTraitsMask.TintLayers = false; // changes here are usually Creation Kit artifacts
            _compareTraitsMask.TextureLighting = false; // changes here are usually Creation Kit artifacts
            _compareTraitsMask.HeadParts = false; // custom comparison needed, as order of elements is irrelevant
            _compareTraitsMask.FaceMorph = false; // can contain small numeric differences that are irrelevant
        }

        public bool CheckRecordEquality(INpcGetter reference, INpcGetter other)
        {
            return reference.Equals(other, _compareTraitsMask) &&
                   CompareHeadParts(other, reference) &&
                   CompareFaceMorphs(other, reference);
        }

        public void ForwardDataFromTemplate(Npc record, INpcGetter templateRecord)
        {
            ActorCopyTools.CopyDataForTemplateFlag(record, templateRecord, NpcConfiguration.TemplateFlag.Traits);
            ActorCopyTools.CopyDataForTemplateFlag(record, templateRecord, NpcConfiguration.TemplateFlag.AttackData);
        }

        private static bool CompareHeadParts(INpcGetter reference, INpcGetter other)
        {
            var thisParts = reference.HeadParts.Select(x => x.FormKey).ToImmutableHashSet();
            var otherParts = other.HeadParts.Select(x => x.FormKey).ToImmutableHashSet();
            return thisParts.SetEquals(otherParts);
        }

        private static bool CompareFaceMorphs(INpcGetter reference, INpcGetter other)
        {
            var refMorph = reference.FaceMorph;
            var otherMorph = other.FaceMorph;
            const float tolerance = 0.0001f;

            if (refMorph == null && otherMorph == null) return true;
            if (refMorph == null || otherMorph == null) return false;

            return Math.Abs(refMorph.BrowsForwardVsBack - otherMorph.BrowsForwardVsBack) < tolerance &&
                   Math.Abs(refMorph.BrowsInVsOut - otherMorph.BrowsInVsOut) < tolerance &&
                   Math.Abs(refMorph.BrowsUpVsDown - otherMorph.BrowsUpVsDown) < tolerance &&
                   Math.Abs(refMorph.CheeksForwardVsBack - otherMorph.CheeksForwardVsBack) < tolerance &&
                   Math.Abs(refMorph.CheeksUpVsDown - otherMorph.CheeksUpVsDown) < tolerance &&
                   Math.Abs(refMorph.ChinNarrowVsWide - otherMorph.ChinNarrowVsWide) < tolerance &&
                   Math.Abs(refMorph.ChinUnderbiteVsOverbite - otherMorph.ChinUnderbiteVsOverbite) < tolerance &&
                   Math.Abs(refMorph.ChinUpVsDown - otherMorph.ChinUpVsDown) < tolerance &&
                   Math.Abs(refMorph.EyesForwardVsBack - otherMorph.EyesForwardVsBack) < tolerance &&
                   Math.Abs(refMorph.EyesInVsOut - otherMorph.EyesInVsOut) < tolerance &&
                   Math.Abs(refMorph.EyesUpVsDown - otherMorph.EyesUpVsDown) < tolerance &&
                   Math.Abs(refMorph.JawForwardVsBack - otherMorph.JawForwardVsBack) < tolerance &&
                   Math.Abs(refMorph.JawNarrowVsWide - otherMorph.JawNarrowVsWide) < tolerance &&
                   Math.Abs(refMorph.JawUpVsDown - otherMorph.JawUpVsDown) < tolerance &&
                   Math.Abs(refMorph.LipsInVsOut - otherMorph.LipsInVsOut) < tolerance &&
                   Math.Abs(refMorph.LipsUpVsDown - otherMorph.LipsUpVsDown) < tolerance &&
                   Math.Abs(refMorph.NoseLongVsShort - otherMorph.NoseLongVsShort) < tolerance &&
                   Math.Abs(refMorph.NoseUpVsDown - otherMorph.NoseUpVsDown) < tolerance;
        }
    }
}