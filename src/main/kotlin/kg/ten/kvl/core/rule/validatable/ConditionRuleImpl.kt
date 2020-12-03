package kg.ten.kvl.core.rule.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.rule.Validatable
import org.apache.commons.text.StringSubstitutor

internal class ConditionRuleImpl<T, TContext>(
    private val condition: (T, TContext) -> Boolean,
    private val rulesSet: RulesSet<T, TContext>
) : ConditionRule<T, TContext>, Validatable {

    private var withMessage: ((T, TContext) -> String)? = null

    override fun message(vararg args: Pair<String, Any?>, message: (T, TContext) -> String): ConditionRule<T, TContext> {
        withMessage = { item, context ->
            val sub = StringSubstitutor(args.toMap(), "%(", ")")
            val messageTemplate = message.invoke(item, context)
            sub.replace(messageTemplate)
        }
        return this
    }

    override fun and(): RulesSet<T, TContext> {
        return rulesSet
    }

    override fun validate(item: Any?, context: Any?, path: String): Collection<ValidationError> {
        @Suppress("UNCHECKED_CAST")
        return if (condition.invoke(item as T, context as TContext).not()) {
            listOf(ValidationError(path = path, message = withMessage?.invoke(item, context) ?: ""))
        } else {
            emptyList()
        }
    }
}
