using System.Drawing;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Actors;
using Xunit;

namespace ReqtificatorTest.Transformers.Actors
{
    public class ActorVisualAutoMergeTest
    {
        private static Npc GenerateTemplateActor(string editorId)
        {
            return new Npc(FormKey.Factory("123456:Skyrim.esm"), SkyrimRelease.SkyrimSE)
            {
                EditorID = editorId,
                HeadParts = new ExtendedList<IFormLinkGetter<IHeadPartGetter>>
                {
                    new FormLink<IHeadPartGetter>(FormKey.Factory("0000AB:Skyrim.esm")),
                    new FormLink<IHeadPartGetter>(FormKey.Factory("0000CD:Skyrim.esm")),
                    new FormLink<IHeadPartGetter>(FormKey.Factory("0000EF:Skyrim.esm"))
                },
                TextureLighting = Color.DarkBlue,
                FaceMorph = new NpcFaceMorph
                {
                    BrowsForwardVsBack = 0.4f,
                    BrowsInVsOut = 0.5f
                },
                TintLayers = new ExtendedList<TintLayer>
                {
                    new TintLayer() { Color = Color.Black, Index = 0, InterpolationValue = 0.5f, Preset = 0 },
                    new TintLayer() { Color = Color.GreenYellow, Index = 1, InterpolationValue = 0.4f, Preset = 1 },
                },
                Height = 1.1f,
                HairColor = new FormLinkNullable<IColorRecordGetter>(FormKey.Factory("1111AB:Skyrim.esm")),
                Weight = 0.5f
            };
        }

        private class Fixture
        {
            public readonly Npc Actor1 = GenerateTemplateActor("Actor1");
            public readonly Npc Actor2 = GenerateTemplateActor("Actor2");
            public readonly ActorVisualAutoMerge Logic = new ActorVisualAutoMerge();
        }


        [Fact]
        public void Should_consider_two_actors_equal_if_their_visual_data_is_the_same()
        {
            var f = new Fixture();
            f.Logic.CheckRecordEquality(f.Actor1, f.Actor2).Should().BeTrue();
        }

        [Fact]
        public void Should_consider_two_actors_equal_if_their_tintlayers_differ()
        {
            var f = new Fixture();
            f.Actor2.TintLayers.RemoveAt(1);
            f.Logic.CheckRecordEquality(f.Actor1, f.Actor2).Should().BeTrue();
        }

        [Fact]
        public void Should_consider_two_actors_equal_if_their_texture_lighting_differs()
        {
            var f = new Fixture();
            f.Actor2.TextureLighting = Color.Indigo;
            f.Logic.CheckRecordEquality(f.Actor1, f.Actor2).Should().BeTrue();
        }

        [Fact]
        public void Should_consider_two_actors_equal_if_their_headparts_are_ordered_differently()
        {
            var f = new Fixture();
            f.Actor2.HeadParts.Reverse();
            f.Logic.CheckRecordEquality(f.Actor1, f.Actor2).Should().BeTrue();
        }

        [Fact]
        public void Should_consider_two_actors_equal_if_their_facemorph_data_is_numerically_equivalent()
        {
            var f = new Fixture();
            f.Actor2.FaceMorph!.BrowsInVsOut += 0.00001f;
            f.Logic.CheckRecordEquality(f.Actor1, f.Actor2).Should().BeTrue();
        }

        [Fact]
        public void Should_consider_two_actors_different_if_visual_data_is_different()
        {
            var f = new Fixture();
            f.Actor2.Weight += 0.1f;
            f.Logic.CheckRecordEquality(f.Actor1, f.Actor2).Should().BeFalse();
        }

        [Fact]
        public void Should_consider_two_actors_different_if_their_headpart_sets_differ()
        {
            var f = new Fixture();
            f.Actor2.HeadParts.RemoveAt(0);
            f.Logic.CheckRecordEquality(f.Actor1, f.Actor2).Should().BeFalse();
        }

        [Fact]
        public void Should_consider_two_actors_different_if_their_facemorph_data_is_numerically_different()
        {
            var f = new Fixture();
            f.Actor2.FaceMorph!.BrowsInVsOut += 0.1f;
            f.Logic.CheckRecordEquality(f.Actor1, f.Actor2).Should().BeFalse();
        }

        [Fact]
        public void Should_copy_the_visual_data_from_the_template()
        {
            var f = new Fixture();
            f.Actor2.Weight = 42f;
            f.Actor2.TintLayers.RemoveAt(0);
            f.Actor2.HeadParts.Reverse();
            f.Actor2.FaceMorph!.BrowsInVsOut += 0.1f;
            f.Actor2.AttackRace = new FormLinkNullable<IRaceGetter>(FormKey.Factory("2222AB:Skyrim.esm"));
            f.Logic.ForwardDataFromTemplate(f.Actor1, f.Actor2);

            f.Actor1.Equals(f.Actor2, ActorCopyTools.InheritTraitsMask()).Should().BeTrue();
            f.Actor1.Equals(f.Actor2, ActorCopyTools.InheritAttackDataMask()).Should().BeTrue();
            f.Actor1.Equals(f.Actor2).Should().BeFalse();
        }

    }
}