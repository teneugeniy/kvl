package kg.ten.kvl.core.rule.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.Validator
import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.rule.Validatable

internal class ObjectRulesSetImpl<T, TContext> : RulesSet<T, TContext>, Validatable {

    private val rules = mutableListOf<Validatable>()

    override fun mustBe(condition: (T, TContext) -> Boolean): ConditionRule<T, TContext> {
        val cascadeRules = CascadeRules<T, TContext>()
        val rule = cascadeRules.mustBe(condition)
        rules.add(cascadeRules)
        return rule
    }

    override fun <TValidatorContext> applyValidator(validator: Validator<T, TValidatorContext>, contextProvider: (T, TContext) -> TValidatorContext): RulesSet<T, TContext> {
        val cascadeRules = CascadeRules<T, TContext>()
        val rule = cascadeRules.applyValidator(validator, contextProvider)
        rules.add(cascadeRules)
        return rule
    }

    override fun validate(item: Any?, context: Any?, path: String): Collection<ValidationError> {
        return rules.flatMap { it.validate(item, context, path) }
    }
}
