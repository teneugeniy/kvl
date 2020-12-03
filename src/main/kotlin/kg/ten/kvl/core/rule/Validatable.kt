package kg.ten.kvl.core.rule

import kg.ten.kvl.core.ValidationError

internal interface Validatable {
    fun validate(item: Any?, context: Any?, path: String): Collection<ValidationError>
}
