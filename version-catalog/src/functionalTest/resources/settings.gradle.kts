import io.spine.internal.catalog.SpineVersionCatalog

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
        SpineVersionCatalog.useIn(this)
    }
}
