using System;
using System.Collections.Immutable;
using FluentAssertions;
using Hocon;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator;
using Reqtificator.Exceptions;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Rules;
using Reqtificator.Transformers.Rules.Conditions;
using Xunit;

namespace ReqtificatorTest.Transformers
{
    public class AssignmentsFromRulesTest
    {
        static readonly AssignmentRule<IArmorGetter, IKeywordGetter> Node1 = new(
            assignments: ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty
                .Add(new FormLink<IKeywordGetter>(FormKey.Factory("ABC123:Assign.esp"))),
            conditions: ImmutableHashSet<IAssignmentCondition<IArmorGetter>>.Empty.Add(
                new HasAllKeywords<IArmorGetter>(ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty.Add(
                    new FormLink<IKeywordGetter>(FormKey.Factory("123ABC:Skyrim.esm"))).Add(
                    new FormLink<IKeywordGetter>(FormKey.Factory("123890:Skyrim.esm"))))
            ),
            nodeName: "some_node",
            subNodes: ImmutableHashSet<AssignmentRule<IArmorGetter, IKeywordGetter>>.Empty
        );

        static readonly AssignmentRule<IArmorGetter, IKeywordGetter> Node2 = new(
            assignments: ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty
                .Add(new FormLink<IKeywordGetter>(FormKey.Factory("DEF123:Assign.esp"))),
            conditions: ImmutableHashSet<IAssignmentCondition<IArmorGetter>>.Empty
                .Add(new HasAllKeywords<IArmorGetter>(
                    ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty.Add(
                        new FormLink<IKeywordGetter>(FormKey.Factory("123DEF:Skyrim.esm")))
                )),
            nodeName: "other_node",
            subNodes: ImmutableHashSet<AssignmentRule<IArmorGetter, IKeywordGetter>>.Empty
        );

        static readonly AssignmentRule<IArmorGetter, IKeywordGetter> Expected = new(
            assignments: ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty,
            conditions: ImmutableHashSet<IAssignmentCondition<IArmorGetter>>.Empty
                .Add(new HasAllKeywords<IArmorGetter>(ImmutableHashSet<IFormLinkGetter<IKeywordGetter>>.Empty
                    .Add(new FormLink<IKeywordGetter>(FormKey.Factory("123456:Skyrim.esm")))
                )),
            nodeName: "feature_foo",
            subNodes: ImmutableHashSet<AssignmentRule<IArmorGetter, IKeywordGetter>>.Empty.Add(Node1).Add(Node2)
        );

