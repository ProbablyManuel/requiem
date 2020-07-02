package skyrim.requiem.fptools

sealed class Option<A> {
    fun <B> map(f: (A) -> B): Option<B> {
        return when (this) {
            is Some -> Some(f(value))
            is None -> None<B>()
        }
    }

    fun <B> flatMap(f: (A) -> Option<B>): Option<B> {
        return when (this) {
            is Some -> f(value)
            is None -> None<B>()
        }
    }

    fun orElse(default: () -> Option<A>): Option<A> {
        return when (this) {
            is Some -> this
            is None -> default()
        }
    }

    fun getOrElse(default: () -> A): A {
        return when (this) {
            is Some -> value
            is None -> default()
        }
    }

    fun empty(): Boolean = this is None

    fun nonEmpty(): Boolean = this is Some

    companion object {
        operator fun <A> invoke(value: A?): Option<A> = if (value != null) Some(value) else None()
    }
}

data class Some<A>(val value: A) : Option<A>()

class None<A> : Option<A>()