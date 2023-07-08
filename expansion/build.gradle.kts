tasks.named("jar") {
    enabled = false
}

subprojects {
    val expansionName = findProperty("expansion.name") ?: "invalid"
    val expansionPrefix = findProperty("expansion.prefix") ?: expansionName

    dependencies {
        compileOnly(project(":api"))
    }

    tasks {
        named<Jar>("jar") {
            archiveFileName.set("$expansionPrefix.jar")
        }

        processResources {
            val expansionDescription = findProperty("expansion.description") ?: ""

            filesMatching("expansion.yml") {
                expand(
                    mapOf(
                        "expansionName" to expansionName,
                        "expansionPrefix" to expansionPrefix,
                        "expansionDescription" to expansionDescription
                    )
                )
            }
        }
    }
}
