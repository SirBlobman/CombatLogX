import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.2.2"
    id("maven-publish")
}

repositories {
    maven("https://repo.helpch.at/releases/")
}

dependencies {
    // Local Dependencies
    implementation(project(":api"))

    // Java Dependencies
    implementation("org.zeroturnaround:zt-zip:1.17") // ZT Zip

    // Plugin Dependencies
    compileOnly("me.clip:placeholderapi:2.11.6") // PlaceholderAPI
}

tasks {
    named<Jar>("jar") {
        enabled = false
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set(null as String?)
        archiveFileName.set("CombatLogX.jar")

        relocate("org.zeroturnaround.zip", "com.github.sirblobman.combatlogx.zip")
        relocate("org.slf4j", "com.github.sirblobman.combatlogx.zip.slf4j")
    }

    named("build") {
        dependsOn("shadowJar")
    }

    processResources {
        val calculatedVersion = rootProject.ext.get("calculatedVersion") as String
        val pluginName = (findProperty("bukkit.plugin.name") ?: "") as String
        val pluginPrefix = (findProperty("bukkit.plugin.prefix") ?: "") as String
        val pluginDescription = (findProperty("bukkit.plugin.description") ?: "") as String
        val pluginWebsite = (findProperty("bukkit.plugin.website") ?: "") as String
        val pluginMainClass = (findProperty("bukkit.plugin.main") ?: "") as String

        filesMatching("plugin.yml") {
            expand(
                mapOf(
                    "pluginName" to pluginName,
                    "pluginPrefix" to pluginPrefix,
                    "pluginDescription" to pluginDescription,
                    "pluginWebsite" to pluginWebsite,
                    "pluginMainClass" to pluginMainClass,
                    "pluginVersion" to calculatedVersion
                )
            )
        }

        filesMatching("config.yml") {
            expand(mapOf("pluginVersion" to calculatedVersion))
        }
    }
}
