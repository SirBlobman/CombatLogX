import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation("net.jodah:expiringmap:0.5.10")
}

tasks {
    named<Jar>("jar") {
        enabled = false
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set(null as String?)
        val expansionName = findProperty("expansion.name") ?: project.name
        archiveFileName.set("$expansionName.jar")

        relocate("net.jodah.expiringmap", "combatlogx.expansion.loot.protection.expiringmap")
    }

    build {
        dependsOn(shadowJar)
    }
}
