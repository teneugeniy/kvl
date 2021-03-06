package kg.ten.kvl.core

import kg.ten.kvl.core.fluent.must
import kg.ten.kvl.core.fluent.nocontext.NoContextKvlValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RulesForEachTests {

    class Order(
        val itemsWeights: List<Int>
    )

    class OrderValidator : NoContextKvlValidator<Order>() {
        init {
            rulesForEach(Order::itemsWeights) {
                must { it < 10 }
            }
        }
    }

    private lateinit var orderValidator: OrderValidator

    @BeforeEach
    fun setUp() {
        orderValidator = OrderValidator()
    }

    @Test
    fun `validate SHOULD pass WHEN all items meet the requirements`() {
        // arrange
        val order = Order(listOf(1, 2, 3))

        // act
        val errors = orderValidator.validate(order)

        // assert
        assertThat(errors, empty())
    }

    @Test
    fun `validate SHOULD generate errors with indices WHEN rules fail`() {
        // arrange
        val order = Order(listOf(20))

        // act
        val errors = orderValidator.validate(order)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(ValidationError(path = "itemsWeights[0]", message = ""))
        )
    }

    @Test
    fun `validate SHOULD generate errors only for the failed items in collection`() {
        // arrange
        val order = Order(listOf(1, 20, 2, 30))

        // act
        val errors = orderValidator.validate(order)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(
                ValidationError(path = "itemsWeights[1]", message = ""),
                ValidationError(path = "itemsWeights[3]", message = "")
            )
        )
    }
}
