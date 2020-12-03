package kg.ten.kvl.core

import kg.ten.kvl.core.fluent.must
import kg.ten.kvl.core.fluent.nocontext.NoContextKvlValidator
import kg.ten.kvl.core.fluent.withMessage
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RulesForMapTests {

    class Order(
        val items: Map<String, OrderItem> = emptyMap(),
        val deliveries: Map<Address, DeliveryComments> = emptyMap()
    )

    class OrderItem(
        val isExpress: Boolean,
        val quantity: Int
    )

    data class Address(
        val value: String,
    ) {
        override fun toString() = value
    }

    data class DeliveryComments(
        val warmedUp: Boolean
    )

    class OrderValidator : NoContextKvlValidator<Order>() {
        init {
            rulesForMap(Order::items) {
                must { (itemCode, _) -> itemCode != "tobacco" }.withMessage { "Tobacco is not allowed" }
                must { (_, item) -> item.isExpress.not() || item.quantity == 1 }.withMessage { "Express items should be in range" }

                rulesFor(OrderItem::quantity) {
                    must { it > 0 }
                }
            }

            rulesForMap(Order::deliveries) {
                must { (address, delivery) -> delivery.warmedUp.not() || address.value != "SouthPole" }
            }
        }
    }

    private lateinit var orderValidator: OrderValidator

    @BeforeEach
    fun setUp() {
        orderValidator = OrderValidator()
    }

    @Test
    fun `validate SHOULD pass WHEN all items in map meet the requirements`() {
        // arrange
        val order = Order(items = mapOf("soap" to OrderItem(isExpress = false, quantity = 1)))

        // act
        val errors = orderValidator.validate(order)

        // assert
        assertThat(errors, empty())
    }

    @Test
    fun `validate SHOULD generate errors with key as path WHEN map item values fail the checks`() {
        // arrange
        val order = Order(items = mapOf("soap" to OrderItem(isExpress = true, quantity = 10)))

        // act
        val errors = orderValidator.validate(order)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(
                ValidationError(path = "items.soap", message = "Express items should be in range")
            )
        )
    }

    @Test
    fun `validate SHOULD generate errors with key as path WHEN map item keys fail the checks`() {
        // arrange
        val order = Order(items = mapOf("tobacco" to OrderItem(isExpress = false, quantity = 1)))

        // act
        val errors = orderValidator.validate(order)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(
                ValidationError(path = "items.tobacco", message = "Tobacco is not allowed")
            )
        )
    }

    @Test
    fun `validate SHOULD generate errors WHEN rulesFor are violated`() {
        // arrange
        val order = Order(items = mapOf("water" to OrderItem(isExpress = false, quantity = -1)))

        // act
        val errors = orderValidator.validate(order)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(
                ValidationError(path = "items.water.quantity", message = "")
            )
        )
    }

    @Test
    fun `validate SHOULD generate errors WHEN rulesForKey are violated`() {
        // arrange
        val order = Order(items = mapOf("water" to OrderItem(isExpress = false, quantity = -1)))

        // act
        val errors = orderValidator.validate(order)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(
                ValidationError(path = "items.water.quantity", message = "")
            )
        )
    }

    @Test
    fun `validate SHOULD use map key toString result as path`() {
        // arrange
        val order = Order(deliveries = mapOf(Address("SouthPole") to DeliveryComments(warmedUp = true)))

        // act
        val errors = orderValidator.validate(order)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(
                ValidationError(path = "deliveries.SouthPole", message = "")
            )
        )
    }
}
