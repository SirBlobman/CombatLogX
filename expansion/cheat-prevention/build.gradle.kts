import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":expansion:cheat-prevention:abstract"))
    implementation(project(":expansion:cheat-prevention:legacy"))
    implementation(project(":expansion:cheat-prevention:paper"))
    implementation(project(path = ":expansion:cheat-prevention:modern", configuration = "default"))
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
