package kg.ten.kvl.core.api

import kg.ten.kvl.core.Validator

interface RulesSet<T> {
    fun must(condition: (T) -> Boolean): ConditionRule<T>
    fun applyValidator(validator: Validator<T>): RulesSet<T>
}
