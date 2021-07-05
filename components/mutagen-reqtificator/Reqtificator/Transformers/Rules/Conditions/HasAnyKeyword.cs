using System.Collections.Generic;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Aspects;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.Rules.Conditions
{
    internal record HasAnyKeyword<TMajor>
        (IReadOnlySet<IFormLinkGetter<IKeywordGetter>> Keywords) : IAssignmentCondition<TMajor>
        where TMajor : IMajorRecordGetter, IKeywordedGetter
    {
        public bool CheckRecord(TMajor record)
        {
            throw new System.NotImplementedException();
        }
    }
}