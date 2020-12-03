package kg.ten.kvl.core.rule.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.Validator
import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.PropertyRulesSet
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.rule.Validatable
import kotlin.reflect.KProperty

internal class MapPropertyRulesSetImpl<TKey, TValue, TContext>(
    private val property: KProperty<Map<TKey, TValue?>>
) : PropertyRulesSet<Pair<TKey, TValue>, TContext>, Validatable {

    private val rules = mutableListOf<Validatable>()

    override fun mustBe(condition: (Pair<TKey, TValue>, TContext) -> Boolean): ConditionRule<Pair<TKey, TValue>, TContext> {
        val cascadeRules = CascadeRules<Pair<TKey, TValue>, TContext>()
        val rule = cascadeRules.mustBe(condition)
        rules.add(cascadeRules)
        return rule
    }

    override fun <TValidatorContext> applyValidator(validator: Validator<Pair<TKey, TValue>, TValidatorContext>, contextProvider: (Pair<TKey, TValue>, TContext) -> TValidatorContext): RulesSet<Pair<TKey, TValue>, TContext> {
        val cascadeRules = CascadeRules<Pair<TKey, TValue>, TContext>()
        val rule = cascadeRules.applyValidator(validator, contextProvider)
        rules.add(cascadeRules)
        return rule
    }

    override fun <TSubProperty> rulesFor(property: KProperty<TSubProperty>, rulesSet: PropertyRulesSet<TSubProperty, TContext>.() -> Unit) {
        val propertyRulesSet = PropertyRulesSetImpl<TSubProperty, TContext>(property) { (it as Pair<*, *>).second }
        rules.add(propertyRulesSet)
        rulesSet.invoke(propertyRulesSet)
    }

    override fun validate(item: Any?, context: Any?, path: String): Collection<ValidationError> {
        val collectionPropertyValue = property.call(item)
        val propertyPath = if (path.isEmpty()) property.name else "$path.${property.name}"

        return collectionPropertyValue.toList().flatMap { propertyValue ->
            rules.flatMap { it.validate(propertyValue, context, "$propertyPath.${propertyValue.first}") }
        }
    }
}
