import org.gradle.api.internal.catalog.ExternalModuleDependencyFactory
import org.gradle.api.internal.catalog.ExternalModuleDependencyFactory.BundleNotationSupplier
import org.gradle.api.internal.catalog.ExternalModuleDependencyFactory.VersionNotationSupplier

fun assert(actual: ExternalModuleDependencyFactory.DependencyNotationSupplier, expected: String) =
    assert(actual.asProvider(), expected)

fun assert(actual: VersionNotationSupplier, expected: String) = assert(actual.asProvider(), expected)

fun assert(actual: BundleNotationSupplier, expected: String) = assert(actual.asProvider(), expected)

fun assert(actual: Provider<*>, expected: String) = assert(actual.get().toString(), expected)

fun assert(actual: String, expected: String) {
    if (actual != expected) {
        throw IllegalStateException(
            "\nAssertion failed:" +
                    "\nExpected: $expected" +
                    "\n  Actual: $actual"
        )
    }
}

val group = "org.dummy.company"

assert(libs.versions.dummy, "1.0.0")
assert(libs.dummy, "$group:dummy-lib:1.0.0")

with(libs.dummy) {
    assert(core, "$group:dummy-core:1.0.0")
    assert(runner, "$group:dummy-runner:1.0.0")
    assert(api, "$group:dummy-api:1.0.0")
    assert(params, "$group:dummy-params:1.0.0")
    assert(types, "$group:dummy-types:1.0.0")
}

assert(
    libs.bundles.dummy, "[" +
            "org.dummy.company:dummy-core:1.0.0, " +
            "org.dummy.company:dummy-runner:1.0.0, " +
            "org.dummy.company:dummy-api:1.0.0, " +
            "org.dummy.company:dummy-params:1.0.0, " +
            "org.dummy.company:dummy-types:1.0.0" +
            "]"
)

assert(libs.versions.dummy.gradlePlugin, "0.0.8")
assert(libs.dummy.gradlePlugin, "$group:my-dummy-plugin:0.0.8")
assert(libs.plugins.dummy, "my-dummy-plugin:0.0.8")

assert(libs.versions.dummy.runtime, "1.0.0")
assert(libs.versions.dummy.runtime.bom, "2.0.0")
with(libs.dummy.runtime) {
    assert(win, "$group:runtime-win:1.0.0")
    assert(mac, "$group:runtime-mac:1.0.0")
    assert(linux, "$group:runtime-linux:1.0.0")
    assert(bom, "$group:dummy-bom:2.0.0")
}

assert(
    libs.bundles.dummy.runtime,
    "[" +
            "org.dummy.company:dummy-bom:2.0.0, " +
            "org.dummy.company:runtime-win:1.0.0, " +
            "org.dummy.company:runtime-mac:1.0.0, " +
            "org.dummy.company:runtime-linux:1.0.0" +
            "]"
)

assert(libs.versions.dummy.tools, "3.0.0")
