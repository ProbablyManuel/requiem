using System;
using System.IO;
using Reqtificator.Configuration;
using Reqtificator.Events;
using Reqtificator.Events.Outcomes;
using Serilog;

namespace Reqtificator
{
    internal static class HeadlessRunner
    {
        public const int Success = 0;
        public const int Failure = 1;
        public const int StoppedOnWarning = 2;
        public const int InvalidArguments = 64;

        public static int Run(CommandLineArguments arguments)
        {
            if (arguments.ShowHelp)
            {
                arguments.PrintHelpOrErrors();
                return Success;
            }

            if (arguments.Errors.Count > 0)
            {
                arguments.PrintHelpOrErrors();
                return InvalidArguments;
            }

            try
            {
                return Patch(arguments);
            }
            finally
            {
                Log.CloseAndFlush();
            }
        }

        [System.Diagnostics.CodeAnalysis.SuppressMessage("Design", "CA1031:Do not catch general exception types", Justification = "Any failure is reported with an exit code")]
        private static int Patch(CommandLineArguments arguments)
        {
            var events = new InternalEvents();
            var progress = new Progress();
            events.StateChanged += (_, state) => progress.OnStateChanged(state);

            try
            {
                var logContext = new ReqtificatorLogContext(LogUtils.DefaultLogFileName);
                Log.Information("Starting the Reqtificator without its window");
                _ = new Backend(events, logContext, arguments.Release, saveUserSettings: false);
                if (progress.Error is not null)
                {
                    return Failure;
                }

                if (progress.Warning is not null && !arguments.IgnoreWarnings)
                {
                    Console.Error.WriteLine("Stopped because of the warning above. Run with --ignore-warnings to create the patch anyway.");
                    return StoppedOnWarning;
                }

                var userSettings = progress.UserSettings ?? throw new InvalidOperationException("the Reqtificator never became ready to patch");
                var settings = arguments.ApplyOverrides(userSettings);
                Log.Information("Patching with {settings}", settings);
                Console.WriteLine($"Patching with {settings}");
                events.RequestPatch(settings);
                return Success;
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Patching process failed!");
                if (progress.Error is null)
                {
                    Console.Error.WriteLine($"Patching failed: {ex.Message}");
                }

                if (File.Exists(LogUtils.DefaultLogFileName))
                {
                    Console.Error.WriteLine($"See {Path.GetFullPath(LogUtils.DefaultLogFileName)} for details.");
                }

                return Failure;
            }
        }

        private sealed class Progress
        {
            private string _lastProgress = "";

            public UserSettings? UserSettings { get; private set; }

            public ReqtificatorOutcome? Error { get; private set; }

            public ReqtificatorOutcome? Warning { get; private set; }

            public void OnStateChanged(ReqtificatorState state)
            {
                Log.Information("Reqtificator state: " + state.Readable);
                switch (state)
                {
                    case ReadyToPatchState ready:
                        UserSettings ??= ready.UserSettings;
                        break;
                    case PatchingState when state.Readable != _lastProgress:
                        _lastProgress = state.Readable;
                        Console.WriteLine(state.Readable);
                        break;
                    case StoppedState stopped:
                        OnStopped(stopped.Outcome);
                        break;
                    default:
                        break;
                }
            }

            private void OnStopped(ReqtificatorOutcome outcome)
            {
                switch (outcome.Status)
                {
                    case PatchStatus.ERROR when Error is not null && outcome.Title == Error.Title && outcome.Message == Error.Message:
                        return;
                    case PatchStatus.ERROR:
                        Error = outcome;
                        break;
                    case PatchStatus.WARNING:
                        Warning ??= outcome;
                        break;
                    case PatchStatus.SUCCESS:
                    case PatchStatus.MESSAGE:
                    default:
                        break;
                }

                var output = outcome.Status is PatchStatus.ERROR or PatchStatus.WARNING ? Console.Error : Console.Out;
                output.WriteLine();
                output.WriteLine($"{outcome.Status}: {outcome.Title}");
                output.WriteLine(outcome.Message.Replace("**", "", StringComparison.Ordinal));
            }
        }
    }
}
