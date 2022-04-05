package dev.olshevski.udu.data.paging.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PageRangeTest : FunSpec({

    context("allowed values") {
        test("end may be equal than start") {
            PageRange(0, 0)
        }
        test("end should be greater than start") {
            shouldThrow<IllegalArgumentException> {
                PageRange(1, 0)
            }
        }
    }

    context("intersection") {

        test("first and second range are the same") {
            val first = PageRange(1, 2)
            val second = PageRange(1, 2)
            val result = first intersect second
            result shouldBe PageRange(1, 2)
        }

        test("first range inside second") {
            val first = PageRange(1, 2)
            val second = PageRange(0, 3)
            val result = first intersect second
            result shouldBe PageRange(1, 2)
        }

        test("second range inside first") {
            val first = PageRange(0, 3)
            val second = PageRange(1, 2)
            val result = first intersect second
            result shouldBe PageRange(1, 2)
        }

        context("more than one point intersection") {
            test("first range starts before second") {
                val first = PageRange(0, 2)
                val second = PageRange(1, 3)
                val result = first intersect second
                result shouldBe PageRange(
                    1,
                    2
                )
            }

            test("second range starts before first") {
                val first = PageRange(1, 3)
                val second = PageRange(0, 2)
                val result = first intersect second
                result shouldBe PageRange(
                    1,
                    2
                )
            }
        }

        context("one point intersection") {
            test("first range starts before second") {
                val first = PageRange(0, 1)
                val second = PageRange(1, 2)
                val result = first intersect second
                result shouldBe PageRange(
                    1,
                    1
                )
            }

            test("second range starts before first") {
                val first = PageRange(1, 2)
                val second = PageRange(0, 1)
                val result = first intersect second
                result shouldBe PageRange(
                    1,
                    1
                )
            }
        }

        context("no point intersection") {
            test("first range starts before second") {
                val first = PageRange(0, 1)
                val second = PageRange(2, 3)
                val result = first intersect second
                result shouldBe null
            }

            test("second range starts before first") {
                val first = PageRange(2, 3)
                val second = PageRange(0, 1)
                val result = first intersect second
                result shouldBe null
            }
        }
    }

})