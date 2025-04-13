using System.Linq;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Transformers;
using Xunit;

namespace ReqtificatorTest.Transformers
{
    public class ForwardDataFromTemplateTest
    {
        public static readonly ModKey Skyrim = new("Skyrim", ModType.Master);
        public static readonly ModKey VisualMod = new("Amazing Actors", ModType.Plugin);
        public static readonly ModKey UnrelatedMod = new("Serious Loot", ModType.Light);
        public static readonly ModKey DataMod = new("Skilled Actors", ModType.Plugin);

        private sealed class Fixture
        {
            public static readonly FormKey Record = FormKey.Factory("123456:Skyrim.esm");
            public readonly Mock<IDataForwardingLogic<Npc, INpcGetter>> Logic = new(MockBehavior.Strict);
            public readonly Npc SkyrimRecord = new(Record, SkyrimRelease.SkyrimSE) { EditorID = "Skyrim" };
            public readonly Npc VisualRecord = new(Record, SkyrimRelease.SkyrimSE) { EditorID = "Visual" };
            public readonly Npc UnrelatedRecord = new(Record, SkyrimRelease.SkyrimSE) { EditorID = "Unrelated" };
            public readonly Npc DataRecord = new(Record, SkyrimRelease.SkyrimSE) { EditorID = "Data" };
            public readonly Mock<ILinkCache<ISkyrimMod, ISkyrimModGetter>> Cache = new(MockBehavior.Strict);
            public readonly ForwardDataFromTemplate<Npc, INpc, INpcGetter> Transformer;

            public Fixture(bool active)
            {
                var skyrimModRef = new SkyrimMod(Skyrim, SkyrimRelease.SkyrimSE);
                var visualModRef = new SkyrimMod(VisualMod, SkyrimRelease.SkyrimSE);
                visualModRef.ModHeader.MasterReferences.Add(new MasterReference { Master = Skyrim });
                var unrelatedModRef = new SkyrimMod(UnrelatedMod, SkyrimRelease.SkyrimSE);
                unrelatedModRef.ModHeader.MasterReferences.Add(new MasterReference { Master = Skyrim });
                var dataModRef = new SkyrimMod(DataMod, SkyrimRelease.SkyrimSE);
                dataModRef.ModHeader.MasterReferences.Add(new MasterReference { Master = Skyrim });

                var modListing = new[] { skyrimModRef, visualModRef, unrelatedModRef, dataModRef }.Select(x =>
                    new ModListing<ISkyrimModGetter>(x));
                var loadOrder = new LoadOrder<IModListing<ISkyrimModGetter>>(modListing);
                Transformer =
                    new ForwardDataFromTemplate<Npc, INpc, INpcGetter>(Cache.Object, loadOrder, Logic.Object,
                        active);

                Cache.Setup(c => c.ResolveAllContexts<Npc, INpcGetter>(Record, ResolveTarget.Winner))
                    .Returns(
                    [
                        new ModContext<ISkyrimMod, ISkyrimModGetter, Npc, INpcGetter>(DataMod,
                            DataRecord, null!, null!),
                        new ModContext<ISkyrimMod, ISkyrimModGetter, Npc, INpcGetter>(UnrelatedMod,
                            UnrelatedRecord, null!, null!),
                        new ModContext<ISkyrimMod, ISkyrimModGetter, Npc, INpcGetter>(VisualMod,
                            VisualRecord, null!, null!),
                        new ModContext<ISkyrimMod, ISkyrimModGetter, Npc, INpcGetter>(Skyrim,
                            SkyrimRecord, null!, null!)
                    ]);
                Cache.Setup(c => c.ResolveContext<Npc, INpcGetter>(Record, ResolveTarget.Winner)).Returns(
                    new ModContext<ISkyrimMod, ISkyrimModGetter, Npc, INpcGetter>(DataMod, DataRecord, null!, null!));
            }
        }


        [Fact]
        public void Should_forward_changes_if_a_suitable_template_is_found()
        {
            var f = new Fixture(true);

            f.Logic.Setup(l => l.CheckRecordEquality(f.DataRecord, f.SkyrimRecord)).Returns(true);
            f.Logic.Setup(l => l.CheckRecordEquality(f.UnrelatedRecord, f.SkyrimRecord)).Returns(true);
            f.Logic.Setup(l => l.CheckRecordEquality(f.VisualRecord, f.SkyrimRecord)).Returns(false);

            f.Logic.Setup(l => l.CheckRecordEquality(f.DataRecord, f.VisualRecord)).Returns(false);

            f.Logic.Setup(l => l.ForwardDataFromTemplate(It.IsAny<Npc>(), f.VisualRecord))
                .Callback((Npc record, INpcGetter _) => record.EditorID = "modified");

            var result = f.Transformer.Process(new UnChanged<Npc, INpcGetter>(f.DataRecord));
            result.Should().BeOfType<Modified<Npc, INpcGetter>>();
            result.Record().EditorID.Should().Be("modified");
        }

        [Fact]
        public void Should_not_apply_any_changes_if_the_last_override_is_a_template_itself()
        {
            var f = new Fixture(true);

            f.Logic.Setup(l => l.CheckRecordEquality(f.DataRecord, f.SkyrimRecord)).Returns(false);

            var result = f.Transformer.Process(new UnChanged<Npc, INpcGetter>(f.DataRecord));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
        }

        [Fact]
        public void Should_not_apply_any_changes_if_no_previous_override_qualifies_as_template()
        {
            var f = new Fixture(true);

            f.Logic.Setup(l => l.CheckRecordEquality(f.DataRecord, f.SkyrimRecord)).Returns(true);
            f.Logic.Setup(l => l.CheckRecordEquality(f.UnrelatedRecord, f.SkyrimRecord)).Returns(true);
            f.Logic.Setup(l => l.CheckRecordEquality(f.VisualRecord, f.SkyrimRecord)).Returns(true);

            var result = f.Transformer.Process(new UnChanged<Npc, INpcGetter>(f.DataRecord));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
        }

        [Fact]
        public void Should_not_apply_any_changes_if_no_last_override_is_equivalent_to_the_found_template()
        {
            var f = new Fixture(true);

            f.Logic.Setup(l => l.CheckRecordEquality(f.DataRecord, f.SkyrimRecord)).Returns(true);
            f.Logic.Setup(l => l.CheckRecordEquality(f.UnrelatedRecord, f.SkyrimRecord)).Returns(true);
            f.Logic.Setup(l => l.CheckRecordEquality(f.VisualRecord, f.SkyrimRecord)).Returns(false);

            f.Logic.Setup(l => l.CheckRecordEquality(f.DataRecord, f.VisualRecord)).Returns(true);

            var result = f.Transformer.Process(new UnChanged<Npc, INpcGetter>(f.DataRecord));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
        }

        [Fact]
        public void Should_not_apply_any_changes_if_the_feature_has_been_disabled()
        {
            var f = new Fixture(false);

            var result = f.Transformer.Process(new UnChanged<Npc, INpcGetter>(f.DataRecord));
            result.Should().BeOfType<UnChanged<Npc, INpcGetter>>();
        }
    }
}