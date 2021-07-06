using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Hocon;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;
using Noggog;

namespace Reqtificator.Transformers.Rules
{
    public record Assignment<TAssign>(IFormLinkGetter<TAssign> ToAssign, string SourceRule)
        where TAssign : class, IMajorRecordGetter;

    public interface IAssignmentCondition<TMajorGetter> where TMajorGetter : IMajorRecordGetter
    {
        public bool CheckRecord(TMajorGetter record);
    }

    internal sealed class AssignmentRule<TMajorGetter, TAssign> where TMajorGetter : IMajorRecordGetter
        where TAssign : class, IMajorRecordGetter
    {
        public IReadOnlySet<IAssignmentCondition<TMajorGetter>> Conditions { get; }

        public IReadOnlySet<IFormLinkGetter<TAssign>> Assignments { get; }

        public IReadOnlySet<AssignmentRule<TMajorGetter, TAssign>> SubNodes { get; }

        public string NodeName { get; }

        public AssignmentRule(IReadOnlySet<IAssignmentCondition<TMajorGetter>> conditions,
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

        public static IImmutableList<AssignmentRule<TMajorGetter, TAssign>> LoadFromConfigurationFile(Config config,
            Func<HoconField, IReadOnlySet<IFormLinkGetter<TAssign>>?> assignmentExtractor,
            Func<HoconField, IAssignmentCondition<TMajorGetter>?> conditionExtractor
            )
        {
            AssignmentRule<TMajorGetter, TAssign> NodeExtractor(HoconField content)
            {
                IReadOnlySet<IFormLinkGetter<TAssign>>? assignments = null;
                var conditions = ImmutableHashSet<IAssignmentCondition<TMajorGetter>>.Empty;
                var subNodes = ImmutableHashSet<AssignmentRule<TMajorGetter, TAssign>>.Empty;

                content.GetObject().ForEach(e =>
                {
                    if (assignmentExtractor(e.Value) is var maybeAssignment && maybeAssignment is not null)
                        assignments = maybeAssignment;
                    else if (conditionExtractor(e.Value) is var maybeCondition && maybeCondition is not null)
                        conditions = conditions.Add(maybeCondition);
                    else
                        subNodes = subNodes.Add(NodeExtractor(e.Value));
                });
                return new AssignmentRule<TMajorGetter, TAssign>(
                    assignments: assignments ?? ImmutableHashSet<IFormLinkGetter<TAssign>>.Empty,
                    conditions: conditions,
                    nodeName: content.Key,
                    subNodes: subNodes
                );
            }

            return config.Root.GetObject().Where(e => e.Key.StartsWith("feature_"))
                .Select(kv => NodeExtractor(kv.Value))
                .ToImmutableList();
        }
    }
}