using System.IO;
using System.Linq;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator;
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
            var loadOrder = new LoadOrder<IModListing<ISkyrimModGetter>>();

            Backend.WritePatchToDisk(dummyMod, tempDir, loadOrder);
            File.Exists(Path.Combine(tempDir, dummyMod.ModKey.FileName)).Should().BeTrue();
        }

        [Fact]
        public void Should_split_the_patch_if_there_are_too_many_masters()
        {
            var dummyMod = new SkyrimMod(new ModKey("export", ModType.Plugin), SkyrimRelease.SkyrimSE);
            var loadOrder = new LoadOrder<IModListing<ISkyrimModGetter>>();
            for (int i = 1; i < 260; i++)
            {
                var modKey = new ModKey($"dependency{i}", ModType.Plugin);
                var record = new Armor(new FormKey(modKey, 42), SkyrimRelease.SkyrimSE);
                var mod = new ModListing<ISkyrimModGetter>(new SkyrimMod(modKey, SkyrimRelease.SkyrimSE), true);
                dummyMod.Armors.Add(record);
                loadOrder.Add(mod);
            }
            string tempDir = Directory.CreateTempSubdirectory().FullName;

            try
            {
                Backend.WritePatchToDisk(dummyMod, tempDir, loadOrder);

                var exportedFiles = Directory.GetFiles(tempDir).Select(Path.GetFileName);
                exportedFiles.Should().BeEquivalentTo("export.esp", "export_2.esp");

                var exportedMods = Directory.GetFiles(tempDir)
                    .Select(f => SkyrimMod.CreateFromBinary(f, SkyrimRelease.SkyrimSE))
                    .ToList();
                exportedMods.Should().AllSatisfy(m => m.ModHeader.MasterReferences.Count.Should().BeLessThanOrEqualTo(254));
                exportedMods.Sum(m => m.Armors.Count).Should().Be(259);
            }
            finally
            {
                Directory.Delete(tempDir, true);
            }
        }
    }
}