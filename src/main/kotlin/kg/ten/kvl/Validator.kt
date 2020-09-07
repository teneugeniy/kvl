package kg.ten.kvl

import org.apache.commons.text.StringSubstitutor
import kotlin.reflect.KProperty

//self tests
//collection tests
//map tests
//property validator tests
//self validator tests
//cascade property validator tests

abstract class Validator<T> {

    private val propertiesRules = mutableListOf<Validatable>()

    protected fun <TProperty> ruleFor(property: KProperty<TProperty>, rules: PropertyRulesSet<TProperty>.() -> Unit) {
        val propertyRulesSet = PropertyRulesSetImpl(property)
        propertiesRules.add(propertyRulesSet)
        rules.invoke(propertyRulesSet)
    }

    fun validate(item: T): Collection<ValidationError> {
        return propertiesRules.flatMap { it.validate(item, "") }
    }
}



interface PropertyRulesSet<T> : RulesSet<T> {
    fun <TSubProperty> ruleFor(property: KProperty<TSubProperty>, rulesSet: PropertyRulesSet<TSubProperty>.() -> Unit)
}

interface ConditionRule<T> {
    fun withMessage(vararg args: Pair<String, Any?>, message: (T) -> String): ConditionRule<T>
    fun and(): RulesSet<T>
}

interface RulesSet<T> {
    fun must(condition: (T) -> Boolean): ConditionRule<T>
}




internal interface Validatable {
    fun validate(item: Any?, path: String): Collection<ValidationError>
}

internal class CascadeRules<T> : RulesSet<T>, Validatable {

    private val rules = mutableListOf<Validatable>()

    override fun must(condition: (T) -> Boolean): ConditionRule<T> {
        val rule = ConditionRuleImpl(condition, this)
        rules.add(rule)
        return rule
    }

    override fun validate(item: Any?, path: String): Collection<ValidationError> {
        for (rule in rules) {
            val errors = rule.validate(item, path)
            if (errors.isNotEmpty())
                return errors
        }

        return emptyList()
    }
}

internal class PropertyRulesSetImpl<T>(
    private val property: KProperty<T>
) : PropertyRulesSet<T>, Validatable {

    private val rules = mutableListOf<Validatable>()

    override fun must(condition: (T) -> Boolean): ConditionRule<T> {
        val cascadeRules = CascadeRules<T>()
        val rule = cascadeRules.must(condition)
        rules.add(cascadeRules)
        return rule
    }

    override fun <TSubProperty> ruleFor(property: KProperty<TSubProperty>, rulesSet: PropertyRulesSet<TSubProperty>.() -> Unit) {
        val propertyRulesSet = PropertyRulesSetImpl(property)
        rules.add(propertyRulesSet)
        rulesSet.invoke(propertyRulesSet)
    }

    override fun validate(item: Any?, path: String): Collection<ValidationError> {
        val propertyValue = property.call(item)
        val propertyPath = if (path.isEmpty()) property.name else "$path.${property.name}"
        return rules.flatMap { it.validate(propertyValue, propertyPath) }
    }
}

internal class ConditionRuleImpl<T>(
    private val condition: (T) -> Boolean,
    private val rulesSet: RulesSet<T>
): ConditionRule<T>, Validatable {

    private var withMessage: ((T) -> String)? = null

    override fun withMessage(vararg args: Pair<String, Any?>, message: (T) -> String): ConditionRule<T> {
        withMessage = { item ->
            val sub = StringSubstitutor(args.toMap(), "%(", ")")
            val messageTemplate = message.invoke(item)
            sub.replace(messageTemplate)
        }
        return this
    }

    override fun and(): RulesSet<T> {
        return rulesSet
    }

    override fun validate(item: Any?, path: String): Collection<ValidationError> {
        @Suppress("UNCHECKED_CAST")
        return if (condition.invoke(item as T).not()) {
            listOf(ValidationError(path = path, message = withMessage?.invoke(item) ?: ""))
        } else {
            emptyList()
        }
    }
}
