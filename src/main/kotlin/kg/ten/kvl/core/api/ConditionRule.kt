package kg.ten.kvl.core.api

interface ConditionRule<T, TContext> {
    fun message(vararg args: Pair<String, Any?>, message: (T, TContext) -> String): ConditionRule<T, TContext>
    fun and(): RulesSet<T, TContext>
}
