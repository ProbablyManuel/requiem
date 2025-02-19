﻿using System.IO;
using Hocon;

namespace Reqtificator.Configuration
{
    public record UserSettings(
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
    ""{KeyVerboseLogging}"": {VerboseLogging.ToString().ToLowerInvariant()},
    ""{KeyMergeLeveledLists}"": {MergeLeveledLists.ToString().ToLowerInvariant()},
    ""{KeyMergeLeveledCharacters}"": {MergeLeveledCharacters.ToString().ToLowerInvariant()},
    ""{KeyMergeLeveledLists}"": {OpenEncounterZones.ToString().ToLowerInvariant()},
    ""{KeyActorVisualAutoMerge}"": {ActorVisualAutoMerge.ToString().ToLowerInvariant()},
    ""{KeyRaceVisualAutoMerge}"": {RaceVisualAutoMerge.ToString().ToLowerInvariant()}
}}";
            File.WriteAllText(targetFile, configToWrite);
        }
    }
}