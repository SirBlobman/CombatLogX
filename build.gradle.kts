val baseVersion = providers.gradleProperty("version.base").get()
val betaVersion = providers.gradleProperty("version.beta").get().toBooleanStrict()
val betaString = if (betaVersion) "Beta-" else ""
val jenkinsBuild = providers.environmentVariable("BUILD_NUMBER").orElse("Unofficial").get()
rootProject.version = "${baseVersion}.${betaString}${jenkinsBuild}"

plugins {
    id("java")
}

subprojects {
    apply {
        plugin("java")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    }

    repositories {
        maven("https://nexus.sirblobman.xyz/central-mirror/") // Central Mirror
        maven("https://repo.aikar.co/nexus/content/groups/aikar/") // Aikar Repository
        maven("https://nexus.sirblobman.xyz/public/") // SirBlobman Public Repository
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:26.1.0") // JetBrains Annotations
        compileOnly("com.github.sirblobman.api:core:2.9-SNAPSHOT") // BlueSlimeCore
        compileOnly("org.spigotmc:spigot-api") // Spigot API

        // Spigot API
        constraints {
            compileOnly("org.spigotmc:spigot-api") {
                version {
                    prefer("1.19.4-R0.1-SNAPSHOT")
                }
            }
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xlint:deprecation")
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isReproducibleFileOrder = true
        isPreserveFileTimestamps = false
    }
}
