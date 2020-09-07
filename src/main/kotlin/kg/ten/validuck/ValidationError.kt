package kg.ten.validuck

data class ValidationError(
    val path: String,
    val message: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }

        other as ValidationError
        if (path != other.path || message != other.message) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + path.hashCode()
        result = prime * result + message.hashCode()
        return result
    }
}
