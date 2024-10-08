val apiVersion = fetchProperty("version.api", "invalid")
val coreVersion = fetchProperty("version.core", "invalid")
val mavenUsername = fetchEnv("MAVEN_DEPLOY_USR", "mavenUsernameSirBlobman", "")
val mavenPassword = fetchEnv("MAVEN_DEPLOY_PSW", "mavenPasswordSirBlobman", "")

rootProject.ext.set("apiVersion", apiVersion)
rootProject.ext.set("coreVersion", coreVersion)
rootProject.ext.set("mavenUsername", mavenUsername)
rootProject.ext.set("mavenPassword", mavenPassword)

val baseVersion = fetchProperty("version.base", "invalid")
val betaString = fetchProperty("version.beta", "false")
val jenkinsBuildNumber = fetchEnv("BUILD_NUMBER", null, "Unofficial")

val betaBoolean = betaString.toBoolean()
val betaVersion = if (betaBoolean) "Beta-" else ""
val calculatedVersion = "$baseVersion.$betaVersion$jenkinsBuildNumber"
rootProject.ext.set("calculatedVersion", calculatedVersion)

fun fetchProperty(propertyName: String, defaultValue: String): String {
    val found = findProperty(propertyName)
    if (found != null) {
        return found.toString()
    }

    return defaultValue
}

fun fetchEnv(envName: String, propertyName: String?, defaultValue: String): String {
    val found = System.getenv(envName)
    if (found != null) {
        return found
    }

    if (propertyName != null) {
        return fetchProperty(propertyName, defaultValue)
    }

    return defaultValue
}

plugins {
    id("java")
}

tasks.named("jar") {
    enabled = false
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://nexus.sirblobman.xyz/public/")
    }

    dependencies {
        // Java Dependencies
        compileOnly("org.jetbrains:annotations:26.0.0")

        // Spigot API
        val spigotVersion = property("version.spigot")
        compileOnly("org.spigotmc:spigot-api:$spigotVersion")

        // BlueSlimeCore
        compileOnly("com.github.sirblobman.api:core:$coreVersion")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xlint:deprecation")
    }
}
