fun getEnvOrProp(variableName: String, propertyName: String): String {
    val environmentProvider = providers.environmentVariable(variableName)
    val propertyProvider = providers.gradleProperty(propertyName)
    return environmentProvider.orElse(propertyProvider).orElse("").get()
}

fun getProp(propertyName: String): String {
    val propertyProvider = providers.gradleProperty(propertyName)
    return propertyProvider.get()
}

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
        maven("https://nexus.sirblobman.xyz/public") {
            credentials {
                username = getEnvOrProp("MAVEN_DEPLOY_USR", "maven.username.sirblobman")
                password = getEnvOrProp("MAVEN_DEPLOY_PSW", "maven.password.sirblobman")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.sirblobman.combatlogx"
            artifactId = "api"
            version = getProp("version.api")
            from(components["java"])
        }
    }
}

tasks.withType<Javadoc> {
    val standardOptions = (options as StandardJavadocDocletOptions)
    standardOptions.addStringOption("Xdoclint:none", "-quiet")
}
