plugins {
    id("maven-publish")
}

java {
    withSourcesJar()
    withJavadocJar()
}

repositories {
    maven {
        name = "placeholderapi"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.2")
}

publishing {
    repositories {
        maven {
            name = "sirblobman-public"
            url = uri("https://nexus.sirblobman.xyz/repository/public-snapshots/")

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
        options {
            this as StandardJavadocDocletOptions
            addStringOption("Xdoclint:none", "-quiet")
        }
    }
}
