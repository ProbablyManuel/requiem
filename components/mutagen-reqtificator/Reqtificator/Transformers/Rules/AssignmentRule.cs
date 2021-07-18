using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using Hocon;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Records;
using Noggog;
using Reqtificator.Exceptions;

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
        public IImmutableSet<IAssignmentCondition<TMajorGetter>> Conditions { get; }

        public IImmutableSet<IFormLinkGetter<TAssign>> Assignments { get; }

        public IImmutableSet<AssignmentRule<TMajorGetter, TAssign>> SubNodes { get; }

        public string NodeName { get; }

        public AssignmentRule(IImmutableSet<IAssignmentCondition<TMajorGetter>> conditions,
            IImmutableSet<IFormLinkGetter<TAssign>> assignments,
            IImmutableSet<AssignmentRule<TMajorGetter, TAssign>> subNodes,
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

        public override bool Equals(object? obj)
        {
            return obj switch
            {
                AssignmentRule<TMajorGetter, TAssign> other => NodeName.Equals(other.NodeName, StringComparison.Ordinal)
                                                               && Conditions.SetEquals(other.Conditions)
                                                               && Assignments.SetEquals(other.Assignments)
                                                               && SubNodes.SetEquals(other.SubNodes),
                _ => false
            };
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int sum = 0;
                foreach (IAssignmentCondition<TMajorGetter> x in Conditions) sum += x.GetHashCode();

                int sum1 = 0;
                foreach (IFormLinkGetter<TAssign> x in Assignments) sum1 += x.GetHashCode();

                int sum2 = 0;
                foreach (AssignmentRule<TMajorGetter, TAssign> x in SubNodes) sum2 += x.GetHashCode();

                return HashCode.Combine(sum,
                    sum1, sum2, NodeName);
            }
        }

        public static ErrorOr<ImmutableList<AssignmentRule<TMajorGetter, TAssign>>> LoadFromConfigurationFile(
            Config config,
            Func<HoconField, IImmutableSet<IFormLinkGetter<TAssign>>?> assignmentExtractor,
            Func<HoconField, IAssignmentCondition<TMajorGetter>?> conditionExtractor
        )
        {
            AssignmentRule<TMajorGetter, TAssign> NodeExtractor(HoconField content)
            {
                IImmutableSet<IFormLinkGetter<TAssign>>? assignments = null;
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

            try
            {
                return config.Root.GetObject()
                    .Where(e => e.Key.StartsWith("feature_", StringComparison.InvariantCulture))
                    .Select(kv => NodeExtractor(kv.Value))
                    .ToImmutableList().AsSuccess();
            }
            catch (ReqtificatorException e)
            {
                return new Failed<ImmutableList<AssignmentRule<TMajorGetter, TAssign>>>(e);
            }
        }
    }
}