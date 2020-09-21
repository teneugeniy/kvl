package kg.ten.kvl.core

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
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
        private val companyValidator: Validator<Company>
    ) : KvlValidator<Employee>() {
        init {
            rulesFor(Employee::company) {
                applyValidator(companyValidator)
            }
        }
    }

    private lateinit var employeeValidator: EmployeeValidator

    private lateinit var companyValidator: Validator<Company>

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

        every { companyValidator.validate(company) } returns listOf(ValidationError(path = "error.path", message = ""))

        // act
        val errors = employeeValidator.validate(employee)

        // assert
        MatcherAssert.assertThat(
            errors,
            Matchers.containsInAnyOrder(ValidationError(path = "company.error.path", message = ""))
        )
    }
}
