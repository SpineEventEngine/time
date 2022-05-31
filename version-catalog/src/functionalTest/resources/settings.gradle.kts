import io.spine.internal.catalog.SpineDependencies

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("io.spine.internal:spine-version-catalog:+")
    }
}

dependencyResolutionManagement.versionCatalogs {
    create("libs") {
        SpineDependencies.useIn(this)
    }
}
