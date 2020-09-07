package kg.ten.validuck

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MultipleRulesTests {

    class Person(
        val name: String
    )

    class PersonValidator : Validator<Person>() {
        init {
            ruleFor(Person::name) {
                must { it.contains("Jack").not() }.withMessage { "message one" }
                must { it.contains("Sir") }.withMessage { "message two" }
            }
        }
    }

    private lateinit var personValidator: PersonValidator

    @BeforeEach
    fun setUp() {
        personValidator = PersonValidator()
    }

    @Test
    fun `validate SHOULD generate errors for all failed rules`() {
        //arrange
        val person = Person("Jack Black")

        //act
        val errors = personValidator.validate(person)

        //assert
        assertThat(
            errors, containsInAnyOrder(
                ValidationError(path = "name", message = "message one"),
                ValidationError(path = "name", message = "message two")
            )
        )
    }
}
