using System;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;

namespace Reqtificator.Exceptions
{
    public abstract class ReqtificatorException : Exception
    {
        protected ReqtificatorException()
        {

        }

        protected ReqtificatorException(Exception inner) : base("", inner)
        {

        }

    }

    public class InvalidRecordReferenceException<T> : ReqtificatorException where T : class, IMajorRecordGetter
    {
        public IFormLinkGetter<T> Unresolved { get; }

        public InvalidRecordReferenceException(IFormLinkGetter<T> unresolved)
        {
            Unresolved = unresolved;
        }
    }

    public class RuleConfigurationParsingException : ReqtificatorException
    {
        public string SourceFile { get; }
        public string FailingPath { get; }

        public RuleConfigurationParsingException(string sourceFile, string failingPath)
        {
            SourceFile = sourceFile;
            FailingPath = failingPath;
        }

        public RuleConfigurationParsingException(string sourceFile, string failingPath, Exception innerException) : base(innerException)
        {
            SourceFile = sourceFile;
            FailingPath = failingPath;
        }

        public override string Message => $"failed to parse path '{FailingPath}' in file '{SourceFile}'";
    }
}