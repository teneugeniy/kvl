package kg.ten.kvl.core

import kg.ten.kvl.core.api.PropertyRulesSet
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.rule.Validatable
import kg.ten.kvl.core.rule.validatable.CollectionPropertyRulesSetImpl
import kg.ten.kvl.core.rule.validatable.MapPropertyRulesSetImpl
import kg.ten.kvl.core.rule.validatable.ObjectRulesSetImpl
import kg.ten.kvl.core.rule.validatable.PropertyRulesSetImpl
import kotlin.reflect.KProperty

abstract class KvlValidator<T, TContext> : Validator<T, TContext> {

    private val rulesSets = mutableListOf<Validatable>()

    protected fun <TProperty> rulesFor(property: KProperty<TProperty>, rules: PropertyRulesSet<TProperty, TContext>.() -> Unit) {
        val propertyRulesSet = PropertyRulesSetImpl<TProperty, TContext>(property)
        rulesSets.add(propertyRulesSet)
        rules.invoke(propertyRulesSet)
    }

    protected fun rulesForThis(rules: RulesSet<T, TContext>.() -> Unit) {
        val objectRulesSet = ObjectRulesSetImpl<T, TContext>()
        rulesSets.add(objectRulesSet)
        rules.invoke(objectRulesSet)
    }

    protected fun <TProperty> rulesForEach(property: KProperty<Collection<TProperty>>, rules: PropertyRulesSet<TProperty, TContext>.() -> Unit) {
        val propertyRulesSet = CollectionPropertyRulesSetImpl<TProperty, TContext>(property)
        rulesSets.add(propertyRulesSet)
        rules.invoke(propertyRulesSet)
    }

    protected fun <TMapPropertyKey, TMapPropertyValue> rulesForMap(property: KProperty<Map<TMapPropertyKey, TMapPropertyValue?>>, rules: PropertyRulesSet<Pair<TMapPropertyKey, TMapPropertyValue>, TContext>.() -> Unit) {
        val propertyRulesSet = MapPropertyRulesSetImpl<TMapPropertyKey, TMapPropertyValue, TContext>(property)
        rulesSets.add(propertyRulesSet)
        rules.invoke(propertyRulesSet)
    }

    override fun validate(item: T, context: TContext): Collection<ValidationError> {
        return rulesSets.flatMap { it.validate(item, context, "") }
    }
}
