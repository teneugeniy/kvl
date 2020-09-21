package kg.ten.kvl.core

data class ValidationError(
    private var path: String,
    val message: String
) {
    val pathValue: String
        get() = path

    internal fun setPrefix(prefix: String) {
        path = prefix + if (path.isEmpty() || prefix.isEmpty()) "" else ".$path"
    }
}
