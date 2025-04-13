using System.IO;
using Hocon;

namespace Reqtificator.Configuration
{
    internal record UserSettings(
        bool VerboseLogging,
        bool MergeLeveledLists,
        bool MergeLeveledCharacters,
        bool OpenEncounterZones,
        bool ActorVisualAutoMerge,
        bool RaceVisualAutoMerge)
    {
        private const string KeyVerboseLogging = "verboseLogging";
        private const string KeyMergeLeveledLists = "mergeLeveledLists";
        private const string KeyMergeLeveledCharacters = "mergeLeveledCharacters";
        private const string KeyOpenEncounterZones = "openEncounterZones";
        private const string KeyActorVisualAutoMerge = "actorVisualAutoMerge";
        private const string KeyRaceVisualAutoMerge = "raceVisualAutoMerge";

        public static UserSettings LoadUserSettings(string sourceFile)
        {
            //TODO: define and implement error handling strategy in case the user settings file is not matching the spec
            var config = HoconConfigurationFactory.FromResource<UserSettings>("DefaultUserConfig");
            if (File.Exists(sourceFile))
            {
                var userConfig = HoconConfigurationFactory.FromFile(sourceFile);
                config = userConfig.WithFallback(config);
            }

            return new UserSettings(
                VerboseLogging: config.GetBoolean(KeyVerboseLogging),
                MergeLeveledLists: config.GetBoolean(KeyMergeLeveledLists),
                MergeLeveledCharacters: config.GetBoolean(KeyMergeLeveledCharacters),
                OpenEncounterZones: config.GetBoolean(KeyOpenEncounterZones),
                ActorVisualAutoMerge: config.GetBoolean(KeyActorVisualAutoMerge),
                RaceVisualAutoMerge: config.GetBoolean(KeyRaceVisualAutoMerge)
            );
        }

        public void WriteToFile(string targetFile)
        {
            string configToWrite = $@"{{
    ""{KeyVerboseLogging}"": {(VerboseLogging ? "true" : "false")},
    ""{KeyMergeLeveledLists}"": {(MergeLeveledLists ? "true" : "false")},
    ""{KeyMergeLeveledCharacters}"": {(MergeLeveledCharacters ? "true" : "false")},
    ""{KeyMergeLeveledLists}"": {(OpenEncounterZones ? "true" : "false")},
    ""{KeyActorVisualAutoMerge}"": {(ActorVisualAutoMerge ? "true" : "false")},
    ""{KeyRaceVisualAutoMerge}"": {(RaceVisualAutoMerge ? "true" : "false")}
}}";
            File.WriteAllText(targetFile, configToWrite);
        }
    }
}