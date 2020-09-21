package kg.ten.kvl.core.api

interface ConditionRule<T> {
    fun withMessage(vararg args: Pair<String, Any?>, message: (T) -> String): ConditionRule<T>
    fun and(): RulesSet<T>
}
