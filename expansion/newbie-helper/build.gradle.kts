plugins {
    id("maven-publish")
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
            artifactId = "newbie-helper"
            from(components["java"])
        }
    }
}
