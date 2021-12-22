using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.Races
{
    internal class RaceVisualAutoMerge : IDataForwardingLogic<Race, IRaceGetter>
    {
        private const float tolerance = 0.0001f;

        private readonly Race.TranslationMask _comparisonMask = new Race.TranslationMask(defaultOn: false)
        {
            //comparison for gendered items doesn't work, need to do a workaround
            Skin = true,
            // SkeletalModel = new GenderedItem<SimpleModel.TranslationMask>(true, true),
            // DefaultHairColors = new GenderedItem<bool>(true, true),
            // BodyData = new GenderedItem<BodyData.TranslationMask>(true, true),
            BodyPartData = true,
            // BehaviorGraph = new GenderedItem<Model.TranslationMask>(true, true),
            // HeadData = new GenderedItem<HeadData.TranslationMask>(true, true),
            ArmorRace = true
            // numeric values (or contains them) that need to be compared separately
            // Height = new GenderedItem<bool>(true, true),
            // Weight = new GenderedItem<bool>(true, true),
            // FacegenFaceClamp = true,
            // FacegenMainClamp = true,
            // FaceFxPhonemes = true
        };

        private readonly HeadData.TranslationMask _headDataComparisonMask = new HeadData.TranslationMask(defaultOn: true)
        {
            AvailableHairColors = false,
            FaceDetails = false,
            HeadParts = false,
            RacePresets = false
        };

        private readonly Race.TranslationMask _copyMask = new Race.TranslationMask(defaultOn: false)
        {
            Skin = true,
            Height = new GenderedItem<bool>(true, true),
            Weight = new GenderedItem<bool>(true, true),
            SkeletalModel = new GenderedItem<SimpleModel.TranslationMask>(true, true),
            DefaultHairColors = new GenderedItem<bool>(true, true),
            FacegenFaceClamp = true,
            FacegenMainClamp = true,
            BodyData = new GenderedItem<BodyData.TranslationMask>(true, true),
            BodyPartData = true,
            BehaviorGraph = new GenderedItem<Model.TranslationMask>(true, true),
            FaceFxPhonemes = true,
            HeadData = new GenderedItem<HeadData.TranslationMask>(true, true),
            ArmorRace = true
        };

        public bool CheckRecordEquality(IRaceGetter reference, IRaceGetter other)
        {
            bool CheckGenderedField<T>(Func<IRaceGetter, IGenderedItemGetter<T?>?> extractor, Func<T, T, bool> equality)
            {
                var referenceData = extractor(reference);
                var otherData = extractor(other);

                if (referenceData is null && otherData is null) return true;
                if (referenceData is null || otherData is null) return false;

                bool EqualityWithNullCheck(T? value1, T? value2)
                {
                    if (value1 is null && value2 is null) return true;
                    if (value1 is null || value2 is null) return false;
                    return equality(value1, value2);
                }

                return EqualityWithNullCheck(referenceData.Male, otherData.Male) &&
                       EqualityWithNullCheck(referenceData.Female, otherData.Female);
            }

            return reference.Equals(other, _comparisonMask) &&
                   CheckGenderedField(r => r.Height, (x, y) => Math.Abs(x - y) <= tolerance) &&
                   CheckGenderedField(r => r.Weight, (x, y) => Math.Abs(x - y) <= tolerance) &&
                   Math.Abs(reference.FacegenFaceClamp - other.FacegenFaceClamp) <= tolerance &&
                   Math.Abs(reference.FacegenMainClamp - other.FacegenMainClamp) <= tolerance &&
                   CheckGenderedField(r => r.SkeletalModel, (x, y) => x.Equals(y)) &&
                   CheckGenderedField(r => r.DefaultHairColors, (x, y) => x.Equals(y)) &&
                   CheckGenderedField(r => r.BodyData, (x, y) => x.Equals(y)) &&
                   CheckGenderedField(r => r.BehaviorGraph, (x, y) => x.Equals(y)) &&
                   CheckGenderedField(r => r.HeadData, CompareHeadparts) &&
                   ComparePhonemes(reference.FaceFxPhonemes, other.FaceFxPhonemes);
        }

        private bool CompareHeadparts(IHeadDataGetter reference, IHeadDataGetter other)
        {
            bool CompareSetData<T>(Func<IHeadDataGetter, IEnumerable<T?>> extractor)
            {
                var thisParts = extractor(reference).ToImmutableHashSet();
                var otherParts = extractor(other).ToImmutableHashSet();
                return thisParts.SetEquals(otherParts);
            }


            return reference.Equals(other, _headDataComparisonMask) &&
                   CompareSetData(r => r.AvailableHairColors.Select(x => x.FormKey)) &&
                   CompareSetData(r => r.FaceDetails.Select(x => x.FormKey)) &&
                   CompareSetData(r => r.RacePresets.Select(x => x.FormKey)) &&
                   CompareSetData(r => r.HeadParts.Select(x => (x.Head, x.Number)));
        }

        private static bool ComparePhonemes(IFaceFxPhonemesGetter reference, IFaceFxPhonemesGetter other)
        {
            bool CompareEntry(IPhonemeGetter? refEntry, IPhonemeGetter? otherEntry)
            {
                if (refEntry is null && otherEntry is null) return true;
                if (refEntry is null || otherEntry is null) return false;

                return Math.Abs(refEntry.B - otherEntry.B) < tolerance &&
                       Math.Abs(refEntry.D - otherEntry.D) < tolerance &&
                       Math.Abs(refEntry.F - otherEntry.F) < tolerance &&
                       Math.Abs(refEntry.G - otherEntry.G) < tolerance &&
                       Math.Abs(refEntry.K - otherEntry.K) < tolerance &&
                       Math.Abs(refEntry.L - otherEntry.L) < tolerance &&
                       Math.Abs(refEntry.M - otherEntry.M) < tolerance &&
                       Math.Abs(refEntry.N - otherEntry.N) < tolerance &&
                       Math.Abs(refEntry.P - otherEntry.P) < tolerance &&
                       Math.Abs(refEntry.R - otherEntry.R) < tolerance &&
                       Math.Abs(refEntry.S - otherEntry.S) < tolerance &&
                       Math.Abs(refEntry.T - otherEntry.T) < tolerance &&
                       Math.Abs(refEntry.V - otherEntry.V) < tolerance &&
                       Math.Abs(refEntry.W - otherEntry.W) < tolerance &&
                       Math.Abs(refEntry.Y - otherEntry.Y) < tolerance &&
                       Math.Abs(refEntry.Z - otherEntry.Z) < tolerance &&
                       Math.Abs(refEntry.AA - otherEntry.AA) < tolerance &&
                       Math.Abs(refEntry.AE - otherEntry.AE) < tolerance &&
                       Math.Abs(refEntry.AH - otherEntry.AH) < tolerance &&
                       Math.Abs(refEntry.AO - otherEntry.AO) < tolerance &&
                       Math.Abs(refEntry.AW - otherEntry.AW) < tolerance &&
                       Math.Abs(refEntry.AX - otherEntry.AX) < tolerance &&
                       Math.Abs(refEntry.AY - otherEntry.AY) < tolerance &&
                       Math.Abs(refEntry.CH - otherEntry.CH) < tolerance &&
                       Math.Abs(refEntry.DH - otherEntry.DH) < tolerance &&
                       Math.Abs(refEntry.EH - otherEntry.EH) < tolerance &&
                       Math.Abs(refEntry.ER - otherEntry.ER) < tolerance &&
                       Math.Abs(refEntry.EY - otherEntry.EY) < tolerance &&
                       Math.Abs(refEntry.HH - otherEntry.HH) < tolerance &&
                       Math.Abs(refEntry.IH - otherEntry.IH) < tolerance &&
                       Math.Abs(refEntry.IY - otherEntry.IY) < tolerance &&
                       Math.Abs(refEntry.JH - otherEntry.JH) < tolerance &&
                       Math.Abs(refEntry.NG - otherEntry.NG) < tolerance &&
                       Math.Abs(refEntry.OW - otherEntry.OW) < tolerance &&
                       Math.Abs(refEntry.OY - otherEntry.OY) < tolerance &&
                       Math.Abs(refEntry.SH - otherEntry.SH) < tolerance &&
                       Math.Abs(refEntry.TH - otherEntry.TH) < tolerance &&
                       Math.Abs(refEntry.UH - otherEntry.UH) < tolerance &&
                       Math.Abs(refEntry.UW - otherEntry.UW) < tolerance &&
                       Math.Abs(refEntry.ZH - otherEntry.ZH) < tolerance &&
                       Math.Abs(refEntry.SIL - otherEntry.SIL) < tolerance &&
                       Math.Abs(refEntry.FLAP - otherEntry.FLAP) < tolerance &&
                       Math.Abs(refEntry.SHOTSIL - otherEntry.SHOTSIL) < tolerance;
            }

            return CompareEntry(reference.I, other.I) &&
                   CompareEntry(reference.K, other.K) &&
                   CompareEntry(reference.N, other.N) &&
                   CompareEntry(reference.Oh, other.Oh) &&
                   CompareEntry(reference.R, other.R) &&
                   CompareEntry(reference.Th, other.Th) &&
                   CompareEntry(reference.W, other.W) &&
                   CompareEntry(reference.OohQ, other.OohQ) &&
                   CompareEntry(reference.Eee_LipL, other.Eee_LipL) &&
                   CompareEntry(reference.Eh_LipR, other.Eh_LipR) &&
                   CompareEntry(reference.Aah_LipBigAah, other.Aah_LipBigAah) &&
                   CompareEntry(reference.FV_LipTh, other.FV_LipTh) &&
                   CompareEntry(reference.BMP_LipEee, other.BMP_LipEee) &&
                   CompareEntry(reference.DST_LipK, other.DST_LipK) &&
                   CompareEntry(reference.BigAah_LipDST, other.BigAah_LipDST) &&
                   CompareEntry(reference.ChJSh_LipFV, other.ChJSh_LipFV);
        }

        public void ForwardDataFromTemplate(Race record, IRaceGetter templateRecord)
        {
            record.DeepCopyIn(templateRecord, _copyMask);
        }
    }
}