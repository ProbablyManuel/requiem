using System;
using System.Collections.Immutable;
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
        public HasAnyKeyword(IImmutableSet<IFormLinkGetter<IKeywordGetter>> keywords)
        {
            if (keywords.Count == 0)
            {
                throw new ArgumentException("keywords must be non-empty", nameof(keywords));
            }

            Keywords = keywords;
        }

        public IImmutableSet<IFormLinkGetter<IKeywordGetter>> Keywords { get; }

        public bool CheckRecord(TMajor record)
        {
            return Keywords.Any(k => record.Keywords?.Contains(k) ?? false);
        }

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
            unchecked
            {
                int sum = 0;
                foreach (var x in Keywords)
                {
                    sum += x.GetHashCode();
                }

                return 2 + sum;
            }
        }
    }
}