package kg.ten.kvl.core.internal.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.Validator
import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.internal.Validatable

internal class CascadeRules<T> : RulesSet<T>, Validatable {

    private val rules = mutableListOf<Validatable>()

    override fun must(condition: (T) -> Boolean): ConditionRule<T> {
        val rule = ConditionRuleImpl(condition, this)
        rules.add(rule)
        return rule
    }

    override fun applyValidator(validator: Validator<T>): RulesSet<T> {
        rules.add(ValidatorRule(validator))
        return this
    }

    override fun validate(item: Any?, path: String): Collection<ValidationError> {
        for (rule in rules) {
            val errors = rule.validate(item, path)
            if (errors.isNotEmpty())
                return errors
        }

        return emptyList()
    }
}
