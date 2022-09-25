plugins {
    id("java")
}

allprojects {
    group = "com.github.sirblobman.combatlogx"
    version = "11.0.0.0-SNAPSHOT"

    val jenkinsBuildNumber = System.getenv("BUILD_NUMBER") ?: "Unknown"
    val baseVersion = rootProject.property("version.base") as String
    val betaVersionString = rootProject.property("version.beta") as String
    val betaVersion = betaVersionString.toBoolean()

    var calculatedVersion = ("$baseVersion.")
    if (betaVersion) {
        calculatedVersion += "Beta-"
    }

    calculatedVersion += jenkinsBuildNumber

    project.ext.set("calculatedVersion", calculatedVersion)

    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenCentral()

        maven {
            name = "sirblobman-public"
            url = uri("https://nexus.sirblobman.xyz/repository/public/")
        }

        maven {
            name = "spigot-repo"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }

        maven {
            name = "oss-sonatype-snapshots"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }

    dependencies {
        val spigotVersion = property("spigot.version") as String
        val coreVersion = property("blue.slime.core.version") as String

        compileOnly("org.jetbrains:annotations:23.0.0")
        compileOnly("org.spigotmc:spigot-api:$spigotVersion")
        compileOnly("com.github.sirblobman.api:core:$coreVersion")
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }
    }
}
