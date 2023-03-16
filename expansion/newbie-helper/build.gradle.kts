plugins {
    id("maven-publish")
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            name = "sirblobman-public"
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
            artifactId = "newbie-helper"
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
