import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation("net.jodah:expiringmap:0.5.11")
}

tasks {
    named<Jar>("jar") {
        enabled = false
    }

    named<ShadowJar>("shadowJar") {
        val expansionName = findProperty("expansion.name") ?: project.name
        archiveFileName.set("$expansionName.jar")
        archiveClassifier.set(null as String?)

        relocate("net.jodah.expiringmap", "combatlogx.expansion.loot.protection.expiringmap")
    }

    named("build") {
        dependsOn("shadowJar")
    }
}
