package kg.ten.validuck

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CascadeRulesTests {

    class Person(
        val name: String
    )

    class PersonValidator : Validator<Person>() {
        init {
            ruleFor(Person::name) {
                must { it.contains("Jack") }.withMessage { "should contain Jack" }
                    .and()
                    .must { it.contains("Black") }.withMessage { "should contain Black" }
            }
        }
    }

    private lateinit var personValidator: PersonValidator

    @BeforeEach
    fun setUp() {
        personValidator = PersonValidator()
    }

    @Test
    fun `validate SHOULD return only the first error WHEN the first check fails`() {
        //arrange
        val person = Person("Black")

        //act
        val errors = personValidator.validate(person)

        //assert
        assertThat(
            errors, containsInAnyOrder(
                ValidationError(path = "name", message = "should contain Jack")
            )
        )
    }

    @Test
    fun `validate SHOULD return only the last error WHEN the last check fails`() {
        //arrange
        val person = Person("Jack White")

        //act
        val errors = personValidator.validate(person)

        //assert
        assertThat(
            errors, containsInAnyOrder(
                ValidationError(path = "name", message = "should contain Black")
            )
        )
    }
}
