using System.Collections.Generic;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator;
using Xunit;
using Xunit.Sdk;

namespace ReqtificatorTest
{
    public class UtilsTest
    {
        [Fact]
        public void Should_read_and_merge_the_records_from_all_formlist_versions()
        {
            var linkCache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            var formlist = new FormLink<IFormListGetter>(FormKey.Factory("123456:Requiem.esp"));

            var perk1 = new FormLink<IPerkGetter>(FormKey.Factory("123ABC:Skyrim.esm"));
            var perk2 = new FormLink<IPerkGetter>(FormKey.Factory("123DEF:Skyrim.esm"));

            var version1 = new FormList(formlist.FormKey, SkyrimRelease.SkyrimSE)
            {
                Items = new ExtendedList<IFormLinkGetter<ISkyrimMajorRecordGetter>> { perk1 }
            };
            var version2 = new FormList(formlist.FormKey, SkyrimRelease.SkyrimSE)
            {
                Items = new ExtendedList<IFormLinkGetter<ISkyrimMajorRecordGetter>> { perk2 }
            };
            var imports = new List<FormList> { version1, version2 };

            IPerkGetter? perkDummy1 = new Perk(perk1.FormKey, SkyrimRelease.SkyrimSE);
            IPerkGetter? perkDummy2 = new Perk(perk2.FormKey, SkyrimRelease.SkyrimSE);

            linkCache.Setup(c => c.ResolveAll<IFormListGetter>(formlist.FormKey, ResolveTarget.Winner))
                .Returns(imports);
            linkCache.Setup(c => c.TryResolve<IPerkGetter>(perk1.FormKey, out perkDummy1, ResolveTarget.Winner))
                .Returns(true);
            linkCache.Setup(c => c.TryResolve<IPerkGetter>(perk2.FormKey, out perkDummy2, ResolveTarget.Winner))
                .Returns(true);

            var output = Utils.GetRecordsFromAllImports<IPerkGetter>(formlist, linkCache.Object);

            output.Map(data => data.Should().BeEquivalentTo(new HashSet<FormLink<IPerkGetter>> { perk1, perk2 }));
            output.Recover(_ => throw new XunitException("expected a Success<>"));
        }
    }
}