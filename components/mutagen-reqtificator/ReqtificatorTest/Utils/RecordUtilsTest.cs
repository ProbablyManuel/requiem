using System.Collections.Generic;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Exceptions;
using Reqtificator.Utils;
using Xunit;

namespace ReqtificatorTest.Utils
{
    public class RecordUtilsTest
    {
        [Fact]
        public void Should_read_and_merge_the_records_from_all_formlist_versions()
        {
            var linkCache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            var formlist = new FormLink<IFormListGetter>(FormKey.Factory("123456:Requiem.esp"));

            var perk1 = new FormLink<IPerkGetter>(FormKey.Factory("123ABC:Skyrim.esm"));
            var perk2 = new FormLink<IPerkGetter>(FormKey.Factory("123DEF:Skyrim.esm"));

            var mod1 = new ModKey("Skyrim", ModType.Master);
            var mod2 = new ModKey("Requiem", ModType.Plugin);

            var version1 = new FormList(formlist.FormKey, SkyrimRelease.SkyrimSE)
            {
                Items = new ExtendedList<IFormLinkGetter<ISkyrimMajorRecordGetter>> { perk1 }
            };
            var version2 = new FormList(formlist.FormKey, SkyrimRelease.SkyrimSE)
            {
                Items = new ExtendedList<IFormLinkGetter<ISkyrimMajorRecordGetter>> { perk2 }
            };

            IPerkGetter? perkDummy1 = new Perk(perk1.FormKey, SkyrimRelease.SkyrimSE);
            IPerkGetter? perkDummy2 = new Perk(perk2.FormKey, SkyrimRelease.SkyrimSE);

            linkCache.Setup(c =>
                    c.ResolveAllContexts<FormList, IFormListGetter>(formlist.FormKey, ResolveTarget.Winner))
                .Returns(new List<IModContext<ISkyrimMod, ISkyrimModGetter, FormList, IFormListGetter>>
                {
                    new ModContext<ISkyrimMod, ISkyrimModGetter, FormList, IFormListGetter>(mod2, version2, null!,
                        null!),
                    new ModContext<ISkyrimMod, ISkyrimModGetter, FormList, IFormListGetter>(mod1, version1, null!,
                        null!),
                });

            linkCache.Setup(c => c.TryResolve<IPerkGetter>(perk1.FormKey, out perkDummy1, ResolveTarget.Winner))
                .Returns(true);
            linkCache.Setup(c => c.TryResolve<IPerkGetter>(perk2.FormKey, out perkDummy2, ResolveTarget.Winner))
                .Returns(true);

            var output = RecordUtils.GetRecordsFromAllImports<IPerkGetter>(formlist, linkCache.Object);

            output.Should().BeASuccess(data =>
            {
                data.Should().BeEquivalentTo(new HashSet<FormLink<IPerkGetter>> { perk1, perk2 });
            });
        }

        [Fact]
        public void Should_throw_an_InvalidRecordReferenceException_if_an_entry_cannot_be_found()
        {
            var linkCache = new Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>>(MockBehavior.Strict);
            var formlist = new FormLink<IFormListGetter>(FormKey.Factory("123456:Requiem.esp"));

            var perk1 = new FormLink<IPerkGetter>(FormKey.Factory("123ABC:Skyrim.esm"));
            var perk2 = new FormLink<IPerkGetter>(FormKey.Factory("123DEF:Skyrim.esm"));

            var mod1 = new ModKey("Skyrim", ModType.Master);
            var mod2 = new ModKey("Requiem", ModType.Plugin);

            var version1 = new FormList(formlist.FormKey, SkyrimRelease.SkyrimSE)
            {
                Items = new ExtendedList<IFormLinkGetter<ISkyrimMajorRecordGetter>> { perk1 }
            };
            var version2 = new FormList(formlist.FormKey, SkyrimRelease.SkyrimSE)
            {
                Items = new ExtendedList<IFormLinkGetter<ISkyrimMajorRecordGetter>> { perk2 }
            };

            IPerkGetter? perkDummy1 = new Perk(perk1.FormKey, SkyrimRelease.SkyrimSE);
            IPerkGetter? perkDummy2 = null;

            linkCache.Setup(c =>
                    c.ResolveAllContexts<FormList, IFormListGetter>(formlist.FormKey, ResolveTarget.Winner))
                .Returns(new List<IModContext<ISkyrimMod, ISkyrimModGetter, FormList, IFormListGetter>>
                {
                    new ModContext<ISkyrimMod, ISkyrimModGetter, FormList, IFormListGetter>(mod2, version2, null!,
                        null!),
                    new ModContext<ISkyrimMod, ISkyrimModGetter, FormList, IFormListGetter>(mod1, version1, null!,
                        null!),
                });

            linkCache.Setup(c => c.TryResolve<IPerkGetter>(perk1.FormKey, out perkDummy1, ResolveTarget.Winner))
                .Returns(true);
            linkCache.Setup(c => c.TryResolve<IPerkGetter>(perk2.FormKey, out perkDummy2, ResolveTarget.Winner))
                .Returns(false);

            var output = RecordUtils.GetRecordsFromAllImports<IPerkGetter>(formlist, linkCache.Object);

            output.Should().BeAFailed(e =>
            {
                e.Should().BeOfType<InvalidRecordReferenceException<IPerkGetter>>();
                var error = e as InvalidRecordReferenceException<IPerkGetter>;
                error.Should().NotBeNull();
                error.ParentRecord.Should().Be(version2);
                error.ProblematicMod.Should().Be(mod2);
            });
        }
    }
}