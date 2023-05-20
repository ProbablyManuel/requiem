using System;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.Rules.Conditions
{
    internal class HasAllKeywords<TMajor> : IAssignmentCondition<TMajor>
        where TMajor : IMajorRecordGetter, IKeywordedGetter
    {
        public HasAllKeywords(IImmutableSet<IFormLinkGetter<IKeywordGetter>> keywords)
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
            return Keywords.All(k => record.Keywords?.Contains(k) ?? false);
        }

        public override string ToString()
        {
            if (Keywords.Count == 0)
            {
                return "HasAllKeywords [ ]";
            }

            return $"HasAllKeywords [{Keywords.Select(x => x.FormKey.ToString()).Aggregate((x, y) => $"{x}, {y}")}] ";
        }

        public override bool Equals(object? obj)
        {
            return obj switch
            {
                HasAllKeywords<TMajor> other => Keywords.SetEquals(other.Keywords),
                _ => false
            };
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int sum = 0;
                foreach (IFormLinkGetter<IKeywordGetter> x in Keywords)
                {
                    sum += x.GetHashCode();
                }

                return 1 + sum;
            }
        }
    }
}