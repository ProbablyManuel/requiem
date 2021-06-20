using System;
using FluentAssertions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Transformers;
using Xunit;

namespace ReqtificatorTest.Transformers
{
    public static class ChainedTransformerV2Test
    {
        private class DummyTransformer : TransformerV2<Keyword, IKeywordGetter>
        {
            private readonly string _checkPrefix;
            private readonly string _addPrefix;

            public DummyTransformer(string checkPrefix, string addPrefix)
            {
                _checkPrefix = checkPrefix;
                _addPrefix = addPrefix;
            }

            public override TransformationResult<Keyword, IKeywordGetter> Process(
                TransformationResult<Keyword, IKeywordGetter> input)
            {
                if (input.Record().EditorID?.StartsWith(_checkPrefix, StringComparison.InvariantCulture) ?? false)
                {
                    return input.Modify(record => record.EditorID = _addPrefix + (record.EditorID ?? ""));
                }

                return input;
            }
        }

        public class ShouldProcess
        {
            [Fact]
            public void Should_apply_both_transformations_in_order_if_their_conditions_are_matched()
            {
                var transform1 = new DummyTransformer("foo", "bar_");
                var transform2 = new DummyTransformer("bar", "gus_");
                var chainedTransformer = transform1.AndThen(transform2);

                var input = new Keyword(FormKey.Factory("123456:Skyrim.esm"), SkyrimRelease.SkyrimSE)
                {
                    EditorID = "foo_record"
                };

                var result = chainedTransformer.Process(new UnChanged<Keyword, IKeywordGetter>(input));
                var mask = new Keyword.TranslationMask(defaultOn: true) { EditorID = false };
                result.Should().BeOfType<Modified<Keyword, IKeywordGetter>>();
                result.Record().Equals(input, mask).Should().BeTrue();
                result.Record().EditorID.Should().Be("gus_bar_foo_record");
            }

            [Fact]
            public void Should_use_output_of_first_transformer_as_input_for_second_transformer()
            {
                var transform1 = new DummyTransformer("foo", "bar_");
                var transform2 = new DummyTransformer("foo", "fail_");
                var chainedTransformer = transform1.AndThen(transform2);

                var input = new Keyword(FormKey.Factory("123456:Skyrim.esm"), SkyrimRelease.SkyrimSE)
                {
                    EditorID = "foo_record"
                };

                var result = chainedTransformer.Process(new UnChanged<Keyword, IKeywordGetter>(input));
                var mask = new Keyword.TranslationMask(defaultOn: true) { EditorID = false };
                result.Should().BeOfType<Modified<Keyword, IKeywordGetter>>();
                result.Record().Equals(input, mask).Should().BeTrue();
                result.Record().EditorID.Should().Be("bar_foo_record");
            }

            [Fact]
            public void Should_use_original_input_for_second_transformer_if_first_transformer_did_not_modify_it()
            {
                var transform1 = new DummyTransformer("bar", "fail_");
                var transform2 = new DummyTransformer("foo", "bar_");
                var chainedTransformer = transform1.AndThen(transform2);

                var input = new Keyword(FormKey.Factory("123456:Skyrim.esm"), SkyrimRelease.SkyrimSE)
                {
                    EditorID = "foo_record"
                };

                var result = chainedTransformer.Process(new UnChanged<Keyword, IKeywordGetter>(input));
                var mask = new Keyword.TranslationMask(defaultOn: true) { EditorID = false };
                result.Should().BeOfType<Modified<Keyword, IKeywordGetter>>();
                result.Record().Equals(input, mask).Should().BeTrue();
                result.Record().EditorID.Should().Be("bar_foo_record");
            }

            [Fact]
            public void Should_return_the_original_input_unchanged_if_neither_transformation_changed_it()
            {
                var transform1 = new DummyTransformer("bar", "fail_");
                var transform2 = new DummyTransformer("foo", "more_fail_");
                var chainedTransformer = transform1.AndThen(transform2);

                var input = new Keyword(FormKey.Factory("123456:Skyrim.esm"), SkyrimRelease.SkyrimSE)
                {
                    EditorID = "gus_record"
                };

                var result = chainedTransformer.Process(new UnChanged<Keyword, IKeywordGetter>(input));
                result.Should().BeOfType<UnChanged<Keyword, IKeywordGetter>>();
                result.Record().Equals(input).Should().BeTrue();
            }
        }
    }
}