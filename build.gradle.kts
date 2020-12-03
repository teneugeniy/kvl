plugins {
    kotlin("jvm") version "1.4.0"
    jacoco
}

group "kg.ten.kvl"
version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktlint: Configuration by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:0.38.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.0")
    implementation("org.apache.commons:commons-text:1.9")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    testImplementation("io.mockk:mockk:1.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

tasks.test {
    useJUnitPlatform()
}

val ktlintCheck by tasks.creating(JavaExec::class) {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("src/**/*.kt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
    group = "formatting"
    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "src/**/*.kt")
}

tasks {
    jacocoTestReport {
        dependsOn(test)
        doLast {
            println("View code coverage at: $buildDir/reports/jacoco/test/html/index.html")
        }
    }

    jacocoTestCoverageVerification {
        dependsOn(jacocoTestReport)
        violationRules {
            rule { limit { minimum = 0.8.toBigDecimal() } }
        }
    }

    check {
        dependsOn(ktlintCheck)
        dependsOn(jacocoTestCoverageVerification)
    }
}
