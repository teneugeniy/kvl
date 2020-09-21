package kg.ten.kvl.core.api

import kotlin.reflect.KProperty

interface PropertyRulesSet<T> : RulesSet<T> {
    fun <TSubProperty> rulesFor(property: KProperty<TSubProperty>, rulesSet: PropertyRulesSet<TSubProperty>.() -> Unit)
}
