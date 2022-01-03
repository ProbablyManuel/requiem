using System.Collections.Immutable;
using System.IO;
using System.Linq;
using Hocon;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Exceptions;

namespace Reqtificator.Utils
{
    internal static class RecordUtils
    {
        public static ErrorOr<IImmutableSet<IFormLinkGetter<TGetter>>> GetRecordsFromAllImports<TGetter>(
            IFormLinkGetter<IFormListGetter> formListLink,
            ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache) where TGetter : class, ISkyrimMajorRecordGetter
        {
            IImmutableSet<IFormLinkGetter<TGetter>> records = ImmutableHashSet<IFormLinkGetter<TGetter>>.Empty;
            foreach (var recordVersion in formListLink
                .ResolveAllContexts<ISkyrimMod, ISkyrimModGetter, FormList, IFormListGetter>(linkCache))
            {
                foreach (var entry in recordVersion.Record.Items)
                {
                    if (entry.TryResolve<TGetter>(linkCache, out var resolved))
                    {
                        records = records.Add(resolved.AsLink());
                    }
                    else
                    {
                        var error = new InvalidRecordReferenceException<TGetter>(new FormLink<TGetter>(entry.FormKey),
                            recordVersion.ModKey, recordVersion.Record);
                        return new Failed<IImmutableSet<IFormLinkGetter<TGetter>>>(error);
                    }
                }
            }

            return records.AsSuccess();
        }

        public static ErrorOr<IImmutableList<(string, Config)>> LoadModConfigFiles(GameContext context,
            string filePrefix)
        {
            //TODO: graceful error handling for not parseable configuration files
            var configPath = Path.Combine(context.DataFolder, "Reqtificator", "Data");
            IImmutableList<(string, Config)> configs = context.ActiveMods
                .Select(m => Path.Combine(configPath, $"{filePrefix}_{m.ModKey.FileName}.conf"))
                .Where(f => File.Exists(f))
                .Select(f => (f, HoconConfigurationFactory.FromFile(f)))
                .ToImmutableList();
            return configs.AsSuccess();
        }
    }
}