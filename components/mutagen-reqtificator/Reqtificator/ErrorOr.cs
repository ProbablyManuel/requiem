using System;
using Reqtificator.Exceptions;

namespace Reqtificator
{
    internal abstract record ErrorOr<T>
    {
        public abstract ErrorOr<T2> Map<T2>(Func<T, T2> func);

        public abstract ErrorOr<T2> FlatMap<T2>(Func<T, ErrorOr<T2>> func);

        public abstract ErrorOr<T> Recover(Func<ReqtificatorException, T> func);
    }

    internal sealed record Success<T>(T Value) : ErrorOr<T>
    {
        public override ErrorOr<T2> Map<T2>(Func<T, T2> func)
        {
            return new Success<T2>(func(Value));
        }

        public override ErrorOr<T2> FlatMap<T2>(Func<T, ErrorOr<T2>> func)
        {
            return func(Value);
        }

        public override ErrorOr<T> Recover(Func<ReqtificatorException, T> func)
        {
            return this;
        }
    }

    internal sealed record Failed<T>(ReqtificatorException Error) : ErrorOr<T>
    {
        public override ErrorOr<T2> Map<T2>(Func<T, T2> func)
        {
            return new Failed<T2>(Error);
        }

        public override ErrorOr<T2> FlatMap<T2>(Func<T, ErrorOr<T2>> func)
        {
            return new Failed<T2>(Error);
        }

        public override ErrorOr<T> Recover(Func<ReqtificatorException, T> func)
        {
            return new Success<T>(func(Error));
        }
    }

    internal static class ConversionExtensions
    {
        public static ErrorOr<T> AsSuccess<T>(this T value)
        {
            return new Success<T>(value);
        }
    }
}