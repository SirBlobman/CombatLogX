allprojects {
    group = "com.github.sirblobman.combatlogx.expansion"

    dependencies {
        compileOnly(project(":api"))
    }

    tasks {
        named<Jar>("jar") {
            val expansionName = findProperty("expansion.name") ?: project.name
            archiveFileName.set("$expansionName.jar")
        }
    }
}
