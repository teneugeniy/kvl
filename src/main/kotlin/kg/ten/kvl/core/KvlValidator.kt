package kg.ten.kvl.core

import kg.ten.kvl.core.api.PropertyRulesSet
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.internal.Validatable
import kg.ten.kvl.core.internal.validatable.CollectionPropertyRulesSetImpl
import kg.ten.kvl.core.internal.validatable.MapPropertyRulesSetImpl
import kg.ten.kvl.core.internal.validatable.ObjectRulesSetImpl
import kg.ten.kvl.core.internal.validatable.PropertyRulesSetImpl
import kotlin.reflect.KProperty

interface Validator<T> {
    fun validate(item: T): Collection<ValidationError>
}

abstract class KvlValidator<T> : Validator<T> {

    private val rulesSets = mutableListOf<Validatable>()

    protected fun <TProperty> rulesFor(property: KProperty<TProperty>, rules: PropertyRulesSet<TProperty>.() -> Unit) {
        val propertyRulesSet = PropertyRulesSetImpl(property)
        rulesSets.add(propertyRulesSet)
        rules.invoke(propertyRulesSet)
    }

    protected fun rulesForThis(rules: RulesSet<T>.() -> Unit) {
        val objectRulesSet = ObjectRulesSetImpl<T>()
        rulesSets.add(objectRulesSet)
        rules.invoke(objectRulesSet)
    }

    protected fun <TProperty> rulesForEach(property: KProperty<Collection<TProperty>>, rules: PropertyRulesSet<TProperty>.() -> Unit) {
        val propertyRulesSet = CollectionPropertyRulesSetImpl(property)
        rulesSets.add(propertyRulesSet)
        rules.invoke(propertyRulesSet)
    }

    protected fun <TMapPropertyKey, TMapPropertyValue> rulesForMap(property: KProperty<Map<TMapPropertyKey, TMapPropertyValue?>>, rules: PropertyRulesSet<Pair<TMapPropertyKey, TMapPropertyValue>>.() -> Unit) {
        val propertyRulesSet = MapPropertyRulesSetImpl(property)
        rulesSets.add(propertyRulesSet)
        rules.invoke(propertyRulesSet)
    }

    override fun validate(item: T): Collection<ValidationError> {
        return rulesSets.flatMap { it.validate(item, "") }
    }
}
