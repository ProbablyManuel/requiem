using System;
using System.IO;
using Mutagen.Bethesda.Plugins.Records;
using Serilog;
using Serilog.Context;
using Serilog.Core;
using Serilog.Events;

namespace Reqtificator
{
    internal class ReqtificatorLogContext
    {
        public LoggingLevelSwitch LogLevel { get; }

        public ReqtificatorLogContext(string logName)
        {
            File.Delete(logName);
            LogLevel = new LoggingLevelSwitch(LogEventLevel.Information);
            const string format =
                "{Timestamp} [{Level:u3}] {RecordFormId} - {Message:lj} ({RecordEditorId}){NewLine}{Exception}";
            Log.Logger = new LoggerConfiguration()
                .MinimumLevel.Debug()
                .Enrich.FromLogContext()
                .WriteTo.File(logName, levelSwitch: LogLevel, outputTemplate: format)
                .CreateLogger();
        }
    }

    internal static class LogUtils
    {
        public const string DefaultLogFileName = "Reqtificator.log";

        public static Func<Func<T>, T> WithRecordContextLog<T>(IMajorRecordGetter record)
        {
            return f =>
            {
                using (LogContext.PushProperty("RecordEditorId", record.EditorID))
                {
                    using (LogContext.PushProperty("RecordFormId", record.FormKey.ToString()))
                    {
                        return f();
                    }
                }
            };
        }
    }
}