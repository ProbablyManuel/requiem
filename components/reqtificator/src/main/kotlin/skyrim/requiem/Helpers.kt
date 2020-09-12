package skyrim.requiem

fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C {
    return { x -> f(g(x)) }
}

fun <A, B, C> Function1<A, B>.andThen(f: (B) -> C): (A) -> C {
    return { x -> f(this(x)) }
}
