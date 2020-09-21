# Kotlin Validation Library
> shortly KVL or kvl is implemented to provide an easy fluent DSL that would support abstraction, be easily testable and supported by all popular DI frameworks

[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io) 

## Examples
```kotlin
data class User(val fullName: String, val email: String) 

class UserValidator : KvlValidator<User> {
    init {
        rulesFor(User::fullName) {
            must { it.notBlank }
                .and()
                .must { it.length < 200 }
        }
        rulesFor(User::email) {
            must { it.notBlank }
            must { it.isEmail() }
        }
    }
}
```