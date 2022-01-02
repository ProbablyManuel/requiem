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
        public IMajorRecordGetter ParentRecord { get; }
        public ModKey ProblematicMod { get; }

        public InvalidRecordReferenceException(IFormLinkGetter<T> unresolved, ModKey problematicMod,
            IMajorRecordGetter parentRecord)
        {
            Unresolved = unresolved;
            ProblematicMod = problematicMod;
            ParentRecord = parentRecord;
        }

        public override string Message
        {
            get
            {
                var name = (ParentRecord.EditorID is null)
                    ? $"'{ParentRecord.FormKey}'"
                    : $"'{ParentRecord.FormKey}' ({ParentRecord.EditorID})";
                return $"record {name} contains unresolved reference {Unresolved.FormKey} in overwrite '{ProblematicMod}'";
            }
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

        public RuleConfigurationParsingException(string sourceFile, string failingPath, Exception innerException) :
            base(innerException)
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

        public InvalidTemperingDataException(IFormLinkGetter<ILeveledItemGetter> sourceRecord, Exception innerException)
            : base(innerException)
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

        public override string Message =>
            $"version mismatch detected! patcher version {PatcherVersion.ShortVersion()}, plugin version {PluginVersion.ShortVersion()}";

        public RequiemVersion PluginVersion { get; }
        public RequiemVersion PatcherVersion { get; }
    }

    public class OversizedLeveledListException : ReqtificatorException
    {
        public FormKey FormID { get; }
        public string? EditorID { get; }

        public OversizedLeveledListException(FormKey formId, string? editorId)
        {
            FormID = formId;
            EditorID = editorId;
        }

        public override string Message
        {
            get
            {
                var name = (EditorID is null) ? $"'{FormID}'" : $"'{FormID}' ({EditorID})";
                return
                    $"Leveled List '{name}' contains more than 255 entries after processing. Skyrim only supports up to 255 entries in a leveled list.";
            }
        }
    }
}