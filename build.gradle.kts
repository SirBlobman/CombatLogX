plugins {
    id("java")
}

val jenkinsBuildNumber = System.getenv("BUILD_NUMBER") ?: "Unknown"
val baseVersion = findProperty("version.base") as String
val betaVersionString = (findProperty("version.beta") ?: "false") as String
val betaVersion = betaVersionString.toBoolean()
val betaVersionPart = if (betaVersion) "Beta-" else ""

val calculatedVersion = "$baseVersion.$betaVersionPart$jenkinsBuildNumber"
rootProject.ext.set("calculatedVersion", calculatedVersion)

val mavenDeployUsername = System.getenv("MAVEN_DEPLOY_USR") ?: findProperty("mavenUsernameSirBlobman") ?: ""
rootProject.ext.set("mavenUsername", mavenDeployUsername)

val mavenDeployPassword = System.getenv("MAVEN_DEPLOY_PSW") ?: findProperty("mavenPasswordSirBlobman") ?: ""
rootProject.ext.set("mavenPassword", mavenDeployPassword)

allprojects {
    group = "com.github.sirblobman.combatlogx"
    version = findProperty("version.api") as String

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

        compileOnly("org.jetbrains:annotations:24.0.0")
        compileOnly("org.spigotmc:spigot-api:$spigotVersion")
        compileOnly("com.github.sirblobman.api:core:$coreVersion")
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }
    }
}
