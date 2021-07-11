using System;
using System.IO;
using System.Linq;
using FluentAssertions;
using FluentAssertions.Execution;
using FluentAssertions.Primitives;
using Reqtificator;
using Reqtificator.Exceptions;

namespace ReqtificatorTest
{
    internal static class ErrorOrExtensions
    {
        public static ErrorOrAssertions<T> Should<T>(this ErrorOr<T> instance)
        {
            return new ErrorOrAssertions<T>(instance);
        }
    }

    internal class ErrorOrAssertions<T> :
        ReferenceTypeAssertions<ErrorOr<T>, ErrorOrAssertions<T>>
    {
        public ErrorOrAssertions(ErrorOr<T> instance)
        {
            Subject = instance;
        }

        protected override string Identifier => "directory";

        public AndConstraint<ErrorOrAssertions<T>> BeASuccess(Action<T> assertions, string because = "",
            params object[] becauseArgs)
        {
            Execute.Assertion
                .BecauseOf(because, becauseArgs)
                .ForCondition(Subject is Success<T>)
                .FailWith("Expected a 'Success', but got a 'Failed'")
                .Then
                .Given(() => ((Success<T>)Subject).Value)
                .ForCondition(r => { assertions(r); return true; })
                .FailWith("weird, you should get the inner assertion failure.");

            return new AndConstraint<ErrorOrAssertions<T>>(this);
        }

        public AndConstraint<ErrorOrAssertions<T>> BeAFailed(Action<ReqtificatorException> assertions, string because = "",
            params object[] becauseArgs)
        {
            Execute.Assertion
                .BecauseOf(because, becauseArgs)
                .ForCondition(Subject is Failed<T>)
                .FailWith("Expected a 'Failed', but got a 'Success'")
                .Then
                .Given(() => ((Failed<T>)Subject).Error)
                .ForCondition(e => { assertions(e); return true; })
                .FailWith("weird, you should get the inner assertion failure.");

            return new AndConstraint<ErrorOrAssertions<T>>(this);
        }
    }
}