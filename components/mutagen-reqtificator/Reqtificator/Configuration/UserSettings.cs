using Hocon;
using Mutagen.Bethesda.Plugins;
using System.Collections.Immutable;
using System.IO;
using System.Linq;

namespace Reqtificator.Configuration
{
    public record UserSettings(
        bool VerboseLogging,
        bool MergeLeveledLists,
        bool MergeLeveledCharacters,
        bool OpenEncounterZones,
        ImmutableList<ModKey> NpcVisualTemplateMods,
        ImmutableList<ModKey> RaceVisualTemplateMods)
    {
        private const string KeyVerboseLogging = "verboseLogging";
        private const string KeyMergeLeveledLists = "mergeLeveledLists";
        private const string KeyMergeLeveledCharacters = "mergeLeveledCharacters";
        private const string KeyOpenEncounterZones = "openEncounterZones";
        private const string KeyNpcVisualTemplateMods = "npcVisualTemplateMods";
        private const string KeyRaceVisualTemplateMods = "raceVisualTemplateMods";

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
                NpcVisualTemplateMods: config.GetStringList(KeyNpcVisualTemplateMods)
                    .Select(it => ModKey.FromNameAndExtension(it)).ToImmutableList(),
                RaceVisualTemplateMods: config.GetStringList(KeyRaceVisualTemplateMods)
                    .Select(it => ModKey.FromNameAndExtension(it)).ToImmutableList()
            );
        }

        public void WriteToFile(string targetFile)
        {
            string FmtArray(string acc, string elem) => acc.Length > 0 ? $"{acc}, \"{elem}\"" : $"\"{elem}\"";
            var configToWrite = $@"{{
    ""verboseLogging"": {VerboseLogging.ToString().ToLowerInvariant()},
    ""mergeLeveledLists"": {MergeLeveledLists.ToString().ToLowerInvariant()},
    ""mergeLeveledCharacters"": {MergeLeveledCharacters.ToString().ToLowerInvariant()},
    ""openEncounterZones"": {OpenEncounterZones.ToString().ToLowerInvariant()},
    ""npcVisualTemplateMods"": [{NpcVisualTemplateMods.Select(it => it.FileName.String).Aggregate("", FmtArray)}],
    ""raceVisualTemplateMods"": [{RaceVisualTemplateMods.Select(it => it.FileName.String).Aggregate("", FmtArray)}]
}}";
            File.WriteAllText(targetFile, configToWrite);
        }
    }
}