package kg.ten.kvl.core.internal.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.Validator
import kg.ten.kvl.core.internal.Validatable

internal class ValidatorRule<T>(
    private val validator: Validator<T>
) : Validatable {

    override fun validate(item: Any?, path: String): Collection<ValidationError> {
        if (item == null)
            return emptyList()

        @Suppress("UNCHECKED_CAST")
        return validator.validate(item as T).onEach { it.setPrefix(path) }
    }
}
