using System.Collections.Immutable;
using FluentAssertions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Transformers;
using Reqtificator.Transformers.EncounterZones;
using Xunit;
using ZoneRef = Mutagen.Bethesda.Plugins.IFormLink<Mutagen.Bethesda.Skyrim.IEncounterZoneGetter>;

namespace ReqtificatorTest.Transformers.EncounterZones
{
    public class OpenCombatBoundariesTest
    {
        [Fact]
        public void Should_Add_The_Open_Combat_Boundary_Flag_To_Existing_Flags()
        {
            var formKey = FormKey.Factory("123456:Requiem.esp");
            var transformer = new OpenCombatBoundaries(ImmutableHashSet<ZoneRef>.Empty, true);

            var input = new EncounterZone(formKey, SkyrimRelease.SkyrimSE)
            {
                Flags = EncounterZone.Flag.NeverResets
            };
            var reference = input.DeepCopy();

            var result = transformer.Process(new UnChanged<EncounterZone, IEncounterZoneGetter>(input));

            var mask = new EncounterZone.TranslationMask(defaultOn: true) { Flags = false };
            reference.Equals(result.Record(), mask).Should().BeTrue();

            result.Record().Flags.Should().Be(EncounterZone.Flag.NeverResets | EncounterZone.Flag.DisableCombatBoundary);
            result.IsModified().Should().BeTrue();
        }

        [Fact]
        public void Should_Add_The_Open_Combat_Boundary_Flag_Where_None_Exist()
        {
            var formKey = FormKey.Factory("123456:Requiem.esp");
            var transformer = new OpenCombatBoundaries(ImmutableHashSet<ZoneRef>.Empty, true);

            var input = new EncounterZone(formKey, SkyrimRelease.SkyrimSE);

            var result = transformer.Process(new UnChanged<EncounterZone, IEncounterZoneGetter>(input));
            result.Record().Flags.Should().Be(EncounterZone.Flag.DisableCombatBoundary);
            result.IsModified().Should().BeTrue();
        }

        [Fact]
        public void Should_Skip_Records_In_The_ExclusionList()
        {
            var formKey = FormKey.Factory("123456:Requiem.esp");
            var exceptions = ImmutableHashSet.Create<ZoneRef>(formKey.ToLink<IEncounterZoneGetter>());
            var transformer = new OpenCombatBoundaries(exceptions, true);

            var input = new EncounterZone(formKey, SkyrimRelease.SkyrimSE);

            var result = transformer.Process(new UnChanged<EncounterZone, IEncounterZoneGetter>(input));
            result.IsModified().Should().BeFalse();
        }

        [Fact]
        public void Should_Skip_All_Records_If_The_User_Disabled_The_Feature()
        {
            var formKey = FormKey.Factory("123456:Requiem.esp");
            var transformer = new OpenCombatBoundaries(ImmutableHashSet<ZoneRef>.Empty, false);

            var input = new EncounterZone(formKey, SkyrimRelease.SkyrimSE);

            var result = transformer.Process(new UnChanged<EncounterZone, IEncounterZoneGetter>(input));
            result.IsModified().Should().BeFalse();
        }
    }
}