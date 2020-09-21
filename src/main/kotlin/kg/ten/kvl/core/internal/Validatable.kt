package kg.ten.kvl.core.internal

import kg.ten.kvl.core.ValidationError

internal interface Validatable {
    fun validate(item: Any?, path: String): Collection<ValidationError>
}
