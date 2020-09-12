package skyrim.requiem.fptools

sealed class Either<out A, out B> {
    fun <C> map(f: (B) -> C): Either<A, C> {
        return when (this) {
            is Left -> Left(this.left)
            is Right -> Right(f(this.right))
        }
    }

    fun <C> leftMap(f: (A) -> C): Either<C, B> {
        return when (this) {
            is Right -> Right(this.right)
            is Left -> Left(f(this.left))
        }
    }
}

data class Left<A, B>(val left: A) : Either<A, B>()

data class Right<A, B>(val right: B) : Either<A, B>()

fun <A : A1, B, A1, B1> Either<A, B>.flatMap(f: (B) -> Either<A1, B1>): Either<A1, B1> {
    return when (this) {
        is Left -> Left(this.left)
        is Right -> f(this.right)
    }
}

fun <A, B : B1, A1, B1> Either<A, B>.leftFlatMap(f: (A) -> Either<A1, B1>): Either<A1, B1> {
    return when (this) {
        is Right -> Right(this.right)
        is Left -> f(this.left)
    }
}