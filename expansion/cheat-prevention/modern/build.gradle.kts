java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(project(":expansion:cheat-prevention:abstract"))
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

configurations {
    create("copy") {
        isCanBeConsumed = true
        isCanBeResolved = true
    }
}

artifacts {
    add("copy", layout.buildDirectory.file("libs/Cheat Prevention.jar")) {
        builtBy("copy")
    }
}

tasks {
    register("copy") {
        description = "Dummy task for copying the jar"
        dependsOn("jar")
    }
}
