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
                var currentUsername = System.getenv("MAVEN_DEPLOY_USERNAME")
                if(currentUsername == null) {
                    currentUsername = property("mavenUsernameSirBlobman") as String
                }

                var currentPassword = System.getenv("MAVEN_DEPLOY_PASSWORD")
                if (currentPassword == null) {
                    currentPassword = property("mavenPasswordSirBlobman") as String
                }

                username = currentUsername
                password = currentPassword
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
