package kg.ten.kvl.core.fluent

import kg.ten.kvl.core.api.ConditionRule

fun <T, TContext> ConditionRule<T, TContext>.withMessage(vararg args: Pair<String, Any?>, message: (T) -> String): ConditionRule<T, TContext> {
    return this.message(* args) { item, _ -> message.invoke(item) }
}
