package kg.ten.kvl.core.api

import kg.ten.kvl.core.Validator

interface RulesSet<T, TContext> {
    fun mustBe(condition: (T, TContext) -> Boolean): ConditionRule<T, TContext>
    fun <TValidatorContext> applyValidator(validator: Validator<T, TValidatorContext>, contextProvider: (T, TContext) -> TValidatorContext): RulesSet<T, TContext>
}
