package kg.ten.kvl.core.rule.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.Validator
import kg.ten.kvl.core.rule.Validatable

internal class ValidatorRule<T, TContext>(
    private val validator: Validator<T, TContext>,
    private val contextProvider: (T, Any?) -> TContext
) : Validatable {

    override fun validate(item: Any?, context: Any?, path: String): Collection<ValidationError> {
        if (item == null)
            return emptyList()

        @Suppress("UNCHECKED_CAST")
        val validatorContext = contextProvider.invoke(item as T, context)
        return validator.validate(item, validatorContext).onEach { it.setPrefix(path) }
    }
}
