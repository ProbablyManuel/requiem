using System.Collections.Generic;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.Rules.Conditions
{
    public class HasNoneOfKeywords<TMajor> : IAssignmentCondition<TMajor>
        where TMajor : IMajorRecordGetter, IKeywordedGetter
    {
        public HasNoneOfKeywords(IReadOnlySet<IFormLinkGetter<IKeywordGetter>> keywords)
        {
            Keywords = keywords;
        }

        public IReadOnlySet<IFormLinkGetter<IKeywordGetter>> Keywords { get; }

        public bool CheckRecord(TMajor record) => Keywords.All(k => record.Keywords?.ContainsNot(k) ?? true);

        public override bool Equals(object? obj)
        {
            return obj switch
            {
                HasNoneOfKeywords<TMajor> other => Keywords.SetEquals(other.Keywords),
                _ => false
            };
        }

        public override int GetHashCode()
        {
            return 3 + Keywords.Select(x => x.GetHashCode()).Sum();
        }
    }
}