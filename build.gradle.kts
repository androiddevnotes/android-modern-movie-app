plugins {
  id("com.android.application") version "8.1.0" apply false
  id("org.jetbrains.kotlin.android") version "1.9.0" apply false
  id("io.gitlab.arturbosch.detekt") version "1.23.1"
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
