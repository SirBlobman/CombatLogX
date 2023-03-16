plugins {
    id("maven-publish")
}

java {
    withSourcesJar()
    withJavadocJar()
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.2")
}

publishing {
    repositories {
        maven {
            url = uri("https://nexus.sirblobman.xyz/public/")

            credentials {
                username = rootProject.ext.get("mavenUsername") as String
                password = rootProject.ext.get("mavenPassword") as String
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "$group"
            artifactId = "api"
            from(components["java"])
        }
    }
}

tasks {
    javadoc {
        val standardOptions = (options as StandardJavadocDocletOptions)
        standardOptions.addStringOption("Xdoclint:none", "-quiet")
    }
}
