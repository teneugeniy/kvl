package kg.ten.kvl.core

import io.mockk.every
import io.mockk.mockk
import kg.ten.kvl.core.fluent.nocontext.NoContextKvlValidator
import kg.ten.kvl.core.fluent.nocontext.NoContextValidator
import kg.ten.kvl.core.fluent.validator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ApplyValidatorTests {

    class Employee(
        val company: Company
    )

    class Company(
        val name: String
    )

    class EmployeeValidator(
        private val companyValidator: NoContextValidator<Company>
    ) : NoContextKvlValidator<Employee>() {
        init {
            rulesFor(Employee::company) {
                validator(companyValidator)
            }
        }
    }

    private lateinit var employeeValidator: EmployeeValidator

    private lateinit var companyValidator: NoContextValidator<Company>

    @BeforeEach
    fun setUp() {
        companyValidator = mockk()
        employeeValidator = EmployeeValidator(companyValidator)
    }

    @Test
    fun `validate SHOULD use nested validator to check the property`() {
        // arrange
        val company = Company(name = "ABC")
        val employee = Employee(company = company)

        every { companyValidator.validate(company, Unit) } returns listOf(ValidationError(path = "error.path", message = ""))

        // act
        val errors = employeeValidator.validate(employee)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(ValidationError(path = "company.error.path", message = ""))
        )
    }
}
