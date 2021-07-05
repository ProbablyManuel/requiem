using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;

namespace Reqtificator.Transformers.Rules
{
    public record Assignment<TAssign>(IFormLinkGetter<TAssign> ToAssign, string SourceRule)
        where TAssign : class, IMajorRecordGetter;

    public interface IAssignmentCondition<TMajorGetter> where TMajorGetter : IMajorRecordGetter
    {
        public bool CheckRecord(TMajorGetter record);
    }

    internal abstract class AssignmentRule<TMajorGetter, TAssign> where TMajorGetter : IMajorRecordGetter
        where TAssign : class, IMajorRecordGetter
    {
        public IReadOnlySet<IAssignmentCondition<TMajorGetter>> Conditions { get; }

        public IReadOnlySet<IFormLinkGetter<TAssign>> Assignments { get; }

        public IReadOnlySet<AssignmentRule<TMajorGetter, TAssign>> SubNodes { get; }

        public string NodeName { get; }

        protected AssignmentRule(IReadOnlySet<IAssignmentCondition<TMajorGetter>> conditions,
            IReadOnlySet<IFormLinkGetter<TAssign>> assignments, IReadOnlySet<AssignmentRule<TMajorGetter, TAssign>> subNodes,
            string nodeName)
        {
            Conditions = conditions;
            Assignments = assignments;
            SubNodes = subNodes;
            NodeName = nodeName;
        }

        public IEnumerable<Assignment<TAssign>> GetAssignments(TMajorGetter record)
        {
            return GetAssignmentsWithSource(record, "");
        }

        private IEnumerable<Assignment<TAssign>> GetAssignmentsWithSource(TMajorGetter record, string parentRule)
        {
            if (Conditions.All(c => c.CheckRecord(record)))
            {
                var fullNodeName = string.IsNullOrEmpty(parentRule) ? NodeName : $"{parentRule}.{NodeName}";
                var assignments = Assignments.Select(a => new Assignment<TAssign>(a, fullNodeName));
                var subAssignments = SubNodes.SelectMany(n => n.GetAssignmentsWithSource(record, fullNodeName));
                return assignments.ToImmutableList().AddRange(subAssignments);
            }

            return ImmutableList<Assignment<TAssign>>.Empty;
        }
    }
}