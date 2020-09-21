package kg.ten.kvl.core.internal.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.Validator
import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.PropertyRulesSet
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.internal.Validatable
import kotlin.reflect.KProperty

internal class CollectionPropertyRulesSetImpl<T>(
    private val property: KProperty<Collection<T>>
) : PropertyRulesSet<T>, Validatable {

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

    override fun <TSubProperty> rulesFor(property: KProperty<TSubProperty>, rulesSet: PropertyRulesSet<TSubProperty>.() -> Unit) {
        val propertyRulesSet = PropertyRulesSetImpl(property)
        rules.add(propertyRulesSet)
        rulesSet.invoke(propertyRulesSet)
    }

    override fun validate(item: Any?, path: String): Collection<ValidationError> {
        val collectionPropertyValue = property.call(item)
        val propertyPath = if (path.isEmpty()) property.name else "$path.${property.name}"

        return collectionPropertyValue.flatMapIndexed { index, propertyValue ->
            rules.flatMap { it.validate(propertyValue, "$propertyPath[$index]") }
        }
    }
}
