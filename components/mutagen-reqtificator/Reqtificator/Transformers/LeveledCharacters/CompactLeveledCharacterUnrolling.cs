using System.Collections.Immutable;
using System.Linq;
using System.Text.RegularExpressions;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;

namespace Reqtificator.Transformers.LeveledCharacters
{
    internal class CompactLeveledCharacterUnrolling : TransformerV2<LeveledNpc, ILeveledNpcGetter>
    {
        private readonly IImmutableSet<ModKey> _registeredMods;

        private static readonly Regex Pattern = new Regex("^[a-zA-Z0-9]+_CLChar_", RegexOptions.IgnoreCase);

        public CompactLeveledCharacterUnrolling(IImmutableSet<ModKey> modsRegisteredForFeature)
        {
            _registeredMods = modsRegisteredForFeature;
        }

        public override TransformationResult<LeveledNpc, ILeveledNpcGetter> Process(
            TransformationResult<LeveledNpc, ILeveledNpcGetter> input)
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