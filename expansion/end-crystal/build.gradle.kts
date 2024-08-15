import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":expansion:end-crystal:legacy"))
    implementation(project(":expansion:end-crystal:modern"))
    implementation(project(":expansion:end-crystal:moderner"))
}

tasks {
    named("jar") {
        enabled = false
    }

    named<ShadowJar>("shadowJar") {
        val expansionName = findProperty("expansion.name") ?: "invalid"
        val expansionPrefix = findProperty("expansion.prefix") ?: expansionName
        archiveFileName.set("$expansionPrefix.jar")
        archiveClassifier.set(null as String?)
    }

    named("build") {
        dependsOn("shadowJar")
    }
}
