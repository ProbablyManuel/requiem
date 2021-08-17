using System;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Xunit;
using KeywordList = Noggog.ExtendedList<Mutagen.Bethesda.Plugins.IFormLinkGetter<Mutagen.Bethesda.Skyrim.IKeywordGetter>>;

[assembly: CLSCompliant(false)]

namespace ReqtificatorTest.Transformers
{
    public class AmmunitionTransformerTest
    {
        [Fact]
        public void Should_Scale_Up_Damage_In_Eligible_Records()
        {
            var transformer = new AmmunitionTransformer();
            var unrelatedKeyword = Keywords.NoWeaponReachRescaling;
            var arrow = new Ammunition(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new KeywordList { unrelatedKeyword },
                Damage = 25.0f
            };
            var reference = arrow.DeepCopy();

            var result = transformer.Process(new UnChanged<Ammunition, IAmmunitionGetter>(arrow));
            var mask = new Ammunition.TranslationMask(defaultOn: true) { Damage = false };
            reference.Equals(result.Record(), mask).Should().BeTrue();
            result.Record().Damage.Should().BeInRange(99.9f, 100.1f);
        }

        [Fact]
        public void Should_Patch_Eligible_Records_Without_Keywords_Block()
        {
            var transformer = new AmmunitionTransformer();
            var arrow = new Ammunition(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = null,
                Damage = 42f
            };

            var result = transformer.Process(new UnChanged<Ammunition, IAmmunitionGetter>(arrow));
            result.IsModified().Should().BeTrue();
        }

        [Fact]
        public void Should_Not_Select_Records_Marked_as_Already_Reqtified()
        {
            var transformer = new AmmunitionTransformer();
            var arrow = new Ammunition(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new KeywordList { Keywords.AlreadyReqtified },
                Damage = 42f
            };

            var result = transformer.Process(new UnChanged<Ammunition, IAmmunitionGetter>(arrow));
            result.IsModified().Should().BeFalse();
        }

        [Fact]
        public void Should_Not_Select_Records_Marked_as_No_Damage_Scaling()
        {
            var transformer = new AmmunitionTransformer();
            var arrow = new Ammunition(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new KeywordList { Keywords.NoDamageRescaling },
                Damage = 42f
            };

            var result = transformer.Process(new UnChanged<Ammunition, IAmmunitionGetter>(arrow));
            result.IsModified().Should().BeFalse();
        }

        [Fact]
        public void Should_Not_Select_Records_Without_Damage()
        {
            var transformer = new AmmunitionTransformer();
            var arrow = new Ammunition(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
            {
                Keywords = new KeywordList(),
                Damage = 0f
            };

            var result = transformer.Process(new UnChanged<Ammunition, IAmmunitionGetter>(arrow));
            result.IsModified().Should().BeFalse();
        }
    }
}