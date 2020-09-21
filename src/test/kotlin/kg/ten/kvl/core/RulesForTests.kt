package kg.ten.kvl.core

import org.hamcrest.MatcherAssert
import org.hamcrest.collection.IsEmptyCollection
import org.hamcrest.core.Is
import org.hamcrest.core.IsNot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RulesForTests {

    class Person(
        val name: String
    )

    class PersonValidator : KvlValidator<Person>() {
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
        MatcherAssert.assertThat(errors, IsEmptyCollection.empty())
    }

    @Test
    fun `validate SHOULD generate errors WHEN conditions are not met`() {
        // arrange
        val person = Person("")

        // act
        val errors = personValidator.validate(person)

        // assert
        MatcherAssert.assertThat(errors, Is.`is`(IsNot.not(IsEmptyCollection.empty())))
    }

    @Test
    fun `validate SHOULD generate message`() {
        // arrange
        val person = Person("1")

        // act
        val errors = personValidator.validate(person)

        // assert
        MatcherAssert.assertThat(errors.first().message, Is.`is`("Must be at least 2 symbols. Current value: 1"))
    }

    @Test
    fun `validate SHOULD generate path`() {
        // arrange
        val person = Person("1")

        // act
        val errors = personValidator.validate(person)

        // assert
        MatcherAssert.assertThat(errors.first().pathValue, Is.`is`("name"))
    }
}
