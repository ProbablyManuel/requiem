using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers.Races;
using Xunit;

namespace ReqtificatorTest.Transformers.Races
{
    public class RaceVisualAutoMergeTest
    {
        private static readonly FormKey Skin = FormKey.Factory("0000AB:Skyrim.esm");

        private static readonly IFormLinkGetter<IColorRecordGetter> Color1 =
            FormKey.Factory("0000CD:Skyrim.esm").ToLinkGetter<IColorRecordGetter>();

        private static readonly IFormLinkGetter<IColorRecordGetter> Color2 =
            FormKey.Factory("0000EF:Skyrim.esm").ToLinkGetter<IColorRecordGetter>();

        private static readonly FormKey BodyParts = FormKey.Factory("0010AB:Skyrim.esm");
        private static readonly FormKey ArmorRace = FormKey.Factory("0010CD:Skyrim.esm");

        private static readonly FormKey HeadPartDummy1 = FormKey.Factory("0020AB:Skyrim.esm");
        private static readonly FormKey HeadPartDummy2 = FormKey.Factory("0020CD:Skyrim.esm");

        private static Race GenerateTemplateRace(string editorId)
        {
            static HeadData GenerateHeadData()
            {
                return new HeadData()
                {
                    AvailableHairColors = new ExtendedList<IFormLinkGetter<IColorRecordGetter>>
                    {
                        new FormLink<IColorRecordGetter>(HeadPartDummy1),
                        new FormLink<IColorRecordGetter>(HeadPartDummy2)
                    },
                    FaceDetails = new ExtendedList<IFormLinkGetter<ITextureSetGetter>>
                    {
                        new FormLink<ITextureSetGetter>(HeadPartDummy1), new FormLink<ITextureSetGetter>(HeadPartDummy2)
                    },
                    HeadParts = new ExtendedList<HeadPartReference>
                    {
                        new HeadPartReference{Head = new FormLinkNullable<IHeadPartGetter>(HeadPartDummy1)},
                        new HeadPartReference{Head = new FormLinkNullable<IHeadPartGetter>(HeadPartDummy2)}
                    },
                    RacePresets = new ExtendedList<IFormLinkGetter<INpcGetter>>
                    {
                        new FormLink<INpcGetter>(HeadPartDummy1), new FormLink<INpcGetter>(HeadPartDummy2)
                    }
                };
            }

            return new Race(FormKey.Factory("123456:Skyrim.esm"), SkyrimRelease.SkyrimSE)
            {
                EditorID = editorId,
                Skin = new FormLinkNullable<IArmorGetter>(Skin),
                Height = new GenderedItem<float>(1.0f, 0.95f),
                Weight = new GenderedItem<float>(1.0f, 1.0f),
                SkeletalModel = new GenderedItem<SimpleModel?>(new SimpleModel { File = "male" },
                    new SimpleModel { File = "female" }),
                DefaultHairColors = new GenderedItem<IFormLinkGetter<IColorRecordGetter>>(Color1, Color2),
                FacegenFaceClamp = 3.0f,
                FacegenMainClamp = 5.0f,
                BodyData = new GenderedItem<BodyData?>(new BodyData { Model = new Model { File = "male body" } },
                    new BodyData { Model = new Model { File = "female body" } }),
                BodyPartData = new FormLinkNullable<IBodyPartDataGetter>(BodyParts),
                BehaviorGraph = new GenderedItem<Model?>(new Model { File = "male graph" },
                    new Model { File = "female graph" }),
                FaceFxPhonemes = new FaceFxPhonemes
                {
                    I = new Phoneme { B = 0.5f, D = 0.6f }
                },
                HeadData = new GenderedItem<HeadData?>(GenerateHeadData(), GenerateHeadData()),
                ArmorRace = new FormLinkNullable<IRaceGetter>(ArmorRace)
            };
        }

        private class Fixture
        {
            public readonly Race Race1 = GenerateTemplateRace("Race1");
            public readonly Race Race2 = GenerateTemplateRace("Race2");
            public readonly RaceVisualAutoMerge Logic = new();
        }

        [Fact]
        public void Should_consider_two_races_equal_if_their_visual_data_is_the_same()
        {
            var f = new Fixture();
            f.Logic.CheckRecordEquality(f.Race1, f.Race2).Should().BeTrue();
        }

