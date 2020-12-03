package kg.ten.kvl.core.api

import kotlin.reflect.KProperty

interface PropertyRulesSet<T, TContext> : RulesSet<T, TContext> {
    fun <TSubProperty> rulesFor(property: KProperty<TSubProperty>, rulesSet: PropertyRulesSet<TSubProperty, TContext>.() -> Unit)
}
