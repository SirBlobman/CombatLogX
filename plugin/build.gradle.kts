import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

repositories {
    maven {
        name = "placeholderapi"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

dependencies {
    implementation(project(":api"))
    implementation("org.zeroturnaround:zt-zip:1.15")
    compileOnly("me.clip:placeholderapi:2.11.2")
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

    build {
        dependsOn(shadowJar)
    }

    processResources {
        val calculatedVersion = project.ext.get("calculatedVersion")

        filesMatching("plugin.yml") {
            val bukkitPluginName = rootProject.property("bukkit.plugin.name") as String
            val bukkitPluginPrefix = rootProject.property("bukkit.plugin.prefix") as String
            val bukkitPluginDescription = rootProject.property("bukkit.plugin.description") as String
            val bukkitPluginMain = rootProject.property("bukkit.plugin.main") as String

            filter<ReplaceTokens>("tokens" to mapOf(
                "bukkit.plugin.version" to calculatedVersion,
                "bukkit.plugin.name" to bukkitPluginName,
                "bukkit.plugin.prefix" to bukkitPluginPrefix,
                "bukkit.plugin.description" to bukkitPluginDescription,
                "bukkit.plugin.main" to bukkitPluginMain
            ))
        }

        filesMatching("config.yml") {
            filter<ReplaceTokens>("tokens" to mapOf(
                "bukkit.plugin.version" to calculatedVersion
            ))
        }
    }
}
