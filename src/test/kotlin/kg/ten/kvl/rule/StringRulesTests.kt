package kg.ten.kvl.rule

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.fluent.nocontext.NoContextKvlValidator
import kg.ten.kvl.rules.isEmail
import kg.ten.kvl.rules.isMax
import kg.ten.kvl.rules.isMin
import kg.ten.kvl.rules.isNotEmpty
import kg.ten.kvl.rules.pattern
import kg.ten.kvl.rules.size
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test

class StringRulesTests {

    class Company(
        val name: String
    )

    @Test
    fun `isNotEmpty SHOULD generate error WHEN string is empty`() {
        // arrange
        class CompanyValidator : NoContextKvlValidator<Company>() {
            init {
                rulesFor(Company::name) { isNotEmpty() }
            }
        }
        val company = Company(name = "")

        // act
        val errors = CompanyValidator().validate(company)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(ValidationError(path = "name", message = "must not be empty"))
        )
    }

    @Test
    fun `isMin SHOULD generate error WHEN string shorter than the specified value`() {
        // arrange
        class CompanyValidator : NoContextKvlValidator<Company>() {
            init {
                rulesFor(Company::name) { isMin(10) }
            }
        }
        val company = Company(name = "12345")

        // act
        val errors = CompanyValidator().validate(company)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(ValidationError(path = "name", message = "must not be shorter than 10"))
        )
    }

    @Test
    fun `isMax SHOULD generate error WHEN string longer than the specified value`() {
        // arrange
        class CompanyValidator : NoContextKvlValidator<Company>() {
            init {
                rulesFor(Company::name) { isMax(3) }
            }
        }
        val company = Company(name = "12345")

        // act
        val errors = CompanyValidator().validate(company)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(ValidationError(path = "name", message = "must not be longer than 3"))
        )
    }

    @Test
    fun `size SHOULD generate error WHEN string shorter than the specified value`() {
        // arrange
        class CompanyValidator : NoContextKvlValidator<Company>() {
            init {
                rulesFor(Company::name) { size(min = 3, max = 5) }
            }
        }
        val company = Company(name = "12")

        // act
        val errors = CompanyValidator().validate(company)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(ValidationError(path = "name", message = "must be between 3 and 5"))
        )
    }

    @Test
    fun `size SHOULD generate error WHEN string longer than the specified value`() {
        // arrange
        class CompanyValidator : NoContextKvlValidator<Company>() {
            init {
                rulesFor(Company::name) { size(min = 3, max = 5) }
            }
        }
        val company = Company(name = "12345678")

        // act
        val errors = CompanyValidator().validate(company)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(ValidationError(path = "name", message = "must be between 3 and 5"))
        )
    }

    @Test
    fun `isEmail SHOULD generate error WHEN the the text is not an email address`() {
        // arrange
        class CompanyValidator : NoContextKvlValidator<Company>() {
            init {
                rulesFor(Company::name) { isEmail() }
            }
        }
        val company = Company(name = "address.domain.com")

        // act
        val errors = CompanyValidator().validate(company)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(ValidationError(path = "name", message = "must be an email address"))
        )
    }

    @Test
    fun `pattern SHOULD generate error WHEN the does not match the provided regex pattern`() {
        // arrange
        class CompanyValidator : NoContextKvlValidator<Company>() {
            init {
                rulesFor(Company::name) { pattern("^Jack$") }
            }
        }
        val company = Company(name = "Jackson")

        // act
        val errors = CompanyValidator().validate(company)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(ValidationError(path = "name", message = "invalid format"))
        )
    }
}
