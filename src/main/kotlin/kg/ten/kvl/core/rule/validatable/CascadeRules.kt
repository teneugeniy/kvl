package kg.ten.kvl.core.rule.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.Validator
import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.rule.Validatable

internal class CascadeRules<T, TContext> : RulesSet<T, TContext>, Validatable {

    private val rules = mutableListOf<Validatable>()

    override fun mustBe(condition: (T, TContext) -> Boolean): ConditionRule<T, TContext> {
        val rule = ConditionRuleImpl(condition, this)
        rules.add(rule)
        return rule
    }

    override fun <TValidatorContext> applyValidator(validator: Validator<T, TValidatorContext>, contextProvider: (T, TContext) -> TValidatorContext): RulesSet<T, TContext> {
        @Suppress("UNCHECKED_CAST")
        rules.add(ValidatorRule(validator) { item, context -> contextProvider(item, context as TContext) })
        return this
    }

    override fun validate(item: Any?, context: Any?, path: String): Collection<ValidationError> {
        for (rule in rules) {
            val errors = rule.validate(item, context, path)
            if (errors.isNotEmpty())
                return errors
        }

        return emptyList()
    }
}
