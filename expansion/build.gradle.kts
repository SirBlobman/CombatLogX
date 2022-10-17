allprojects {
    group = "com.github.sirblobman.combatlogx.expansion"
    val expansionName = (findProperty("expansion.name") ?: project.name) as String

    dependencies {
        compileOnly(project(":api"))
    }

    tasks {
        named<Jar>("jar") {
            archiveFileName.set("$expansionName.jar")
        }

        processResources {
            val expansionDescription = (findProperty("expansion.description") ?: "") as String

            filesMatching("expansion.yml") {
                filter {
                    it.replace("\${expansion.name}", expansionName)
                        .replace("\${project.description}", expansionDescription)
                }
            }
        }
    }
}
