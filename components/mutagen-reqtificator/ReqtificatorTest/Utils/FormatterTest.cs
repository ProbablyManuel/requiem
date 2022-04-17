using System;
using FluentAssertions;
using Reqtificator.Utils;
using Xunit;

namespace ReqtificatorTest.Utils
{
    public class FormatterTest
    {
        [Fact]
        public void Should_Remove_The_Indent_From_Multiline_Strings()
        {
            string message = @"
            This is a message
            which has been indented.
            It should appear
            as a single line without the indent.";

            string expectedMessage = "This is a message\r\nwhich has been indented.\r\n" +
                "It should appear\r\nas a single line without the indent.";

            string result = Formatter.FormatMultiline(message);

            _ = result.Should().Be(expectedMessage);
        }

        [Fact]
        public void Should_Throw_An_Error_If_String_Is_Not_Multiline()
        {
            string message = "This single line should cause an exception";

            Action act = () => Formatter.FormatMultiline(message);
            _ = act.Should().Throw<FormatException>();
        }

        [Fact]
        public void Should_remove_the_indent_Up_to_the_indentation_character_from_multiline_strings()
        {
            string message = @"|This is a message
                |which has been indented.
            |It should appear
                |as a single line without the indent.";

            string expectedMessage = "This is a message\r\nwhich has been indented.\r\n" +
                                     "It should appear\r\nas a single line without the indent.";

            string result = Formatter.FormatMultiline(message, '|');

            _ = result.Should().Be(expectedMessage);
        }
    }
}
