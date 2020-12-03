package kg.ten.kvl.core.fluent.nocontext

import kg.ten.kvl.core.KvlValidator
import kg.ten.kvl.core.Validator

interface NoContextValidator<T> : Validator<T, Unit> {
    fun validate(item: T) = validate(item, Unit)
}

abstract class NoContextKvlValidator<T> : NoContextValidator<T>, KvlValidator<T, Unit>()
