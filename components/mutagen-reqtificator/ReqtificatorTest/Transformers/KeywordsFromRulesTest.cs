using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Hocon;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Rules;
using Reqtificator.Transformers.Rules.Conditions;
using Xunit;

namespace ReqtificatorTest.Transformers
{
    public class KeywordsFromRulesTest
    {
        static readonly AssignmentRule<IArmorGetter, IKeywordGetter> Node1 = new(
            assignments: new HashSet<IFormLinkGetter<IKeywordGetter>>
                {new FormLink<IKeywordGetter>(FormKey.Factory("ABC123:Assign.esp"))},
            conditions: ImmutableHashSet<IAssignmentCondition<IArmorGetter>>.Empty.Add(
                new HasAllKeywords<IArmorGetter>(ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty.Add(
                    new FormLink<IKeywordGetter>(FormKey.Factory("123ABC:Skyrim.esm"))))
            ),
            nodeName: "some_node",
            subNodes: new HashSet<AssignmentRule<IArmorGetter, IKeywordGetter>>()
        );

        static readonly AssignmentRule<IArmorGetter, IKeywordGetter> Node2 = new(
            assignments: new HashSet<IFormLinkGetter<IKeywordGetter>>
                {new FormLink<IKeywordGetter>(FormKey.Factory("DEF123:Assign.esp"))},
            conditions: new HashSet<IAssignmentCondition<IArmorGetter>>
            {
                new HasAllKeywords<IArmorGetter>(new HashSet<IFormLinkGetter<IKeywordGetter>>
                    {new FormLink<IKeywordGetter>(FormKey.Factory("123DEF:Skyrim.esm"))})
            },
            nodeName: "other_node",
            subNodes: new HashSet<AssignmentRule<IArmorGetter, IKeywordGetter>>()
        );

        static readonly AssignmentRule<IArmorGetter, IKeywordGetter> Expected = new(
            assignments: new HashSet<IFormLinkGetter<IKeywordGetter>>(),
            conditions: new HashSet<IAssignmentCondition<IArmorGetter>>
            {
                new HasAllKeywords<IArmorGetter>(new HashSet<IFormLinkGetter<IKeywordGetter>>
                    {new FormLink<IKeywordGetter>(FormKey.Factory("123456:Skyrim.esm"))})
            },
            nodeName: "feature_foo",
            subNodes: new HashSet<AssignmentRule<IArmorGetter, IKeywordGetter>> { Node1, Node2 }
        );

        [Fact]
        public void Should_parse_a_nice_and_well_formatted_config_file_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ ""123456:Skyrim.esm"" ]
                    some_node {
                        keywords_all = [ ""123ABC:Skyrim.esm"" ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = KeywordsFromRules.LoadFromConfigurationFile<IArmorGetter>(rawConfig);
            result.Should().HaveCount(1);
            result[0].Should().Be(Expected);
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_triple_quoted_strings_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ """"""123456:Skyrim.esm"""""" ]
                    some_node {
                        keywords_all = [ ""123ABC:Skyrim.esm"" ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = KeywordsFromRules.LoadFromConfigurationFile<IArmorGetter>(rawConfig);
            result.Should().HaveCount(1);
            result[0].Should().Be(Expected);
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_SkyProc_style_formIds_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ ""123456 Skyrim.esm"" ]
                    some_node {
                        keywords_all = [ ""123ABC:Skyrim.esm"" ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = KeywordsFromRules.LoadFromConfigurationFile<IArmorGetter>(rawConfig);
            result.Should().HaveCount(1);
            result[0].Should().Be(Expected);
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_nested_array_formIds_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ [""123456:Skyrim.esm""] ]
                    some_node {
                        keywords_all = [ ""123ABC:Skyrim.esm"" ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = KeywordsFromRules.LoadFromConfigurationFile<IArmorGetter>(rawConfig);
            result.Should().HaveCount(1);
            result[0].Should().Be(Expected);
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_nested_object_formIds_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ { foo = ""123456:Skyrim.esm""} ]
                    some_node {
                        keywords_all = [ ""123ABC:Skyrim.esm"" ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = KeywordsFromRules.LoadFromConfigurationFile<IArmorGetter>(rawConfig);
            result.Should().HaveCount(1);
            result[0].Should().Be(Expected);
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_unquoted_SkyProc_style_formIds_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ 123456 Skyrim.esm ]
                    some_node {
                        keywords_all = [ ""123ABC:Skyrim.esm"" ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = KeywordsFromRules.LoadFromConfigurationFile<IArmorGetter>(rawConfig);
            result.Should().HaveCount(1);
            result[0].Should().Be(Expected);
        }
    }
}