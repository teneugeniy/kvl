package kg.ten.kvl.core.fluent

import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.fluent.nocontext.NoContextValidator

fun <T> RulesSet<T, *>.must(condition: (T) -> Boolean): ConditionRule<T, *> = mustBe { item, _ -> condition(item) }

fun <T> RulesSet<T, Unit>.validator(validator: NoContextValidator<T>): RulesSet<T, Unit> = applyValidator(validator) { _, _ -> Unit }
