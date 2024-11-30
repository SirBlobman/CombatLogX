plugins {
    id("maven-publish")
}

java {
    withSourcesJar()
    withJavadocJar()
}

repositories {
    maven("https://repo.helpch.at/releases/")
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.6")
}

publishing {
    repositories {
        maven("https://nexus.sirblobman.xyz/public/") {
            credentials {
                username = rootProject.ext.get("mavenUsername") as String
                password = rootProject.ext.get("mavenPassword") as String
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.sirblobman.combatlogx"
            artifactId = "api"
            version = rootProject.ext.get("apiVersion") as String
            from(components["java"])
        }
    }
}

tasks.withType<Javadoc> {
    val standardOptions = (options as StandardJavadocDocletOptions)
    standardOptions.addStringOption("Xdoclint:none", "-quiet")
}
