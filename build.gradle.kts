plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

subprojects {
    configurations.all {
        resolutionStrategy.force(
            "org.jetbrains.kotlin:kotlin-stdlib:2.1.0",
            "org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.0",
            "org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.1.0",
            "org.jetbrains.kotlin:kotlin-stdlib-common:2.1.0"
        )
    }
}
