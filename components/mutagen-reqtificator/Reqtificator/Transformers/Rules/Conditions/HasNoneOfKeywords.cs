using System.Collections.Generic;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.Rules.Conditions
{
    public record HasNoneOfKeywords<TMajor>
        (IReadOnlySet<IFormLinkGetter<IKeywordGetter>> Keywords) : IAssignmentCondition<TMajor>
        where TMajor : IMajorRecordGetter, IKeywordedGetter
    {
        public bool CheckRecord(TMajor record) => Keywords.All(k => record.Keywords?.ContainsNot(k) ?? true);
    }
}