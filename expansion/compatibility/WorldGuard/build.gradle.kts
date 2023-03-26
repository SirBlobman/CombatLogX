import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    implementation("org.codemc.worldguardwrapper:worldguardwrapper:1.2.0-SNAPSHOT")
}

tasks {
    named<Jar>("jar") {
        enabled = false
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set(null as String?)
        val expansionName = findProperty("expansion.name") ?: project.name
        archiveFileName.set("$expansionName.jar")

        relocate("org.codemc.worldguardwrapper", "combatlogx.expansion.compatibility.region.world.guard.wrapper")
    }

    build {
        dependsOn(shadowJar)
    }
}
