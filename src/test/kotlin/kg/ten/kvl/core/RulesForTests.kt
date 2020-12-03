package kg.ten.kvl.core

import kg.ten.kvl.core.fluent.must
import kg.ten.kvl.core.fluent.nocontext.NoContextKvlValidator
import kg.ten.kvl.core.fluent.withMessage
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RulesForTests {

    class Person(
        val name: String
    )

    class PersonValidator : NoContextKvlValidator<Person>() {
        init {
            rulesFor(Person::name) {
                must { it.length > 2 }.withMessage("min" to 2) { "Must be at least %(min) symbols. Current value: $it" }
            }
        }
    }

    private lateinit var personValidator: PersonValidator

    @BeforeEach
    fun setUp() {
        personValidator = PersonValidator()
    }

    @Test
    fun `validate SHOULD not generate errors WHEN conditions are met`() {
        // arrange
        val person = Person("Jack Black")

        // act
        val errors = personValidator.validate(person)

        // assert
        assertThat(errors, empty())
    }

    @Test
    fun `validate SHOULD generate errors WHEN conditions are not met`() {
        // arrange
        val person = Person("")

        // act
        val errors = personValidator.validate(person)

        // assert
        assertThat(errors, `is`(not(empty())))
    }

    @Test
    fun `validate SHOULD generate message`() {
        // arrange
        val person = Person("1")

        // act
        val errors = personValidator.validate(person)

        // assert
        assertThat(errors.first().message, `is`("Must be at least 2 symbols. Current value: 1"))
    }

    @Test
    fun `validate SHOULD generate path`() {
        // arrange
        val person = Person("1")

        // act
        val errors = personValidator.validate(person)

        // assert
        assertThat(errors.first().pathValue, `is`("name"))
    }
}
