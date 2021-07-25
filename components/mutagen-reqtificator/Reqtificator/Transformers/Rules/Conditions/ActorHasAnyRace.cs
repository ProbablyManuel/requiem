using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;

namespace Reqtificator.Transformers.Rules.Conditions
{
    internal class ActorHasAnyRace : IAssignmentCondition<INpcGetter>
    {
        private readonly IActorInheritanceGraphParser _inheritanceGraphParser;
        private readonly IImmutableSet<IFormLinkGetter<IRaceGetter>> _races;

        public ActorHasAnyRace(IImmutableSet<IFormLinkGetter<IRaceGetter>> races,
            IActorInheritanceGraphParser inheritanceGraphParserParser)
        {
            _inheritanceGraphParser = inheritanceGraphParserParser;
            _races = races;
        }

        public bool CheckRecord(INpcGetter record)
        {
            var possibleTemplates =
                _inheritanceGraphParser.FindAllTemplates(record, NpcConfiguration.TemplateFlag.Traits);

            return possibleTemplates.All(t => _races.Contains(t[NpcConfiguration.TemplateFlag.Traits].Race));
        }
    }
}