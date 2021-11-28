using System;
using System.Collections.Generic;
using Mutagen.Bethesda.Plugins;
using Reqtificator.Configuration;

namespace Reqtificator.Events
{
    internal abstract class ReqtificatorState
    {
        protected ReqtificatorState(string readable, bool isPatching, double percentageProgress = 0.0)
        {
            Readable = readable;
            IsPatching = isPatching;
            PercentageProgress = percentageProgress;
        }

        public static ReadyToPatchState ReadyToPatch(UserSettings userConfig, IEnumerable<ModKey> modKeys)
        {
            return new ReadyToPatchState(userConfig, modKeys);
        }
        public static PatchingState Patching(double percentageProgress, string progressMessage)
        {
            return new PatchingState(percentageProgress, progressMessage);
        }

        public static StoppedState Stopped(ReqtificatorOutcome checkResult)
        {
            return new StoppedState(checkResult);
        }

        public string Readable { get; private set; }

        public bool IsPatching { get; private set; }
        public double PercentageProgress { get; private set; }
    }
    internal class StoppedState : ReqtificatorState
    {
        public StoppedState(ReqtificatorOutcome outcome)
            : base(outcome == null ? throw new ArgumentNullException(nameof(outcome)) : outcome.Title, false)
        {
            Outcome = outcome;
        }

        public ReqtificatorOutcome Outcome { get; private set; }
    }

    internal class ReadyToPatchState : ReqtificatorState
    {
        public ReadyToPatchState(UserSettings userSettings, IEnumerable<ModKey> activeMods) : base("Ready to Patch...", false)
        {
            UserSettings = userSettings;
            ActiveMods = activeMods;
        }

        public UserSettings UserSettings { get; private set; }
        public IEnumerable<ModKey> ActiveMods { get; private set; }
    }

    internal class PatchingState : ReqtificatorState
    {
        public PatchingState(double percentageProgress, string progressMessage) : base("Patching... " + progressMessage, true, percentageProgress) { }
    }
}