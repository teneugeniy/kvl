package kg.ten.kvl.core.rule.validatable

import kg.ten.kvl.core.ValidationError
import kg.ten.kvl.core.Validator
import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.PropertyRulesSet
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.rule.Validatable
import kotlin.reflect.KProperty

internal class CollectionPropertyRulesSetImpl<T, TContext>(
    private val property: KProperty<Collection<T>>
) : PropertyRulesSet<T, TContext>, Validatable {

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

    override fun <TSubProperty> rulesFor(property: KProperty<TSubProperty>, rulesSet: PropertyRulesSet<TSubProperty, TContext>.() -> Unit) {
        val propertyRulesSet = PropertyRulesSetImpl<TSubProperty, TContext>(property)
        rules.add(propertyRulesSet)
        rulesSet.invoke(propertyRulesSet)
    }

    override fun validate(item: Any?, context: Any?, path: String): Collection<ValidationError> {
        val collectionPropertyValue = property.call(item)
        val propertyPath = if (path.isEmpty()) property.name else "$path.${property.name}"

        return collectionPropertyValue.flatMapIndexed { index, propertyValue ->
            rules.flatMap { it.validate(propertyValue, context, "$propertyPath[$index]") }
        }
    }
}
