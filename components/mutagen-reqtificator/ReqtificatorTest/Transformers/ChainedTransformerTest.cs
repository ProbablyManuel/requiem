using System;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda.Plugins.Records;
using Reqtificator.Transformers;
using Xunit;

namespace System.Runtime.CompilerServices
{
    public record IsExternalInit;
}

namespace ReqtificatorTest.Transformers
{
    public static class ChainedTransformerTest
    {
        private class DummyTransformer : Transformer<MajorRecord, IMajorRecord, IMajorRecordGetter>
        {
            private readonly string _prefix;

            public DummyTransformer(string prefix)
            {
                _prefix = prefix;
            }

            public override bool ShouldProcess(IMajorRecordGetter record)
            {
                return record.EditorID?.StartsWith(_prefix, StringComparison.InvariantCulture) ?? false;
            }

            public override void Process(IMajorRecord record)
            {
                record.EditorID = "bar_" + (record.EditorID ?? "");
            }
        }

        public class ShouldProcess
        {
            [Fact]
            public void Should_Select_A_Record_If_Either_Transformation_Applies()
            {
                var transform1 = new DummyTransformer("foo");
                var transform2 = new DummyTransformer("bar");
                var chainedTansformer =
                    new ChainedTransformer<MajorRecord, IMajorRecord, IMajorRecordGetter>(transform1, transform2);

                var toPatch = new Mock<IMajorRecordGetter>(MockBehavior.Strict);
                toPatch.Setup(m => m.EditorID).Returns("foo_record");
                chainedTansformer.ShouldProcess(toPatch.Object).Should().BeTrue();

                toPatch.Setup(m => m.EditorID).Returns("bar_record");

                chainedTansformer.ShouldProcess(toPatch.Object).Should().BeTrue();
                toPatch.VerifyAll();
            }

            [Fact]
            public void Should_Skip_A_Record_If_Neither_Transformation_Applies()
            {
                var transform1 = new DummyTransformer("foo");
                var transform2 = new DummyTransformer("bar");
                var chainedTansformer =
                    new ChainedTransformer<MajorRecord, IMajorRecord, IMajorRecordGetter>(transform1, transform2);

                var toPatch = new Mock<IMajorRecordGetter>(MockBehavior.Strict);
                toPatch.Setup(m => m.EditorID).Returns("epic_record");

                chainedTansformer.ShouldProcess(toPatch.Object).Should().BeFalse();
                toPatch.VerifyAll();
            }
        }

        public class PatchRecord
        {
            [Fact]
            public void Should_Apply_First_Transformation_If_Its_Condition_Is_Met()
            {
                var transform1 = new DummyTransformer("foo");
                var transform2 = new DummyTransformer("epic");
                var chainedTansformer =
                    new ChainedTransformer<MajorRecord, IMajorRecord, IMajorRecordGetter>(transform1, transform2);

                var toPatch = new Mock<IMajorRecord>(MockBehavior.Strict);
                var toPatchGetter = toPatch.As<IMajorRecordGetter>();
                toPatchGetter.SetupGet(m => m.EditorID).Returns("foo_record");
                toPatch.SetupGet(m => m.EditorID).Returns("foo_record");
                toPatch.SetupSet(m => m.EditorID = "bar_foo_record");

                chainedTansformer.Process(toPatch.Object);
                toPatch.VerifyAll();
            }

            [Fact]
            public void Should_Apply_Second_Transformation_If_Its_Condition_Is_Met()
            {
                var transform1 = new DummyTransformer("epic");
                var transform2 = new DummyTransformer("bar");
                var chainedTansformer =
                    new ChainedTransformer<MajorRecord, IMajorRecord, IMajorRecordGetter>(transform1, transform2);

                var toPatch = new Mock<IMajorRecord>(MockBehavior.Strict);
                var toPatchGetter = toPatch.As<IMajorRecordGetter>();
                toPatchGetter.SetupGet(m => m.EditorID).Returns("bar_record");
                toPatch.SetupGet(m => m.EditorID).Returns("bar_record");
                toPatch.SetupSet(m => m.EditorID = "bar_bar_record");

                chainedTansformer.Process(toPatch.Object);
                toPatch.VerifyAll();
            }


            [Fact]
            public void Should_Evaluate_Condition_For_Second_Transformation_After_Applying_The_First_Transformation()
            {
                var transform1 = new DummyTransformer("foo");
                var transform2 = new DummyTransformer("bar");
                var chainedTansformer =
                    new ChainedTransformer<MajorRecord, IMajorRecord, IMajorRecordGetter>(transform1, transform2);

                var toPatch = new Mock<IMajorRecord>(MockBehavior.Strict);
                var toPatchGetter = toPatch.As<IMajorRecordGetter>();
                toPatchGetter.SetupSequence(m => m.EditorID).Returns("foo_record").Returns("bar_foo_record");
                toPatch.SetupSequence(m => m.EditorID).Returns("foo_record").Returns("bar_foo_record");
                toPatch.SetupSet(m => m.EditorID = "bar_foo_record");
                toPatch.SetupSet(m => m.EditorID = "bar_bar_foo_record");

                chainedTansformer.Process(toPatch.Object);
                toPatch.VerifyAll();
            }
        }
    }
}