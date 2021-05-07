using System.Collections.Immutable;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Reqtificator;
using Reqtificator.Transformers;
using Xunit;

namespace ReqtificatorTest.Transformers
{
    public class AmmunitionTransformerTest
    {
        [Fact]
        public void Should_Select_Eligible_Records()
        {
            var transformer = new AmmunitionTransformer();
            var arrow = new Mock<IAmmunitionGetter>(MockBehavior.Strict);
            var keywords = ImmutableList<IFormLinkGetter<IKeywordGetter>>.Empty;
            var bla = StaticReferences.Keywords.NoWeaponReachRescaling.AsLink<Keyword>();
            keywords = keywords.Add(bla);
            arrow.Setup(mock => mock.Keywords).Returns(keywords);
            arrow.Setup(mock => mock.Damage).Returns(42);

            transformer.ShouldProcess(arrow.Object).Should().BeTrue();
        }

        [Fact]
        public void Should_Select_Eligible_Records_Without_Keywords_Block()
        {
            var transformer = new AmmunitionTransformer();
            var arrow = new Mock<IAmmunitionGetter>(MockBehavior.Strict);
            ImmutableList<IFormLinkGetter<IKeywordGetter>> keywords = null;
            arrow.Setup(mock => mock.Keywords).Returns(keywords);
            arrow.Setup(mock => mock.Damage).Returns(42);

            transformer.ShouldProcess(arrow.Object).Should().BeTrue();
        }

        [Fact]
        public void Should_Not_Select_Records_Marked_as_Already_Reqtified()
        {
            var transformer = new AmmunitionTransformer();
            var arrow = new Mock<IAmmunitionGetter>(MockBehavior.Strict);
            var keywords = ImmutableList<IFormLinkGetter<IKeywordGetter>>.Empty;
            keywords = keywords.Add(StaticReferences.Keywords.AlreadyReqtified.AsLink<Keyword>());
            arrow.Setup(mock => mock.Keywords).Returns(keywords);
            arrow.Setup(mock => mock.Damage).Returns(42);

            transformer.ShouldProcess(arrow.Object).Should().BeFalse();
        }

        [Fact]
        public void Should_Not_Select_Records_Marked_as_No_Damage_Scaling()
        {
            var transformer = new AmmunitionTransformer();
            var arrow = new Mock<IAmmunitionGetter>(MockBehavior.Strict);
            var keywords = ImmutableList<IFormLinkGetter<IKeywordGetter>>.Empty;
            keywords = keywords.Add(StaticReferences.Keywords.NoDamageRescaling.AsLink<Keyword>());
            arrow.Setup(mock => mock.Keywords).Returns(keywords);
            arrow.Setup(mock => mock.Damage).Returns(42);

            transformer.ShouldProcess(arrow.Object).Should().BeFalse();
        }

        [Fact]
        public void Should_Not_Select_Records_Without_Damage()
        {
            var transformer = new AmmunitionTransformer();
            var arrow = new Mock<IAmmunitionGetter>(MockBehavior.Strict);
            var keywords = ImmutableList<IFormLinkGetter<IKeywordGetter>>.Empty;
            keywords = keywords.Add(StaticReferences.Keywords.NoWeaponReachRescaling.AsLink<Keyword>());
            arrow.Setup(mock => mock.Keywords).Returns(keywords);
            arrow.Setup(mock => mock.Damage).Returns(0);

            transformer.ShouldProcess(arrow.Object).Should().BeFalse();
        }
    }
}