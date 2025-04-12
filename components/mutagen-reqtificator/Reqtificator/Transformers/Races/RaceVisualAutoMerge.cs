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
        private const float Tolerance = 0.0001f;

        private readonly Race.TranslationMask _comparisonMask = new(defaultOn: false)
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

        private readonly HeadData.TranslationMask _headDataComparisonMask = new(defaultOn: true)
        {
            // need custom comparison because sorting does not matter
            AvailableHairColors = false,
            FaceDetails = false,
            HeadParts = false,
            RacePresets = false,
            // skip over these as they should not be the only changes and are hard to compare
            AvailableMorphs = false,
            TintMasks = false
        };

        private readonly Race.TranslationMask _copyMask = new(defaultOn: false)
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

                if (referenceData is null && otherData is null)
                {
                    return true;
                }

                if (referenceData is null || otherData is null)
                {
                    return false;
                }

                bool EqualityWithNullCheck(T? value1, T? value2)
                {
                    if (value1 is null && value2 is null)
                    {
                        return true;
                    }

                    if (value1 is null || value2 is null)
                    {
                        return false;
                    }

                    return equality(value1, value2);
                }

                return EqualityWithNullCheck(referenceData.Male, otherData.Male) &&
                       EqualityWithNullCheck(referenceData.Female, otherData.Female);
            }

            return reference.Equals(other, _comparisonMask) &&
                   CheckGenderedField(r => r.Height, (x, y) => Math.Abs(x - y) <= Tolerance) &&
                   CheckGenderedField(r => r.Weight, (x, y) => Math.Abs(x - y) <= Tolerance) &&
                   Math.Abs(reference.FacegenFaceClamp - other.FacegenFaceClamp) <= Tolerance &&
                   Math.Abs(reference.FacegenMainClamp - other.FacegenMainClamp) <= Tolerance &&
                   CheckGenderedField(r => r.SkeletalModel, (x, y) => CompareSimpleModel(x, y)) &&
                   CheckGenderedField(r => r.DefaultHairColors, (x, y) => x.Equals(y)) &&
                   CheckGenderedField(r => r.BodyData, (x, y) => CompareBodyData(x, y)) &&
                   CheckGenderedField(r => r.BehaviorGraph, (x, y) => CompareSimpleModel(x, y)) &&
                   CheckGenderedField(r => r.HeadData, CompareHeadparts) &&
                   ComparePhonemes(reference.FaceFxPhonemes, other.FaceFxPhonemes);
        }

        private static bool CompareBodyData(IBodyDataGetter reference, IBodyDataGetter other)
        {
            if (!reference.Index.Equals(other.Index))
            {
                return false;
            }
            if (reference.Model is null && other.Model is null)
            {
                return true;
            }
            if (reference.Model is null || other.Model is null)
            {
                return false;
            }
            return CompareSimpleModel(reference.Model, other.Model);
        }

        private static bool CompareSimpleModel(ISimpleModelGetter reference, ISimpleModelGetter other)
        {
            return reference.File.DataRelativePath.Path.Equals(other.File.DataRelativePath.Path, StringComparison.Ordinal);
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
            static bool CompareEntry(IPhonemeGetter? refEntry, IPhonemeGetter? otherEntry)
            {
                if (refEntry is null && otherEntry is null)
                {
                    return true;
                }

                if (refEntry is null || otherEntry is null)
                {
                    return false;
                }

                return Math.Abs(refEntry.B - otherEntry.B) < Tolerance &&
                       Math.Abs(refEntry.D - otherEntry.D) < Tolerance &&
                       Math.Abs(refEntry.F - otherEntry.F) < Tolerance &&
                       Math.Abs(refEntry.G - otherEntry.G) < Tolerance &&
                       Math.Abs(refEntry.K - otherEntry.K) < Tolerance &&
                       Math.Abs(refEntry.L - otherEntry.L) < Tolerance &&
                       Math.Abs(refEntry.M - otherEntry.M) < Tolerance &&
                       Math.Abs(refEntry.N - otherEntry.N) < Tolerance &&
                       Math.Abs(refEntry.P - otherEntry.P) < Tolerance &&
                       Math.Abs(refEntry.R - otherEntry.R) < Tolerance &&
                       Math.Abs(refEntry.S - otherEntry.S) < Tolerance &&
                       Math.Abs(refEntry.T - otherEntry.T) < Tolerance &&
                       Math.Abs(refEntry.V - otherEntry.V) < Tolerance &&
                       Math.Abs(refEntry.W - otherEntry.W) < Tolerance &&
                       Math.Abs(refEntry.Y - otherEntry.Y) < Tolerance &&
                       Math.Abs(refEntry.Z - otherEntry.Z) < Tolerance &&
                       Math.Abs(refEntry.AA - otherEntry.AA) < Tolerance &&
                       Math.Abs(refEntry.AE - otherEntry.AE) < Tolerance &&
                       Math.Abs(refEntry.AH - otherEntry.AH) < Tolerance &&
                       Math.Abs(refEntry.AO - otherEntry.AO) < Tolerance &&
                       Math.Abs(refEntry.AW - otherEntry.AW) < Tolerance &&
                       Math.Abs(refEntry.AX - otherEntry.AX) < Tolerance &&
                       Math.Abs(refEntry.AY - otherEntry.AY) < Tolerance &&
                       Math.Abs(refEntry.CH - otherEntry.CH) < Tolerance &&
                       Math.Abs(refEntry.DH - otherEntry.DH) < Tolerance &&
                       Math.Abs(refEntry.EH - otherEntry.EH) < Tolerance &&
                       Math.Abs(refEntry.ER - otherEntry.ER) < Tolerance &&
                       Math.Abs(refEntry.EY - otherEntry.EY) < Tolerance &&
                       Math.Abs(refEntry.HH - otherEntry.HH) < Tolerance &&
                       Math.Abs(refEntry.IH - otherEntry.IH) < Tolerance &&
                       Math.Abs(refEntry.IY - otherEntry.IY) < Tolerance &&
                       Math.Abs(refEntry.JH - otherEntry.JH) < Tolerance &&
                       Math.Abs(refEntry.NG - otherEntry.NG) < Tolerance &&
                       Math.Abs(refEntry.OW - otherEntry.OW) < Tolerance &&
                       Math.Abs(refEntry.OY - otherEntry.OY) < Tolerance &&
                       Math.Abs(refEntry.SH - otherEntry.SH) < Tolerance &&
                       Math.Abs(refEntry.TH - otherEntry.TH) < Tolerance &&
                       Math.Abs(refEntry.UH - otherEntry.UH) < Tolerance &&
                       Math.Abs(refEntry.UW - otherEntry.UW) < Tolerance &&
                       Math.Abs(refEntry.ZH - otherEntry.ZH) < Tolerance &&
                       Math.Abs(refEntry.SIL - otherEntry.SIL) < Tolerance &&
                       Math.Abs(refEntry.FLAP - otherEntry.FLAP) < Tolerance &&
                       Math.Abs(refEntry.SHOTSIL - otherEntry.SHOTSIL) < Tolerance;
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