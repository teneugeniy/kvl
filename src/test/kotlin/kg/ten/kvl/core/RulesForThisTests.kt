package kg.ten.kvl.core

import kg.ten.kvl.core.fluent.must
import kg.ten.kvl.core.fluent.nocontext.NoContextKvlValidator
import kg.ten.kvl.core.fluent.withMessage
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RulesForThisTests {

    private class Order(
        val weightOfMeal: Int,
        val weightOfBeverages: Int
    )

    private class OrderValidator : NoContextKvlValidator<Order>() {
        init {
            rulesForThis {
                must { it.weightOfMeal + it.weightOfBeverages < 10 }.withMessage { "Total weight of the order should not exceed 10 kilos" }
            }
        }
    }

    private lateinit var orderValidator: OrderValidator

    @BeforeEach
    fun setUp() {
        orderValidator = OrderValidator()
    }

    @Test
    fun `validate SHOULD pass WHEN rulesForThis conditions are met`() {
        // arrange
        val order = Order(weightOfBeverages = 3, weightOfMeal = 3)

        // act
        val errors = orderValidator.validate(order)

        // assert
        assertThat(errors, empty())
    }

    @Test
    fun `validate SHOULD generate errors with empty path WHEN rulesForThis fail to pass`() {
        // arrange
        val order = Order(weightOfBeverages = 3, weightOfMeal = 10)

        // act
        val errors = orderValidator.validate(order)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(
                ValidationError(path = "", message = "Total weight of the order should not exceed 10 kilos")
            )
        )
    }
}