        [Fact]
        public void Should_consider_two_races_equal_if_their_weight_and_height_are_numerically_equivalent()
        {
            var f = new Fixture();
            f.Race2.Height.Female += 0.000001f;
            f.Race2.Height.Male += 0.000001f;
            f.Race2.Weight.Female += 0.000001f;
            f.Race2.Weight.Male += 0.000001f;
            f.Logic.CheckRecordEquality(f.Race1, f.Race2).Should().BeTrue();
        }


        [Fact]
        public void Should_consider_two_races_equal_if_their_face_fx_phonemes_are_numerically_equivalent()
        {
            var f = new Fixture();
            f.Race2.FaceFxPhonemes.I!.B += 0.000001f;
            f.Race2.FaceFxPhonemes.I.D += 0.000001f;
            f.Logic.CheckRecordEquality(f.Race1, f.Race2).Should().BeTrue();
        }

        [Fact]
        public void Should_consider_two_races_equal_if_their_face_clamp_data_is_numerically_equivalent()
        {
            var f = new Fixture();
            f.Race2.FacegenFaceClamp += 0.000001f;
            f.Race2.FacegenMainClamp += 0.000001f;
            f.Logic.CheckRecordEquality(f.Race1, f.Race2).Should().BeTrue();
        }

        [Fact]
        public void Should_consider_two_races_equal_if_their_headdata_only_differs_by_sorting_of_the_elements()
        {
            var f = new Fixture();
            f.Race2.HeadData!.Male!.FaceDetails.Reverse();
            f.Race2.HeadData!.Male!.HeadParts.Reverse();
            f.Race2.HeadData!.Male!.RacePresets.Reverse();
            f.Race2.HeadData!.Male!.AvailableHairColors.Reverse();
            f.Race2.HeadData!.Female!.FaceDetails.Reverse();
            f.Race2.HeadData!.Female!.HeadParts.Reverse();
            f.Race2.HeadData!.Female!.RacePresets.Reverse();
            f.Race2.HeadData!.Female!.AvailableHairColors.Reverse();
            f.Logic.CheckRecordEquality(f.Race1, f.Race2).Should().BeTrue();
        }

        [Fact]
        public void Should_consider_two_races_different_if_their_visual_data_is_different()
        {
            var f = new Fixture();
            f.Race2.Skin = new FormLinkNullable<IArmorGetter>(FormKey.Factory("FFFFFF:Skyrim.esm"));
            f.Logic.CheckRecordEquality(f.Race1, f.Race2).Should().BeFalse();
        }

        [Fact]
        public void Should_consider_two_races_different_if_their_weight_and_height_are_numerically_different()
        {
            var f = new Fixture();
            f.Race2.Height.Female += 0.1f;
            f.Race2.Height.Male += 0.1f;
            f.Race2.Weight.Female += 0.1f;
            f.Race2.Weight.Male += 0.1f;
            f.Logic.CheckRecordEquality(f.Race1, f.Race2).Should().BeFalse();
        }


        [Fact]
        public void Should_consider_two_races_different_if_their_face_fx_phonemes_are_numerically_different()
        {
            var f = new Fixture();
            f.Race2.FaceFxPhonemes.I!.B += 0.1f;
            f.Race2.FaceFxPhonemes.I.D += 0.1f;
            f.Logic.CheckRecordEquality(f.Race1, f.Race2).Should().BeFalse();
        }

        [Fact]
        public void Should_consider_two_races_different_if_their_face_clamp_data_is_numerically_different()
        {
            var f = new Fixture();
            f.Race2.FacegenFaceClamp += 0.1f;
            f.Race2.FacegenMainClamp += 0.1f;
            f.Logic.CheckRecordEquality(f.Race1, f.Race2).Should().BeFalse();
        }

        [Fact]
        public void Should_consider_two_races_different_if_their_headdata_contains_different_elements()
        {
            var f = new Fixture();
            f.Race2.HeadData!.Male!.HeadParts.RemoveAt(0);
            f.Logic.CheckRecordEquality(f.Race1, f.Race2).Should().BeFalse();
        }
    }
}