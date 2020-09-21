package kg.ten.kvl.core.internal.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.Validator
import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.PropertyRulesSet
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.internal.Validatable
import kotlin.reflect.KProperty

internal class MapPropertyRulesSetImpl<TKey, TValue>(
    private val property: KProperty<Map<TKey, TValue?>>
) : PropertyRulesSet<Pair<TKey, TValue>>, Validatable {

    private val rules = mutableListOf<Validatable>()

    override fun must(condition: (Pair<TKey, TValue>) -> Boolean): ConditionRule<Pair<TKey, TValue>> {
        val cascadeRules = CascadeRules<Pair<TKey, TValue>>()
        val rule = cascadeRules.must(condition)
        rules.add(cascadeRules)
        return rule
    }

    override fun applyValidator(validator: Validator<Pair<TKey, TValue>>): RulesSet<Pair<TKey, TValue>> {
        val cascadeRules = CascadeRules<Pair<TKey, TValue>>()
        val rule = cascadeRules.applyValidator(validator)
        rules.add(cascadeRules)
        return rule
    }

    override fun <TSubProperty> rulesFor(property: KProperty<TSubProperty>, rulesSet: PropertyRulesSet<TSubProperty>.() -> Unit) {
        val propertyRulesSet = PropertyRulesSetImpl(property) { (it as Pair<*, *>).second }
        rules.add(propertyRulesSet)
        rulesSet.invoke(propertyRulesSet)
    }

    override fun validate(item: Any?, path: String): Collection<ValidationError> {
        val collectionPropertyValue = property.call(item)
        val propertyPath = if (path.isEmpty()) property.name else "$path.${property.name}"

        return collectionPropertyValue.toList().flatMap { propertyValue ->
            rules.flatMap { it.validate(propertyValue, "$propertyPath.${propertyValue.first}") }
        }
    }
}
