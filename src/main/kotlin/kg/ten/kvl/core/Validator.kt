package kg.ten.kvl.core

interface Validator<T, TContext> {
    fun validate(item: T, context: TContext): Collection<ValidationError>
}
