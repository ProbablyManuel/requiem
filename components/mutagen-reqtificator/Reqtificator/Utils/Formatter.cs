using System;
using System.Globalization;
using System.Linq;
using System.Text.RegularExpressions;

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
            return string.Format(CultureInfo.InvariantCulture, multiLineString.TrimStart().Replace($"\r\n{indentationStr}", "\r\n", StringComparison.InvariantCulture), args);
        }

        public static string FormatMultiline(string multiLineString, char indentationSymbol, params string[] args)
        {
            string trimmedText = Regex.Replace(multiLineString, $"\r\n *{Regex.Escape(indentationSymbol.ToString())}", "\r\n");
            string finalText = Regex.Replace(trimmedText, $"^ *{Regex.Escape(indentationSymbol.ToString())}", "");
            return string.Format(CultureInfo.InvariantCulture, finalText, args);
        }
    }
}
