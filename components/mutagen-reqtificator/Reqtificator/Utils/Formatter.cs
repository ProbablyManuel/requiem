using System;
using System.Globalization;
using System.Linq;

namespace Reqtificator.Utils
{
    public static class Formatter
    {
        public static string FormatMultiline(string multiLineString, params string[] args)
        {
            if (string.IsNullOrEmpty(multiLineString) || !multiLineString.StartsWith(Environment.NewLine, StringComparison.InvariantCulture))
            {
                throw new FormatException("String must not be empty and must start with a NewLine character.");
            }

            int indentationSize = multiLineString.Skip(Environment.NewLine.Length)
                                    .TakeWhile(char.IsWhiteSpace)
                                    .Count();

            string indentationStr = new(' ', indentationSize);
            Console.WriteLine("indentation string is '{0}'", indentationStr);
            return string.Format(CultureInfo.InvariantCulture, multiLineString.TrimStart().Replace($"\r\n{indentationStr}", "\r\n", StringComparison.InvariantCulture), args);
        }
    }
}
