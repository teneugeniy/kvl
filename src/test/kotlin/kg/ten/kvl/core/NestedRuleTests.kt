package kg.ten.kvl.core

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NestedRuleTests {

    class Person(
        val name: String,
        val company: Company,
        val prevCompany: Company?
    )

    class Company(
        val address: String
    )

    class PersonValidator : KvlValidator<Person>() {
        init {
            rulesFor(Person::company) {
                rulesFor(Company::address) {
                    must { it.isNotEmpty() }
                }
            }

            rulesFor(Person::prevCompany) {
                rulesFor(Company::address) {
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
        // arrange
        val person = Person(name = "Jack Black", company = Company(address = ""), prevCompany = null)

        // act
        val errors = personValidator.validate(person)

        // assert
        MatcherAssert.assertThat(
            errors,
            Matchers.containsInAnyOrder(ValidationError(path = "company.address", message = ""))
        )
    }

    @Test
    fun `validate SHOULD find errors WHEN nested rules check nullable object's property`() {
        // arrange
        val person = Person(name = "Jack Black", company = Company(address = "Finland"), prevCompany = Company(address = ""))

        // act
        val errors = personValidator.validate(person)

        // assert
        MatcherAssert.assertThat(
            errors,
            Matchers.containsInAnyOrder(ValidationError(path = "prevCompany.address", message = ""))
        )
    }

    @Test
    fun `validate SHOULD not fail WHEN a nested rule validates against null object's property`() {
        // arrange
        val person = Person(name = "Jack Black", company = Company(address = "Finland"), prevCompany = null)

        // act
        val errors = personValidator.validate(person)

        // assert
        MatcherAssert.assertThat(errors, Matchers.empty())
    }
}
