java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("dev.espi:protectionstones:2.10.2")
    compileOnly("com.sk89q.worldguard:worldguard-core:7.1.0-SNAPSHOT")
}
