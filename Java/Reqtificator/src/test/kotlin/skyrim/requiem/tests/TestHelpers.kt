package skyrim.requiem.tests

import io.kotlintest.Spec
import io.kotlintest.fail
import io.kotlintest.specs.WordSpec
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import skyrim.requiem.fptools.Either
import skyrim.requiem.fptools.Left
import skyrim.requiem.fptools.Right

fun <A, B> Either<A, B>.left(): A {
    return when (this) {
        is Left -> this.left
        is Right -> fail("expected a Left")
    }
}

fun <A, B> Either<A, B>.right(): B {
    return when (this) {
        is Right -> this.right
        is Left -> fail("expected a Right")
    }
}

abstract class WordSpecWithStaticMockks : WordSpec() {
    open val staticMockks: Array<String> = arrayOf()
    open val objectMockks: Array<Any> = arrayOf()

    override fun beforeSpec(spec: Spec) {
        if (staticMockks.isNotEmpty()) mockkStatic(*staticMockks)
        if (objectMockks.isNotEmpty()) mockkObject(*objectMockks)
        super.beforeSpec(spec)
    }
    override fun afterSpec(spec: Spec) {
        if (staticMockks.isNotEmpty()) unmockkStatic(*staticMockks)
        if (objectMockks.isNotEmpty()) unmockkObject(*objectMockks)
        super.beforeSpec(spec)
    }
}