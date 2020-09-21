package kg.ten.kvl.core.internal.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.internal.Validatable
import org.apache.commons.text.StringSubstitutor

internal class ConditionRuleImpl<T>(
    private val condition: (T) -> Boolean,
    private val rulesSet: RulesSet<T>
) : ConditionRule<T>, Validatable {

    private var withMessage: ((T) -> String)? = null

    override fun withMessage(vararg args: Pair<String, Any?>, message: (T) -> String): ConditionRule<T> {
        withMessage = { item ->
            val sub = StringSubstitutor(args.toMap(), "%(", ")")
            val messageTemplate = message.invoke(item)
            sub.replace(messageTemplate)
        }
        return this
    }

    override fun and(): RulesSet<T> {
        return rulesSet
    }

    override fun validate(item: Any?, path: String): Collection<ValidationError> {
        @Suppress("UNCHECKED_CAST")
        return if (condition.invoke(item as T).not()) {
            listOf(ValidationError(path = path, message = withMessage?.invoke(item) ?: ""))
        } else {
            emptyList()
        }
    }
}
