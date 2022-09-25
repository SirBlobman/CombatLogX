import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation(project(":expansion:cheat-prevention:abstract"))
    implementation(project(":expansion:cheat-prevention:legacy"))
    implementation(project(":expansion:cheat-prevention:paper"))
}

tasks {
    named<Jar>("jar") {
        enabled = false
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set(null as String?)
        val expansionName = findProperty("expansion.name") ?: project.name
        archiveFileName.set("$expansionName.jar")
    }

    build {
        dependsOn(shadowJar)
    }
}