        [Fact]
        public void Should_parse_a_nice_and_well_formatted_config_file_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ ""123456:Skyrim.esm"" ]
                    some_node {
                        keywords_all = [ ""123ABC:Skyrim.esm"", ""123890:Skyrim.esm"" ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(rawConfig, "tests");
            result.Should().BeASuccess(r =>
            {
                r.Should().HaveCount(1);
                r[0].Should().Be(Expected);
            });
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_triple_quoted_strings_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ """"""123456:Skyrim.esm"""""" ]
                    some_node {
                        keywords_all = [ ""123ABC:Skyrim.esm"", ""123890:Skyrim.esm"" ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(rawConfig, "tests");
            result.Should().BeOfType<Success<ImmutableList<AssignmentRule<IArmorGetter, IKeywordGetter>>>>();
            result.Should().BeASuccess(r =>
            {
                r.Should().HaveCount(1);
                r[0].Should().Be(Expected);
            });
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_SkyProc_style_formIds_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ ""123456 Skyrim.esm"" ]
                    some_node {
                        keywords_all = [ ""123ABC:Skyrim.esm"", ""123890:Skyrim.esm"" ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(rawConfig, "tests");
            result.Should().BeASuccess(r =>
            {
                r.Should().HaveCount(1);
                r[0].Should().Be(Expected);
            });
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_nested_array_formIds_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ [""123456:Skyrim.esm""] ]
                    some_node {
                        keywords_all = [ [""123ABC:Skyrim.esm""] [""123890:Skyrim.esm""] ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(rawConfig, "tests");
            result.Should().BeASuccess(r =>
            {
                r.Should().HaveCount(1);
                r[0].Should().Be(Expected);
            });
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_nested_object_formIds_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ { foo { moreFoo = ""123456:Skyrim.esm""} } ]
                    some_node {
                        keywords_all = [ { foo = ""123ABC:Skyrim.esm"", bla = ""123890:Skyrim.esm""} ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(rawConfig, "tests");
            result.Should().BeASuccess(r =>
            {
                r.Should().HaveCount(1);
                r[0].Should().Be(Expected);
            });
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_unquoted_SkyProc_style_formIds_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ 123456 Skyrim.esm ]
                    some_node {
                        keywords_all = [ ""123ABC:Skyrim.esm"", ""123890:Skyrim.esm"" ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(rawConfig, "tests");
            result.Should().BeASuccess(r =>
            {
                r.Should().HaveCount(1);
                r[0].Should().Be(Expected);
            });
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_string_array_formIds_without_commas_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ 123456 Skyrim.esm ]
                    some_node {
                        keywords_all = [ ""123ABC:Skyrim.esm""
                                         ""123890:Skyrim.esm"" ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(rawConfig, "tests");
            result.Should().BeASuccess(r =>
            {
                r.Should().HaveCount(1);
                r[0].Should().Be(Expected);
            });
        }

        [Fact]
        public void Should_parse_a_valid_config_file_with_unquoted_SkyProc_style_formIds_in_nested_objects_into_a_rule()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ 123456 Skyrim.esm ]
                    some_node {
                        keywords_all = [ {foo = 123ABC Skyrim.esm
                                          bla = 123890 Skyrim.esm} ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(rawConfig, "tests");
            result.Should().BeASuccess(r =>
            {
                r.Should().HaveCount(1);
                r[0].Should().Be(Expected);
            });
        }


        [Fact]
        public void Should_throw_a_config_parsing_exception_when_processing_incorrectly_structured_data()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ false ]
                    some_node {
                        keywords_all = [ {foo = 123ABC Skyrim.esm
                                          bla = 123890 Skyrim.esm} ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(rawConfig, "tests");
            result.Should().BeAFailed(e =>
            {
                e.Should().BeOfType<RuleConfigurationParsingException>();
                ((RuleConfigurationParsingException)e).FailingPath.Should().Be("feature_foo.keywords_all");
                ((RuleConfigurationParsingException)e).SourceFile.Should().Be("tests");
            });
        }

        [Fact]
        public void Should_throw_a_config_parsing_exception_when_processing_an_invalid_formId()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [ 123XYZ Skyrim.esm ]
                    some_node {
                        keywords_all = [ {foo = 123ABC Skyrim.esm
                                          bla = 123890 Skyrim.esm} ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(rawConfig, "tests");
            result.Should().BeAFailed(e =>
            {
                e.Should().BeOfType<RuleConfigurationParsingException>();
                ((RuleConfigurationParsingException)e).FailingPath.Should().Be("feature_foo.keywords_all");
                ((RuleConfigurationParsingException)e).SourceFile.Should().Be("tests");
                e.InnerException.Should().BeOfType<ArgumentException>();
                e.InnerException!.Message.Should().Be("Malformed FormKey string: 123XYZ:Skyrim.esm");
            });
        }

        [Fact]
        public void Should_throw_a_config_parsing_exception_when_parsing_a_condition_with_empty_keyword_list()
        {
            var rawConfig = HoconConfigurationFactory.ParseString(@"
                feature_foo {
                    keywords_all = [  ]
                    some_node {
                        keywords_all = [ {foo = 123ABC Skyrim.esm
                                          bla = 123890 Skyrim.esm} ]
                        keywords_assign = [ ""ABC123:Assign.esp""]
                    }
                    other_node {
                        keywords_all = [ ""123DEF:Skyrim.esm"" ]
                        keywords_assign = [ ""DEF123:Assign.esp"" ]
                    }
                }");

            var result = AssignmentsFromRules.LoadKeywordRules<IArmorGetter>(rawConfig, "tests");
            result.Should().BeAFailed(e =>
            {
                e.Should().BeOfType<RuleConfigurationParsingException>();
                ((RuleConfigurationParsingException)e).FailingPath.Should().Be("feature_foo.keywords_all");
                ((RuleConfigurationParsingException)e).SourceFile.Should().Be("tests");
                e.InnerException.Should().BeOfType<ArgumentException>();
                e.InnerException!.Message.Should().Be("keywords must be non-empty (Parameter 'keywords')");
            });
        }
    }
}