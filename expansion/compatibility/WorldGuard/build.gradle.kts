import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.0.0"
}

repositories {
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    // https://github.com/CodeMC/WorldGuardWrapper
    implementation("org.codemc.worldguardwrapper:worldguardwrapper:1.2.1-SNAPSHOT")
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

        val basePath = "combatlogx.expansion.compatibility.region.world.guard"
        relocate("org.codemc.worldguardwrapper", "$basePath.wrapper")
    }

    named("build") {
        dependsOn("shadowJar")
    }
}
