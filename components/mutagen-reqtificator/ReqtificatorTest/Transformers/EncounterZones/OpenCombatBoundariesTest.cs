using System.Collections.Immutable;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Transformers.EncounterZones;
using Xunit;
using ZoneRef = Mutagen.Bethesda.Plugins.IFormLink<Mutagen.Bethesda.Skyrim.IEncounterZoneGetter>;

namespace ReqtificatorTest.Transformers.EncounterZones
{
    public static class OpenCombatBoundariesTest
    {
        public class ShouldProcess
        {
            [Fact]
            public void Should_Select_Eligible_Records()
            {
                var formKey = FormKey.Factory("123456:Requiem.esp");
                var transformer = new OpenCombatBoundaries(ImmutableHashSet<ZoneRef>.Empty, true);
                var zone = new Mock<IEncounterZoneGetter>(MockBehavior.Strict);
                zone.As<IMajorRecordIdentifier>().SetupGet(m => m.FormKey).Returns(formKey);

                transformer.ShouldProcess(zone.Object).Should().BeTrue();
                zone.VerifyAll();
            }

            [Fact]
            public void Should_Skip_Records_In_The_ExclusionList()
            {
                var formKey = FormKey.Factory("123456:Requiem.esp");
                var exceptions = ImmutableHashSet.Create<ZoneRef>(formKey.AsLink<IEncounterZoneGetter>());
                var transformer = new OpenCombatBoundaries(exceptions, true);
                var zone = new Mock<IEncounterZoneGetter>(MockBehavior.Strict);
                zone.As<IMajorRecordIdentifier>().SetupGet(m => m.FormKey).Returns(formKey);

                transformer.ShouldProcess(zone.Object).Should().BeFalse();
                zone.VerifyAll();
            }

            [Fact]
            public void Should_Skip_All_Records_If_The_User_Disabled_The_Feature()
            {
                var transformer = new OpenCombatBoundaries(ImmutableHashSet<ZoneRef>.Empty, false);
                var zone = new Mock<IEncounterZoneGetter>(MockBehavior.Strict);

                transformer.ShouldProcess(zone.Object).Should().BeFalse();
                zone.VerifyAll();
            }
        }

        public class PatchRecord
        {
            [Fact]
            public void Should_Add_The_Open_Combat_Boundary_Flag()
            {
                var transformer = new OpenCombatBoundaries(ImmutableHashSet<ZoneRef>.Empty, true);
                var zone = new Mock<IEncounterZone>(MockBehavior.Strict);
                zone.SetupGet(m => m.Flags).Returns(EncounterZone.Flag.NeverResets);
                zone.SetupSet(m => m.Flags = EncounterZone.Flag.NeverResets | EncounterZone.Flag.DisableCombatBoundary);

                transformer.Process(zone.Object);
                zone.VerifyAll();
            }
        }
    }
}