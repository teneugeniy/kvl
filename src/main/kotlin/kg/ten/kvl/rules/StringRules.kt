package kg.ten.kvl.rules

import kg.ten.kvl.core.api.ConditionRule
import kg.ten.kvl.core.api.RulesSet
import kg.ten.kvl.core.fluent.withMessage

fun <TContext> RulesSet<String, TContext>.isNotEmpty(): ConditionRule<String, TContext> {
    return this.mustBe { item, _ -> item.isNotEmpty() }.withMessage { "must not be empty" }
}

fun <TContext> RulesSet<String, TContext>.isMin(min: Int): ConditionRule<String, TContext> {
    return this.mustBe { item, _ -> item.length >= min }.withMessage { "must not be shorter than $min" }
}

fun <TContext> RulesSet<String, TContext>.isMax(max: Int): ConditionRule<String, TContext> {
    return this.mustBe { item, _ -> item.length <= max }.withMessage { "must not be longer than $max" }
}

fun <TContext> RulesSet<String, TContext>.size(min: Int, max: Int): ConditionRule<String, TContext> {
    return this.mustBe { item, _ -> item.length in min..max }.withMessage { "must be between $min and $max" }
}

// as per https://owasp.org/www-community/OWASP_Validation_Regex_Repository
private val EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$".toRegex()
fun <TContext> RulesSet<String, TContext>.isEmail(): ConditionRule<String, TContext> {
    return this.mustBe { item, _ -> EMAIL_REGEX.matches(item) }.withMessage { "must be an email address" }
}

fun <TContext> RulesSet<String, TContext>.pattern(pattern: String): ConditionRule<String, TContext> {
    return this.mustBe { item, _ -> pattern.toRegex().matches(item) }.withMessage { "invalid format" }
}
