using System;
using System.Collections.Immutable;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Xunit;

[assembly: CLSCompliant(false)]

namespace ReqtificatorTest.Transformers
{
    public static class AmmunitionTransformerTest
    {
        public class ShouldProcess 
        {
            [Fact]
            public void Should_Select_Eligible_Records()
            {
                var transformer = new AmmunitionTransformer();
                var arrow = new Mock<IAmmunitionGetter>(MockBehavior.Strict);
                var keywords = ImmutableList<IFormLinkGetter<IKeywordGetter>>.Empty;
                var unrelatedKeyword = Keywords.NoWeaponReachRescaling.AsLink<Keyword>();
                keywords = keywords.Add(unrelatedKeyword);
                arrow.Setup(mock => mock.Keywords).Returns(keywords);
                arrow.Setup(mock => mock.Damage).Returns(42.0f);

                transformer.ShouldProcess(arrow.Object).Should().BeTrue();
            }

            [Fact]
            public void Should_Select_Eligible_Records_Without_Keywords_Block()
            {
                var transformer = new AmmunitionTransformer();
                var arrow = new Mock<IAmmunitionGetter>(MockBehavior.Strict);
                ImmutableList<IFormLinkGetter<IKeywordGetter>> keywords = null;
                arrow.Setup(mock => mock.Keywords).Returns(keywords);
                arrow.Setup(mock => mock.Damage).Returns(42.0f);

                transformer.ShouldProcess(arrow.Object).Should().BeTrue();
            }

            [Fact]
            public void Should_Not_Select_Records_Marked_as_Already_Reqtified()
            {
                var transformer = new AmmunitionTransformer();
                var arrow = new Mock<IAmmunitionGetter>(MockBehavior.Strict);
                var keywords = ImmutableList<IFormLinkGetter<IKeywordGetter>>.Empty;
                keywords = keywords.Add(Keywords.AlreadyReqtified.AsLink<Keyword>());
                arrow.Setup(mock => mock.Keywords).Returns(keywords);
                arrow.Setup(mock => mock.Damage).Returns(42.0f);

                transformer.ShouldProcess(arrow.Object).Should().BeFalse();
            }

            [Fact]
            public void Should_Not_Select_Records_Marked_as_No_Damage_Scaling()
            {
                var transformer = new AmmunitionTransformer();
                var arrow = new Mock<IAmmunitionGetter>(MockBehavior.Strict);
                var keywords = ImmutableList<IFormLinkGetter<IKeywordGetter>>.Empty;
                keywords = keywords.Add(Keywords.NoDamageRescaling.AsLink<Keyword>());
                arrow.Setup(mock => mock.Keywords).Returns(keywords);
                arrow.Setup(mock => mock.Damage).Returns(42.0f);

                transformer.ShouldProcess(arrow.Object).Should().BeFalse();
            }

            [Fact]
            public void Should_Not_Select_Records_Without_Damage()
            {
                var transformer = new AmmunitionTransformer();
                var arrow = new Mock<IAmmunitionGetter>(MockBehavior.Strict);
                var keywords = ImmutableList<IFormLinkGetter<IKeywordGetter>>.Empty;
                keywords = keywords.Add(Keywords.NoWeaponReachRescaling.AsLink<Keyword>());
                arrow.Setup(mock => mock.Keywords).Returns(keywords);
                arrow.Setup(mock => mock.Damage).Returns(0f);

                transformer.ShouldProcess(arrow.Object).Should().BeFalse();
            }
        }

        public class PatchRecord
        {
            [Fact]
            public void Should_Scale_Up_Damage()
            {
                var transformer = new AmmunitionTransformer();
                var arrow = new Mock<IAmmunition>(MockBehavior.Strict);
                arrow.Setup(mock => mock.Damage).Returns(25.0f);
                arrow.SetupSet(mock => mock.Damage = 100.0f);
                transformer.Process(arrow.Object);
            }
        }
    }
}