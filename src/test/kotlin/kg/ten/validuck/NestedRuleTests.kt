package kg.ten.validuck

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NestedRuleTests {

    class Person(
        val name: String,
        val company: Company
    )

    class Company(
        val address: String
    )

    class PersonValidator : Validator<Person>() {
        init {
            ruleFor(Person::company) {
                ruleFor(Company::address) {
                    must { it.isNotEmpty() }
                }
            }
        }
    }

    private lateinit var personValidator: PersonValidator

    @BeforeEach
    fun setUp() {
        personValidator = PersonValidator()
    }

    @Test
    fun `validate SHOULD generate errors of sub model WHEN conditions are not met for sub model`() {
        //arrange
        val person = Person(name = "Jack Black", company = Company(address = ""))

        //act
        val errors = personValidator.validate(person)

        //assert
        assertThat(errors, containsInAnyOrder(ValidationError(path = "company.address", message = "")))
    }
}
