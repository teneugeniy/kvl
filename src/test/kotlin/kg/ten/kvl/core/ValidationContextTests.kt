package kg.ten.kvl.core

import kg.ten.kvl.core.fluent.nocontext.NoContextKvlValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ValidationContextTests {

    class Document(
        val initiator: User
    )

    class DocumentValidator(
        private val userValidator: Validator<User, UserContext>
    ) : NoContextKvlValidator<Document>() {
        init {
            rulesFor(Document::initiator) {
                applyValidator(userValidator) { user, _ -> UserContext(user) }
            }
        }
    }

    class User(
        val name: String?,
        val email: String?
    )

    class UserContext(
        val user: User
    )

    class UserValidator : KvlValidator<User, UserContext>() {
        init {
            rulesFor(User::email) {
                mustBe { email, context -> context.user.name.isNullOrEmpty().not() || email.isNullOrEmpty().not() }
            }
        }
    }

    private lateinit var userValidator: UserValidator
    private lateinit var documentValidator: DocumentValidator

    @BeforeEach
    fun setUp() {
        userValidator = UserValidator()
        documentValidator = DocumentValidator(userValidator)
    }

    @Test
    fun `validate SHOULD pass WHEN the rules are not violated`() {
        // arrange
        val user = User(name = "John Smith", email = "john@smith.org")
        val userContext = UserContext(user)

        // act
        val errors = userValidator.validate(user, userContext)

        // assert
        assertThat(errors, empty())
    }

    @Test
    fun `validate SHOULD fail WHEN the context violates the rule`() {
        // arrange
        val user = User(name = "", email = null)
        val userContext = UserContext(user)

        // act
        val errors = userValidator.validate(user, userContext)

        // assert
        assertThat(
            errors,
            containsInAnyOrder(ValidationError(path = "email", message = ""))
        )
    }
}
