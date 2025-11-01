val baseVersion = providers.gradleProperty("version.base").get()
val betaVersion = providers.gradleProperty("version.beta").get().toBooleanStrict()
val betaString = if (betaVersion) "Beta-" else ""
val jenkinsBuild = providers.environmentVariable("BUILD_NUMBER").orElse("Unofficial").get()
rootProject.version = "${baseVersion}.${betaString}${jenkinsBuild}"

plugins {
    id("java")
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://nexus.sirblobman.xyz/public/")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:26.0.2-1") // JetBrains Annotations
        compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT") // Base Spigot API
        compileOnly("com.github.sirblobman.api:core:2.9-SNAPSHOT") // BlueSlimeCore
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xlint:deprecation")
    }
}
