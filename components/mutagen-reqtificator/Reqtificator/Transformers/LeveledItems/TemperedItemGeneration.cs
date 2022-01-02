using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Globalization;
using System.Text.RegularExpressions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Exceptions;
using Serilog;

namespace Reqtificator.Transformers.LeveledItems
{
    internal class TemperedItemGeneration : Transformer<LeveledItem, ILeveledItem, ILeveledItemGetter>
    {
        private readonly IImmutableSet<ModKey> _registeredMods;

        private static readonly Regex Pattern =
            new Regex("^[^_]+_.+_Quality(?<tier>[0-9]+)_(?<size>[A-z])_(?<distribution>[A-z]+)$",
                RegexOptions.IgnoreCase);

        public TemperedItemGeneration(IImmutableSet<ModKey> modsRegisteredForFeature)
        {
            _registeredMods = modsRegisteredForFeature;
        }

        public override TransformationResult<LeveledItem, ILeveledItemGetter> Process(
            TransformationResult<LeveledItem, ILeveledItemGetter> input)
        {
            var temperingData = ExtractQualityData(input.Record());

            if (temperingData is null || _registeredMods.ContainsNot(input.Record().FormKey.ModKey)) return input;

            if ((input.Record().Entries?.Count ?? 0) != 1)
                throw new InvalidTemperingDataException(input.Record().AsLinkGetter());

            return input.Modify(record =>
            {
                var itemData = record.Entries![0].Data;
                record.Entries.Clear();
                temperingData.GetItemHealthValues().ForEach(healthData =>
                    record.Entries.Add(new LeveledItemEntry
                    { Data = itemData, ExtraData = new ExtraData { ItemCondition = healthData } }));
                Log.Debug("generated tempered versions of this item");
            });
        }

        private enum Size
        {
            Half = 1,
            Normal = 2,
            Double = 4
        }

        private enum DistributionFunction
        {
            Fall,
            Constant,
            Rise
        }

        private record QualityData(int Tier, Size SegmentSize, DistributionFunction Distribution)
        {
            internal IEnumerable<float> GetItemHealthValues()
            {
                var offset = (Tier - 1) * 3 * (int)SegmentSize;

                for (int segment = 0; segment < 3; segment++)
                {
                    var distributionMultiplier = (Distribution, segment) switch
                    {
                        (DistributionFunction.Constant, _) => 1,
                        (DistributionFunction.Fall, var x) => 3 - x,
                        (DistributionFunction.Rise, var x) => x + 1,
                        _ => throw new ArgumentException("distribution function unknown")
                    };

                    for (int index = 0; index < (int)SegmentSize; index++)
                    {
                        for (int dist = 0; dist < distributionMultiplier; dist++)
                        {
                            yield return 1.0f + (offset + (segment * (int)SegmentSize) + index) / 10f;
                        }
                    }
                }
            }
        }

        private static QualityData? ExtractQualityData(ILeveledItemGetter record)
        {
            var match = Pattern.Match(record.EditorID ?? "");

            if (!match.Success) return null;

            var tier = int.Parse(match.Groups["tier"].Value, CultureInfo.InvariantCulture);
            var size = match.Groups["size"].Value switch
            {
                "H" => Size.Half,
                "N" => Size.Normal,
                "D" => Size.Double,
                _ => throw new InvalidTemperingDataException(record.AsLinkGetter())
            };
            var distribution = match.Groups["distribution"].Value switch
            {
                "fall" => DistributionFunction.Fall,
                "const" => DistributionFunction.Constant,
                "rise" => DistributionFunction.Rise,
                _ => throw new InvalidTemperingDataException(record.AsLinkGetter())
            };

            return new QualityData(tier, size, distribution);
        }
    }
}