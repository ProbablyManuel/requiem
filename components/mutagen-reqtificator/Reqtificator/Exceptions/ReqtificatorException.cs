using System;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;

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

    public class InvalidTemperingDataException : ReqtificatorException
    {
        public IFormLinkGetter<ILeveledItemGetter> SourceRecord { get; }

        public InvalidTemperingDataException(IFormLinkGetter<ILeveledItemGetter> sourceRecord)
        {
            SourceRecord = sourceRecord;
        }

        public InvalidTemperingDataException(IFormLinkGetter<ILeveledItemGetter> sourceRecord, Exception innerException) : base(innerException)
        {
            SourceRecord = sourceRecord;
        }

        public override string Message => $"record '{SourceRecord.FormKey}' contains invalid tempering data";
    }

    public class MissingDependencyException : ReqtificatorException
    {
        public MissingDependencyException(string missingDependency)
        {
            MissingDependency = missingDependency;
        }

        public override string Message => $"dependency '{MissingDependency}' was not found in the setup";

        public string MissingDependency { get; }
    }

    public class VersionMismatchException : ReqtificatorException
    {
        public VersionMismatchException(RequiemVersion pluginVersion, RequiemVersion patcherVersion)
        {
            PluginVersion = pluginVersion;
            PatcherVersion = patcherVersion;
        }

        public override string Message => $"version mismatch detected! patcher version {PatcherVersion.ShortVersion()}, plugin version {PluginVersion.ShortVersion()}";

        public RequiemVersion PluginVersion { get; }
        public RequiemVersion PatcherVersion { get; }
    }
}