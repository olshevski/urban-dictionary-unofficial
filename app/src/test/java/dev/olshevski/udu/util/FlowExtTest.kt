package dev.olshevski.udu.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf

class FlowExtTests : FunSpec({

    context("assertCollectedOnlyOnce") {

        val flow = flowOf(Unit).assertCollectedOnlyOnce()

        coroutineScope { flow.collect() }

        test("should throw IllegalStateException on second collect") {
            shouldThrow<IllegalStateException> {
                flow.collect()
            }
        }
    }

})