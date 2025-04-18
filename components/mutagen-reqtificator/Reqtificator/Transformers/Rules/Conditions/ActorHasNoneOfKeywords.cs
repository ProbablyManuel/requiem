﻿using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.Rules.Conditions
{
    internal class ActorHasNoneOfKeywords : IAssignmentCondition<INpcGetter>
    {
        private readonly IActorInheritanceGraphParser _inheritanceGraphParser;
        private readonly IImmutableSet<IFormLinkGetter<IKeywordGetter>> _keywords;

        public ActorHasNoneOfKeywords(IImmutableSet<IFormLinkGetter<IKeywordGetter>> keywords,
            IActorInheritanceGraphParser inheritanceGraphParserParser)
        {
            _inheritanceGraphParser = inheritanceGraphParserParser;
            _keywords = keywords;
        }

        public bool CheckRecord(INpcGetter record)
        {
            var possibleTemplates = _inheritanceGraphParser.FindAllTemplates(record,
                NpcConfiguration.TemplateFlag.Keywords, NpcConfiguration.TemplateFlag.Traits);

            return possibleTemplates.All(template =>
            {
                var actorKeywords = template[NpcConfiguration.TemplateFlag.Keywords].Keywords ??
                                    [];
                var race = template[NpcConfiguration.TemplateFlag.Traits].Race.Resolve(_inheritanceGraphParser.Cache);
                var raceKeywords = race.Keywords ?? [];

                return _keywords.Intersect(raceKeywords.Union(actorKeywords)).Count == 0;
            });
        }
    }
}