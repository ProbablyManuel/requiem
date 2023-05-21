using System.IO;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator;
using Reqtificator.Events.Outcomes;
using Xunit;

namespace ReqtificatorTest
{
    public class BackendTest
    {
        [Fact]
        public void Should_export_a_patch_if_everything_works_as_planned()
        {
            var dummyMod = new SkyrimMod(new ModKey("export", ModType.Plugin), SkyrimRelease.SkyrimSE);
            for (int i = 1; i < 10; i++)
            {
                _ = new Armor(dummyMod, $"item{i}");
            }
            string tempDir = Path.GetTempPath();
            ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder = new LoadOrder<IModListing<ISkyrimModGetter>>();

            var outcome = Backend.WritePatchToDisk(dummyMod, tempDir, loadOrder);
            outcome.Should().BeNull();
            File.Exists(Path.Combine(tempDir, dummyMod.ModKey.FileName)).Should().BeTrue();
        }

        [Fact]
        public void Should_return_a_failed_outcome_if_there_are_too_many_masters()
        {
            var dummyMod = new SkyrimMod(new ModKey("export", ModType.Plugin), SkyrimRelease.SkyrimSE);
            ILoadOrder<IModListing<ISkyrimModGetter>> loadOrder = new LoadOrder<IModListing<ISkyrimModGetter>>();
            for (int i = 1; i < 260; i++)
            {
                var modKey = new ModKey($"dependency{i}", ModType.Plugin);
                var record = new Armor(new FormKey(modKey, 42), SkyrimRelease.SkyrimSE);
                var mod = new ModListing<ISkyrimModGetter>(new SkyrimMod(modKey, SkyrimRelease.SkyrimSE), true);
                dummyMod.Armors.Add(record);
                loadOrder.Add(mod);
            }
            string tempDir = Path.GetTempPath();

            var outcome = Backend.WritePatchToDisk(dummyMod, tempDir, loadOrder);
            outcome.Should().NotBeNull();
            outcome.Should().BeOfType<TooManyMasters>();
        }
    }
}