package kg.ten.kvl.core.internal.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.Validator
import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.internal.Validatable

internal class ObjectRulesSetImpl<T> : RulesSet<T>, Validatable {

    private val rules = mutableListOf<Validatable>()

    override fun must(condition: (T) -> Boolean): ConditionRule<T> {
        val cascadeRules = CascadeRules<T>()
        val rule = cascadeRules.must(condition)
        rules.add(cascadeRules)
        return rule
    }

    override fun applyValidator(validator: Validator<T>): RulesSet<T> {
        val cascadeRules = CascadeRules<T>()
        val rule = cascadeRules.applyValidator(validator)
        rules.add(cascadeRules)
        return rule
    }

    override fun validate(item: Any?, path: String): Collection<ValidationError> {
        return rules.flatMap { it.validate(item, path) }
    }
}
