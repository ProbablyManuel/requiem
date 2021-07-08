using System.Collections.Generic;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.Rules.Conditions
{
    internal class HasAnyKeyword<TMajor> : IAssignmentCondition<TMajor>
        where TMajor : IMajorRecordGetter, IKeywordedGetter
    {
        public HasAnyKeyword(IReadOnlySet<IFormLinkGetter<IKeywordGetter>> keywords)
        {
            Keywords = keywords;
        }

        public IReadOnlySet<IFormLinkGetter<IKeywordGetter>> Keywords { get; }

        public bool CheckRecord(TMajor record) => Keywords.Any(k => record.Keywords?.Contains(k) ?? false);

        public override bool Equals(object? obj)
        {
            return obj switch
            {
                HasAnyKeyword<TMajor> other => Keywords.SetEquals(other.Keywords),
                _ => false
            };
        }

        public override int GetHashCode()
        {
            return 2 + Keywords.Select(x => x.GetHashCode()).Sum();
        }
    }
}