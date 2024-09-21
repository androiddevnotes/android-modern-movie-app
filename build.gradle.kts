plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.detekt)
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.compose) apply false
}

allprojects {
  apply(plugin = "io.gitlab.arturbosch.detekt")

  detekt {
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
  }
}

tasks.register("detektAll") {
  dependsOn(subprojects.map { "${it.path}:detekt" })
}
