package kg.ten.kvl.core

import java.math.BigDecimal

class ContextValidatorTests {

    class Order(
        val total: BigDecimal
    )

    class Context(
        val limitedMode: Boolean
    )

    class OrderValidator : KvlValidator<Order>()
}
