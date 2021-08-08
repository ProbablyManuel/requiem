using System.Collections.Immutable;
using System.Linq;
using System.Text.RegularExpressions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;

namespace Reqtificator.Transformers.LeveledItems
{
    internal class CompactLeveledItemUnrolling : TransformerV2<LeveledItem, ILeveledItemGetter>
    {
        private readonly IImmutableSet<ModKey> _registeredMods;

        private static readonly Regex Pattern = new Regex("^[a-zA-Z0-9]+_CLI_", RegexOptions.IgnoreCase);

        public CompactLeveledItemUnrolling(IImmutableSet<ModKey> modsRegisteredForFeature)
        {
            _registeredMods = modsRegisteredForFeature;
        }

        public override TransformationResult<LeveledItem, ILeveledItemGetter> Process(
            TransformationResult<LeveledItem, ILeveledItemGetter> input)
        {
            if (input.Record().EditorID is null || !Pattern.IsMatch(input.Record().EditorID!) ||
                _registeredMods.ContainsNot(input.Record().FormKey.ModKey) ||
                input.Record().Entries is null) return input;

            return input.Modify(record =>
            {
                var newContent = record.Entries!
                    .Where(e => e.Data is not null)
                    .SelectMany(e =>
                    {
                        var newEntry = e.DeepCopy();
                        newEntry.Data!.Count = 1;
                        return Enumerable.Repeat(newEntry, e.Data!.Count);
                    });
                record.Entries = newContent.ToExtendedList();
            });
        }
    }
}